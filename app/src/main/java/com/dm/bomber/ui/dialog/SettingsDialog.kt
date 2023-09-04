package com.dm.bomber.ui.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dm.bomber.BuildVars
import com.dm.bomber.R
import com.dm.bomber.databinding.DialogSettingsBinding
import com.dm.bomber.ui.MainRepository
import com.dm.bomber.ui.MainViewModel
import com.dm.bomber.ui.adapters.BomberWorkAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsDialog : BottomSheetDialogFragment() {
    private val repository: MainRepository by lazy { MainRepository(requireContext()) }

    private val binding: DialogSettingsBinding by lazy {
        DialogSettingsBinding.inflate(getLayoutInflater())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (dialog as BottomSheetDialog).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED)

        val workManager = WorkManager.getInstance(requireContext())

        val model by activityViewModels<MainViewModel>()

        val bomberWorkAdapter = BomberWorkAdapter(
            requireActivity()
        ) { workInfo: WorkInfo? ->
            workManager.cancelWorkById(workInfo!!.id)
        }

        model.scheduledAttacks.observe(this) { workInfoResult: List<WorkInfo> ->
            bomberWorkAdapter.setWorkInfo(workInfoResult)
            bomberWorkAdapter.notifyDataSetChanged()
        }

        model.servicesCount.observe(this) { servicesCount: Int ->
            binding.settingsServicesCount.text = servicesCount.toString()
        }

        bomberWorkAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                binding.empty.visibility = if (bomberWorkAdapter.getItemCount() == 0) View.VISIBLE else View.GONE
                super.onChanged()
            }
        })

        binding.tasks.setLayoutManager(LinearLayoutManager(requireContext()))
        binding.tasks.setAdapter(bomberWorkAdapter)

        binding.themeTile.setChecked(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        binding.themeTile.setOnClickListener {
            setCurrentTheme(
                if (binding.themeTile.isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.themeTile.setOnLongClickListener {
            setCurrentTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            true
        }

        binding.proxySwitch.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            model.setProxyEnabled(checked)
        }

        binding.proxyCard.setOnClickListener { ProxiesDialog().show(getParentFragmentManager(), null) }

        model.proxyEnabled.observe(getViewLifecycleOwner()) { enabled: Boolean? ->
            binding.proxySwitch.setEnabled(repository.proxy.isNotEmpty())
            binding.proxySwitch.setChecked(enabled!!)
        }

        binding.sourceCodeTile.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.SOURCECODE_URL)))
        }

        binding.donateTile.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.DONATE_URL)))
        }

        binding.servicesCard.setOnClickListener {
            RepositoriesDialog().show(getParentFragmentManager(), null)
        }

        TooltipCompat.setTooltipText(binding.donateTile, getString(R.string.donate))
        TooltipCompat.setTooltipText(binding.proxyTile, getString(R.string.proxy))
        TooltipCompat.setTooltipText(binding.sourceCodeTile, getString(R.string.source_code))

        return binding.getRoot()
    }

    private fun setCurrentTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(theme)
        repository.theme = theme
    }
}
