package com.dm.bomber.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.dm.bomber.R;
import com.dm.bomber.databinding.DialogAdvertisingBinding;
import com.dm.bomber.ui.MainViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;
import java.util.Objects;

public class AdvertisingDialog extends DialogFragment {

    private DialogAdvertisingBinding binding;

    private final static String HTML_KEY = "html";
    private final static String IMAGE_KEY = "image";
    private final static String URL_KEY = "url";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAdvertisingBinding.inflate(getLayoutInflater());

        binding.advertisingDescription.setMovementMethod(LinkMovementMethod.getInstance());

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setCancelable(false);

        MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        model.getAdvertisingCounter().observe(getViewLifecycleOwner(), counter -> {
            binding.skipButton.setText(getString(R.string.skip, counter));
            binding.skipButton.setEnabled(counter == 0);
        });

        model.getAdvertising().observe(getViewLifecycleOwner(), dataSnapshot -> {
            if (dataSnapshot == null) return;

            String descriptionKey = "description-" + Locale.getDefault().getLanguage();
            if (!dataSnapshot.contains(descriptionKey))
                descriptionKey = "description";

            String description = dataSnapshot.getString(descriptionKey);
            Boolean html = dataSnapshot.getBoolean(HTML_KEY);

            String buttonKey = "button-" + Locale.getDefault().getLanguage();
            if (!dataSnapshot.contains(buttonKey))
                buttonKey = "button";

            CharSequence descriptionPrepared = (html != null && html) ? Html.fromHtml(description) : description;

            binding.advertisingDescription.setText(descriptionPrepared);
            binding.advertisingButton.setText(dataSnapshot.getString(buttonKey));

            binding.advertisingButton.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.getString(URL_KEY)))));

            String image = dataSnapshot.getString(IMAGE_KEY);

            if (image != null && !image.isEmpty()) {
                binding.advertisingImage.setVisibility(View.VISIBLE);
                Glide
                        .with(this)
                        .load(image)
                        .fitCenter()
                        .into(binding.advertisingImage);
            } else
                binding.advertisingImage.setVisibility(View.GONE);

            model.startCounting();
        });

        binding.skipButton.setOnClickListener(view -> {
            getDialog().cancel();
            model.setAdvertisingTrigger(true);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}