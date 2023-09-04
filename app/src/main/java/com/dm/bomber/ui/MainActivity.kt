package com.dm.bomber.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dm.bomber.BuildConfig
import com.dm.bomber.BuildVars
import com.dm.bomber.R
import com.dm.bomber.databinding.ActivityMainBinding
import com.dm.bomber.ui.adapters.CountryCodeAdapter
import com.dm.bomber.ui.dialog.AdvertisingDialog
import com.dm.bomber.ui.dialog.RepositoriesDialog
import com.dm.bomber.ui.dialog.SettingsDialog
import com.dm.bomber.ui.stories.PreviewAdapter
import com.dm.bomber.ui.stories.StoriesDialog
import com.dm.bomber.ui.stories.Story
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.blurry.Blurry
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val repository: MainRepository by lazy { MainRepository(this) }
    private val inputManager: InputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }
    private val workManager: WorkManager by lazy { WorkManager.getInstance(this) }

    private val model by viewModels<MainViewModel> { MainModelFactory(repository, workManager) }

    private var clipText: String? = null
    private var advertisingAvailable = false

    @SuppressLint("SetTextI18n", "BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)

        setContentView(binding.getRoot())

        model.progress.observe(this) { progress ->
            binding.taskIcon.setImageResource(progress.iconResource)
            binding.progressTitle.setText(progress.titleResource)

            binding.progress.setIndeterminate(progress.maxProgress == 0)

            if (progress.maxProgress == 0) {
                binding.progressText.setText(R.string.waiting)
                return@observe
            }

            binding.progress.setProgress(progress.currentProgress)
            binding.progress.setMax(progress.maxProgress)

            binding.progressText.text = "${progress.currentProgress}/${progress.maxProgress}"
        }

        model.workStatus.observe(this) { workStatus: Boolean ->
            if (workStatus) {
                binding.getRoot().requestLayout()
                binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(BlurListener())
            } else {
                repeat(binding.getRoot().childCount) { i ->
                    val view = binding.getRoot().getChildAt(i)

                    if (view.id != R.id.snowfall)
                        view.visibility = View.VISIBLE
                }

                binding.workScreen.visibility = View.GONE
            }
        }

        model.updates.observe(this) { value ->
            if (value == null)
                return@observe

            if (value.versionCode > BuildConfig.VERSION_CODE) {
                if (value.important) binding.startAttack.setEnabled(false)
                Snackbar.make(binding.getRoot(), R.string.update_available, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.download) {
                        val description: CharSequence =
                            HtmlCompat.fromHtml(
                                value.description.getOrDefault(
                                    Locale.getDefault().language,
                                    value.description["_"]
                                )!!, HtmlCompat.FROM_HTML_MODE_LEGACY
                            )

                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setIcon(R.drawable.ic_baseline_update_24)
                            .setTitle(R.string.update_available)
                            .setMessage(description)
                            .setPositiveButton(R.string.download) { _: DialogInterface?, _: Int ->
                                if (value.onlyDirect || value.allowDirect && !isTelegramInstalled)
                                    model.downloadUpdate(value.directUrl)
                                else
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value.telegramUrl)))
                            }.show()
                    }.show()
            }

            model.cancelUpdates()
        }

        model.getAdvertisingAvailable().observe(this) { available: Boolean? ->
            advertisingAvailable = available!!
        }

        val limitSchedule = OnLongClickListener { view: View? ->
            inputManager.hideSoftInputFromWindow(binding.getRoot().windowToken, 0)
            Snackbar.make(view!!, R.string.limit_reached, Snackbar.LENGTH_LONG).show()
            true
        }

        val schedule = OnLongClickListener { view: View? ->
            inputManager.hideSoftInputFromWindow(binding.getRoot().windowToken, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent()
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.setData(Uri.parse("package:$packageName"))
                    startActivity(intent)
                }
            }

            val phoneNumber = binding.phoneNumber.getText().toString()
            val repeats = binding.repeats.getText().toString()

            val currentDate = Calendar.getInstance()
            val date = Calendar.getInstance()

            if (checkPhoneNumberLength(phoneNumber, currentPhoneCodeMaxPhoneLength)) DatePickerDialog(
                this@MainActivity,
                { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    date[year, monthOfYear] = dayOfMonth
                    TimePickerDialog(
                        this@MainActivity,
                        OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                            date[Calendar.HOUR_OF_DAY] = hourOfDay
                            date[Calendar.MINUTE] = minute

                            if (date.getTimeInMillis() < currentDate.getTimeInMillis()) {
                                Snackbar.make(view!!, R.string.time_is_incorrect, Snackbar.LENGTH_LONG).show()
                                return@OnTimeSetListener
                            }

                            showAdvertisingWithCallback {
                                model.scheduleAttack(
                                    BuildVars.COUNTRY_CODES[binding.phoneCode.selectedItemPosition], phoneNumber,
                                    if (repeats.isEmpty()) 1 else repeats.toInt(),
                                    date.getTimeInMillis(), currentDate.getTimeInMillis()
                                )

                                SettingsDialog().show(supportFragmentManager, null)
                            }
                        },
                        currentDate[Calendar.HOUR_OF_DAY],
                        currentDate[Calendar.MINUTE],
                        true
                    ).show()
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE]
            ).show()

            true
        }

        model.scheduledAttacks.observe(this) { attacks: List<WorkInfo?> ->
            binding.startAttack.setOnLongClickListener(
                if (attacks.size >= BuildVars.SCHEDULED_ATTACKS_LIMIT) limitSchedule else schedule
            )
        }

        model.snowfallEnabled.observe(this) {
            binding.snowfall.visibility = if (it) View.VISIBLE else View.GONE
        }

        model.servicesCount.observe(this) {
            binding.servicesCount.text = it.toString()
        }

        model.repositoriesProgress.observe(this) { progress ->
            binding.repositoriesLoading.setMax(progress.maxProgress)
            binding.repositoriesLoading.setProgress(progress.currentProgress)
        }

        val countryCodeAdapter = CountryCodeAdapter(this, BuildVars.COUNTRY_FLAGS, BuildVars.COUNTRY_CODES)

        val hints = getResources().getStringArray(R.array.hints)

        binding.phoneNumber.setHint(hints[0])
        binding.phoneCode.setAdapter(countryCodeAdapter)

        binding.phoneCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
                binding.phoneNumber.setHint(hints[index])
                model.selectCountryCode(BuildVars.COUNTRY_CODES[index])
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.repeats.setOnClickListener {
            if (binding.repeats.getText().toString().isEmpty()) binding.repeats.setText("1")
        }

        binding.repeats.setFilters(
            arrayOf(InputFilter
            { source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int ->
                try {
                    val value = (dest.subSequence(0, dstart)
                        .toString() + source.subSequence(start, end).toString()
                            + dest.subSequence(dend, dest.length))

                    val repeats = value.toInt()

                    if (repeats <= BuildVars.MAX_REPEATS_COUNT && value.length <= BuildVars.REPEATS_MAX_LENGTH)
                        return@InputFilter null
                } catch (ignored: NumberFormatException) {
                }
                ""
            })
        )

        binding.startAttack.setOnClickListener {
            inputManager.hideSoftInputFromWindow(binding.getRoot().windowToken, 0)

            val phoneNumber = binding.phoneNumber.getText().toString()
            val repeats = binding.repeats.getText().toString()

            if (checkPhoneNumberLength(phoneNumber, currentPhoneCodeMaxPhoneLength))
                showAdvertisingWithCallback {
                    repository.lastCountryCode = binding.phoneCode.selectedItemPosition
                    repository.lastPhone = phoneNumber

                    model.startAttack(
                        BuildVars.COUNTRY_CODES[binding.phoneCode.selectedItemPosition], phoneNumber,
                        if (repeats.isEmpty()) 1 else repeats.toInt()
                    )
                }
        }

        binding.closeAttack.setOnClickListener { model.cancelCurrentWork() }

        binding.bomb.setOnLongClickListener {
            val snackbar = Snackbar.make(binding.getRoot(), R.string.toast, Snackbar.LENGTH_SHORT)
            val state = binding.snowfall.visibility != View.VISIBLE

            snackbar.setAction(
                if (state) R.string.enable_snowfall
                else R.string.disable_snowfall
            ) {
                model.setSnowfallEnabled(state)
            }

            snackbar.show()
            false
        }

        binding.bomb.setOnClickListener { view: View ->
            view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(90)
                            .setListener(null)
                            .start()
                    }
                })
                .start()
        }

        binding.phoneNumber.setOnLongClickListener {
            if (binding.phoneNumber.getText().toString()
                    .isEmpty() && clipText != null && !processPhoneNumber(clipText!!)
            ) {
                binding.phoneCode.setSelection(repository.lastCountryCode)
                binding.phoneNumber.setText(repository.lastPhone)
            }

            false
        }

        binding.settings.setOnClickListener { SettingsDialog().show(supportFragmentManager, null) }
        binding.servicesCount.setOnClickListener { RepositoriesDialog().show(supportFragmentManager, null) }

        val telegram = View.OnClickListener { _: View? ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.TELEGRAM_URL)))
        }

        binding.telegramUrl.setOnClickListener(telegram)
        binding.telegramIcon.setOnClickListener(telegram)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean? ->
                if (!result!!) {
                    Snackbar.make(
                        binding.getRoot(),
                        R.string.notification_permission,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (intent != null) {
            if (Intent.ACTION_DIAL == intent.action)
                processPhoneNumber(intent.data!!.schemeSpecificPart)

            if (intent.hasExtra(TASK_ID)) {
                val taskId = UUID.fromString(intent.getStringExtra(TASK_ID))
                workManager.cancelWorkById(taskId)

                SettingsDialog().show(supportFragmentManager, null)

                val notificationManager = NotificationManagerCompat.from(applicationContext)
                notificationManager.cancel(taskId.hashCode())
            }
        }

        model.stories.observe(this) { stories: List<Story> ->
            binding.stories.setAdapter(PreviewAdapter(stories) { position: Int ->
                val dialog = StoriesDialog()

                dialog.setArguments(bundleOf(StoriesDialog.POSITION to position))
                dialog.show(supportFragmentManager, null)
            })

            val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)

            itemDecorator.setDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.stories_divider
                )!!
            )

            binding.stories.addItemDecoration(itemDecorator)
        }

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    model.cancelCurrentWork()

                    if (binding.workScreen.visibility != View.VISIBLE)
                        finish()
                }
            })
    }

    private val isTelegramInstalled: Boolean
        get() = packageManager.queryIntentActivities(
            Intent(Intent.ACTION_VIEW, Uri.parse("tg://")), 0
        ).isNotEmpty()

    private fun processPhoneNumber(data: String): Boolean {
        var phoneNumber = data

        if (phoneNumber.matches("(8|\\+(7|380|375|77))([\\d()\\-\\s])*".toRegex())) {
            if (phoneNumber.startsWith("8"))
                phoneNumber = "+7" + phoneNumber.substring(1)

            phoneNumber = phoneNumber.substring(1)

            for (i in BuildVars.COUNTRY_CODES.indices) {
                if (phoneNumber.startsWith(BuildVars.COUNTRY_CODES[i])) {
                    binding.phoneCode.setSelection(i)
                    binding.phoneNumber.setText(
                        phoneNumber
                            .substring(BuildVars.COUNTRY_CODES[i].length)
                            .replace("[^\\d.]", "")
                    )

                    return true
                }
            }
        }

        return false
    }

    private fun showAdvertisingWithCallback(callback: () -> Unit) {
        if (!advertisingAvailable) {
            callback()
            return
        }

        AdvertisingDialog().show(supportFragmentManager, null)

        model.advertisingTrigger.value = false

        model.advertisingTrigger.observeForever(object : Observer<Boolean?> {
            override fun onChanged(value: Boolean?) {
                if (value != true) return
                callback()
                model.advertisingTrigger.removeObserver(this)
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                try {
                    val clipData = clipboard.primaryClip
                    if (clipData != null)
                        clipText = clipData.getItemAt(0).coerceToText(this).toString()
                } catch (ignored: SecurityException) {
                }
            }
        }
    }

    private inner class BlurListener : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            try {
                binding.blur.setImageBitmap(
                    Blurry.with(this@MainActivity)
                        .radius(20)
                        .sampling(2)
                        .capture(binding.getRoot())
                        .get()
                )
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }

            repeat(binding.getRoot().childCount) { i ->
                val view = binding.getRoot().getChildAt(i)
                if (view.id != R.id.snowfall) view.visibility = View.GONE
            }

            binding.workScreen.visibility = View.VISIBLE
            binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this)
        }
    }

    private fun checkPhoneNumberLength(phoneNumber: String, length: Int): Boolean {
        if (phoneNumber.length != length && length != BuildVars.PHONE_ANY_LENGTH || length == BuildVars.PHONE_ANY_LENGTH && phoneNumber.length < 5) {
            Snackbar.make(binding.getRoot(), R.string.phone_error, Snackbar.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private val currentPhoneCodeMaxPhoneLength: Int
        get() = BuildVars.MAX_PHONE_LENGTH[binding.phoneCode.selectedItemPosition]

    companion object {
        const val TASK_ID = "task_id"
    }
}