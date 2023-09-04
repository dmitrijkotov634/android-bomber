package com.dm.bomber.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import com.dm.bomber.BuildVars.AttackSpeed
import com.dm.bomber.R
import com.dm.bomber.databinding.DialogRepositoriesBinding
import com.dm.bomber.databinding.TextInputRowBinding
import com.dm.bomber.ui.MainRepository
import com.dm.bomber.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RepositoriesDialog : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogRepositoriesBinding.inflate(inflater)
        val repository = MainRepository(requireContext())

        val model by activityViewModels<MainViewModel>()

        (dialog as BottomSheetDialog).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED)

        binding.remoteServices.setChecked(repository.isRemoteServicesEnabled)
        binding.callCenters.setChecked(repository.isCallCentersEnabled)

        binding.speed.check(
            when (repository.attackSpeed) {
                AttackSpeed.FAST -> R.id.fast
                AttackSpeed.DEFAULT -> R.id.defaults
                AttackSpeed.SLOW -> R.id.slow
            }
        )

        binding.speed.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.slow -> repository.attackSpeed = AttackSpeed.SLOW
                R.id.defaults -> repository.attackSpeed = AttackSpeed.DEFAULT
                R.id.fast -> repository.attackSpeed = AttackSpeed.FAST
            }
        }

        binding.callCenters.setOnCheckedChangeListener { _: CompoundButton?, enabled: Boolean ->
            repository.isCallCentersEnabled = enabled
        }

        binding.remoteServices.setOnCheckedChangeListener { _: CompoundButton?, enabled: Boolean ->
            val textInputRowBinding = TextInputRowBinding.inflate(getLayoutInflater())

            val builder = StringBuilder()

            for (url in repository.remoteServicesUrls!!) {
                builder.append(url)
                builder.append(";")
            }

            if (builder.isNotEmpty())
                builder.deleteCharAt(builder.length - 1)

            textInputRowBinding.textInput.editText?.setText(builder.toString())

            if (enabled)
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.enter_source)
                    .setView(textInputRowBinding.getRoot())
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _: DialogInterface?, _: Int ->
                        repository.remoteServicesUrls =
                            HashSet(textInputRowBinding.textInput.editText!!.getText().toString().split(";"))
                    }
                    .show()

            repository.isRemoteServicesEnabled = enabled
        }

        binding.apply.setOnClickListener {
            model.collectAll()
            dismiss()
        }

        return binding.getRoot()
    }
}
