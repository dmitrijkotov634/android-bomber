package com.dm.bomber.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dm.bomber.BuildVars;
import com.dm.bomber.R;
import com.dm.bomber.databinding.DialogRepositoriesBinding;
import com.dm.bomber.databinding.TextInputRowBinding;
import com.dm.bomber.ui.MainRepository;
import com.dm.bomber.ui.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class RepositoriesDialog extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogRepositoriesBinding binding = DialogRepositoriesBinding.inflate(inflater);

        MainRepository repository = new MainRepository(requireContext());

        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ((BottomSheetDialog) Objects.requireNonNull(getDialog())).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        binding.remoteServices.setChecked(repository.isRemoteServicesEnabled());

        switch (repository.getAttackSpeed()) {
            case FAST:
                binding.speed.check(R.id.fast);
                break;
            case DEFAULT:
                binding.speed.check(R.id.defaults);
                break;
            case SLOW:
                binding.speed.check(R.id.slow);
                break;
        }

        binding.speed.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.slow)
                repository.setAttackSpeed(BuildVars.AttackSpeed.SLOW);
            else if (checkedId == R.id.defaults)
                repository.setAttackSpeed(BuildVars.AttackSpeed.DEFAULT);
            else if (checkedId == R.id.fast)
                repository.setAttackSpeed(BuildVars.AttackSpeed.FAST);
        });

        binding.remoteServices.setOnCheckedChangeListener((compoundButton, enabled) -> {
            TextInputRowBinding textInputRowBinding = TextInputRowBinding.inflate(getLayoutInflater());

            StringBuilder builder = new StringBuilder();
            for (String url : repository.getRemoteServicesUrls()) {
                builder.append(url);
                builder.append(";");
            }
            if (builder.length() > 0)
                builder.deleteCharAt(builder.length() - 1);

            Objects.requireNonNull(textInputRowBinding.textInput.getEditText()).setText(builder.toString());

            if (enabled)
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.enter_source)
                        .setView(textInputRowBinding.getRoot())
                        .setPositiveButton(android.R.string.ok,
                                (dialogInterface, i) ->
                                        repository.setRemoteServicesUrls(
                                                new HashSet<>(
                                                        Arrays.asList(textInputRowBinding.textInput.getEditText().getText().toString().split(";")))))
                        .show();

            repository.setRemoteServicesEnabled(enabled);
        });

        binding.apply.setOnClickListener(view -> {
            model.collectAll();
            dismiss();
        });

        return binding.getRoot();
    }
}
