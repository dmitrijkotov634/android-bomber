package com.dm.bomber.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.dm.bomber.R;
import com.dm.bomber.databinding.DialogProxiesBinding;
import com.dm.bomber.ui.MainRepository;
import com.dm.bomber.ui.MainViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class ProxiesDialog extends DialogFragment {

    private DialogProxiesBinding binding;

    private MainRepository repository;

    private MainViewModel model;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        repository = new MainRepository(requireContext());
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = DialogProxiesBinding.inflate(getLayoutInflater());
        binding.proxies.setText(repository.getRawProxy());

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.proxy)
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) Objects.requireNonNull(getDialog())).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            try {
                repository.parseProxy(binding.proxies.getText() == null ? "" : binding.proxies.getText().toString());
                repository.setRawProxy(binding.proxies.getText().toString());

                model.setProxyEnabled(!repository.getProxy().isEmpty());
                dismiss();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                Toast.makeText(requireContext(), R.string.proxy_format_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

