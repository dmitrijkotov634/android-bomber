package com.dm.bomber.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;

import com.dm.bomber.R;
import com.dm.bomber.databinding.DialogProxiesBinding;
import com.dm.bomber.ui.MainRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ProxiesDialog extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogProxiesBinding binding = DialogProxiesBinding.inflate(inflater);

        ((BottomSheetDialog) Objects.requireNonNull(getDialog())).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        MainRepository repository = new MainRepository(requireContext());

        binding.proxies.setText(repository.getRawProxy());
        binding.save.setOnClickListener(view -> {
            try {
                repository.parseProxy(binding.proxies.getText() == null ? "" : binding.proxies.getText().toString());
                repository.setRawProxy(binding.proxies.getText().toString());

                dismiss();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                Snackbar.make(binding.getRoot(), R.string.proxy_format_error, Snackbar.LENGTH_LONG).show();
            }
        });

        TooltipCompat.setTooltipText(binding.save, getString(R.string.save));

        return binding.getRoot();
    }
}
