package com.dm.bomber.ui.dialog;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.dm.bomber.BuildVars;
import com.dm.bomber.R;
import com.dm.bomber.databinding.DialogSettingsBinding;
import com.dm.bomber.ui.MainRepository;
import com.dm.bomber.ui.MainViewModel;
import com.dm.bomber.ui.adapters.BomberWorkAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class SettingsDialog extends BottomSheetDialogFragment {

    private MainRepository repository;

    private DialogSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSettingsBinding.inflate(getLayoutInflater());

        ((BottomSheetDialog) Objects.requireNonNull(getDialog())).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        repository = new MainRepository(requireContext());

        WorkManager workManager = WorkManager.getInstance(requireContext());

        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        BomberWorkAdapter bomberWorkAdapter = new BomberWorkAdapter(
                getViewLifecycleOwner(),
                getActivity(),
                model.getScheduledAttacks(),
                workInfo -> workManager.cancelWorkById(workInfo.getId()));

        bomberWorkAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                binding.empty.setVisibility(bomberWorkAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                super.onChanged();
            }
        });

        model.isProxyEnabled().observe(this, binding.proxyTile::setChecked);

        binding.tasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.tasks.setAdapter(bomberWorkAdapter);

        binding.themeTile.setChecked((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        binding.themeTile.setOnClickListener(view -> {
            dismiss();
            setCurrentTheme(binding.themeTile.isChecked() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        binding.themeTile.setOnLongClickListener(view -> {
            dismiss();
            setCurrentTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            return true;
        });

        binding.proxyTile.setOnCheckedChangeListener((button, checked) -> {
            if (!button.isPressed())
                return;

            if (checked) {
                new ProxiesDialog().show(getParentFragmentManager(), null);
                dismiss();
            }

            model.setProxyEnabled(checked);
        });

        binding.sourceCodeTile.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.SOURCECODE_URL))));
        binding.donateTile.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.DONATE_URL))));

        TooltipCompat.setTooltipText(binding.donateTile, getString(R.string.donate));
        TooltipCompat.setTooltipText(binding.proxyTile, getString(R.string.proxy));
        TooltipCompat.setTooltipText(binding.sourceCodeTile, getString(R.string.source_code));

        return binding.getRoot();
    }

    private void setCurrentTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        repository.setTheme(theme);
    }
}
