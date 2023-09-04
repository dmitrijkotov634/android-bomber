package com.dm.bomber.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.dm.bomber.BuildConfig
import com.dm.bomber.BuildVars
import com.dm.bomber.R
import com.dm.bomber.services.Services
import com.dm.bomber.services.collectAll
import com.dm.bomber.services.filter
import com.dm.bomber.ui.stories.Story
import com.dm.bomber.worker.AttackWorker
import com.dm.bomber.worker.DownloadWorker
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min

class MainViewModel(
    private val repository: MainRepository,
    private val workManager: WorkManager
) : ViewModel() {
    private var services: Services = listOf()

    private var currentWorkId: UUID? = null

    val proxyEnabled: MutableLiveData<Boolean> = MutableLiveData(repository.isProxyEnabled)
    val snowfallEnabled: MutableLiveData<Boolean> = MutableLiveData(repository.isSnowfallEnabled)

    val progress = MutableLiveData(Progress(R.drawable.logo, R.string.attack))
    val workStatus = MutableLiveData(false)

    val updates = MutableLiveData<Updates?>()
    val advertising = MutableLiveData<DocumentSnapshot>()
    val stories = MutableLiveData<List<Story>>()

    val servicesCount = MutableLiveData(0)

    val repositoriesProgress = MutableLiveData(RepositoriesLoadingProgress(0, 0))

    private val advertisingCounter = MutableLiveData(5)
    private val advertisingAvailable = MutableLiveData(false)
    val advertisingTrigger = MutableLiveData(false)

    private var counter: Job? = null

    private var lastCountryCode = BuildVars.COUNTRY_CODES[0]

    val scheduledAttacks: LiveData<List<WorkInfo>>

    private val client = OkHttpClient()

    data class RepositoriesLoadingProgress(
        val currentProgress: Int,
        val maxProgress: Int
    )

    data class Progress(
        val currentProgress: Int,
        val maxProgress: Int,
        val iconResource: Int,
        val titleResource: Int
    ) {
        constructor(iconResource: Int, titleResource: Int) : this(0, 0, iconResource, titleResource)
    }

    init {
        workManager.getWorkInfosLiveData(
            WorkQuery.Builder.fromStates(
                listOf(
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.CANCELLED,
                    WorkInfo.State.SUCCEEDED,
                    WorkInfo.State.FAILED
                )
            )
                .build()
        ).observeForever { workInfoList: List<WorkInfo> ->
            for (workInfo in workInfoList)
                if (workInfo.id == currentWorkId) {
                    if (workInfo.state.isFinished)
                        workStatus.value = false

                    val data = workInfo.progress

                    for (tag in workInfo.tags) {
                        when (tag) {
                            ATTACK -> progress.setValue(
                                Progress(
                                    data.getInt(KEY_PROGRESS, 0),
                                    data.getInt(KEY_MAX_PROGRESS, 0),
                                    R.drawable.logo,
                                    R.string.attack
                                )
                            )

                            UPDATE -> progress.setValue(
                                Progress(
                                    data.getInt(KEY_PROGRESS, 0),
                                    data.getInt(KEY_MAX_PROGRESS, 0),
                                    R.drawable.ic_baseline_download_24,
                                    R.string.update
                                )
                            )
                        }
                    }
                }
        }

        scheduledAttacks = workManager.getWorkInfosLiveData(
            WorkQuery.Builder.fromStates(
                listOf(
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.ENQUEUED
                )
            )
                .addTags(listOf(ATTACK))
                .build()
        )

        selectCountryCode(lastCountryCode)

        collectAll()

        loadAdvertising()
        loadCloudStatic()
    }

    fun getAdvertisingCounter(): LiveData<Int> {
        return advertisingCounter
    }

    fun getAdvertisingAvailable(): LiveData<Boolean?> {
        return advertisingAvailable
    }

    fun startCounting() {
        val snapshot = advertising.getValue()
        if (counter?.isActive == true) return
        counter = CoroutineScope(Dispatchers.IO).launch {
            val value = snapshot!!.get("seconds", Int::class.java) ?: 10
            for (current in value downTo 0) {
                advertisingCounter.postValue(current)
                delay(1000)
            }
        }
    }

    fun collectAll() = CoroutineScope(Dispatchers.IO).launch {
        collectAll(repository.getAllRepositories(client)) { progress, maxProgress, loadedServices ->
            services = loadedServices

            repositoriesProgress.postValue(
                RepositoriesLoadingProgress(progress, maxProgress)
            )

            selectCountryCode(lastCountryCode)
        }
    }

    fun selectCountryCode(countryCode: String) {
        lastCountryCode = countryCode

        servicesCount.postValue(
            services
                .filter(countryCode).size
        )
    }

    fun setProxyEnabled(enabled: Boolean) {
        repository.isProxyEnabled = enabled
        proxyEnabled.value = enabled
    }

    fun setSnowfallEnabled(enabled: Boolean) {
        repository.isSnowfallEnabled = enabled
        snowfallEnabled.value = enabled
    }

    private fun loadCloudStatic() {
        val request: Request = Request.Builder()
            .url(BuildVars.DATA_SOURCE)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            private val json = Json {
                ignoreUnknownKeys = true
            }

            override fun onResponse(call: Call, response: Response) {
                runCatching {
                    val cloudStatic = json.decodeFromString<CloudStatic>(response.body?.string()!!)
                    updates.postValue(cloudStatic.updates)
                    stories.postValue(cloudStatic.stories.filter {
                        BuildConfig.VERSION_CODE <= it.maxVersionCode && BuildConfig.VERSION_CODE >= it.minVersionCode
                    })
                }.onFailure {
                    it.printStackTrace()
                }
            }
        })
    }

    private fun loadAdvertising() {
        FirebaseFirestore.getInstance()
            .collection("main")
            .document("advertising")
            .get()
            .addOnSuccessListener { value: DocumentSnapshot ->
                advertisingAvailable.value = value.get("active", Boolean::class.java)
                advertising.setValue(value)
            }
    }

    fun downloadUpdate(url: String?) {
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .addTag(UPDATE)
            .setInputData(
                Data.Builder()
                    .putString(DownloadWorker.URL, url)
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        progress.value = Progress(R.drawable.ic_baseline_download_24, R.string.update)

        pushCurrentWork(workRequest)
        workManager.enqueue(workRequest)
    }

    fun scheduleAttack(countryCode: String, phoneNumber: String, repeats: Int, date: Long, current: Long) {
        FirebaseFirestore.getInstance()
            .collection("whitelist")
            .document(countryCode + phoneNumber)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                val notWhitelist = !task.isSuccessful || task.result.data == null

                val inputData = Data.Builder()
                    .putString(AttackWorker.KEY_COUNTRY_CODE, countryCode)
                    .putString(AttackWorker.KEY_PHONE, phoneNumber)
                    .putInt(AttackWorker.KEY_REPEATS, min(repeats, BuildVars.MAX_REPEATS_COUNT))
                    .putBoolean(AttackWorker.KEY_PROXY_ENABLED, repository.isProxyEnabled)
                    .putInt(AttackWorker.KEY_CHUNK_SIZE, repository.attackSpeed.chunkSize)
                    .putBoolean(AttackWorker.KEY_FAKE_SERVICES, !notWhitelist)
                    .build()

                val workRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(AttackWorker::class.java)
                    .addTag(ATTACK)
                    .addTag("+$countryCode$phoneNumber;$date")
                    .setInitialDelay(date - current, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                if (current == 0L) {
                    progress.value = Progress(R.drawable.logo, R.string.attack)
                    pushCurrentWork(workRequest)
                }

                workManager.enqueue(workRequest)
            }
    }

    private fun pushCurrentWork(request: WorkRequest) {
        currentWorkId = request.id
        workStatus.value = true
    }

    fun startAttack(countryCode: String, phoneNumber: String, numberOfCyclesNum: Int) =
        scheduleAttack(countryCode, phoneNumber, numberOfCyclesNum, 0, 0)

    fun cancelCurrentWork() =
        currentWorkId?.let { workManager.cancelWorkById(it) }

    fun cancelUpdates() {
        updates.value = null
    }

    companion object {
        const val KEY_PROGRESS = "progress"
        const val KEY_MAX_PROGRESS = "max_progress"
        const val ATTACK = "attack"
        const val UPDATE = "update"
    }
}
