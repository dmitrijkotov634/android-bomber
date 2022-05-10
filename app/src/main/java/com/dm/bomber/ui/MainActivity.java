package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;
import com.dm.bomber.databinding.DialogProxiesBinding;
import com.dm.bomber.databinding.DialogSettingsBinding;
import com.dm.bomber.ui.adapters.BomberWorkAdapter;
import com.dm.bomber.ui.adapters.CountryCodeAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Calendar;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private DialogSettingsBinding settingsBinding;

    private MainViewModel model;
    private Repository repository;

    private String clipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WorkManager workManager = WorkManager.getInstance(this);

        repository = new MainRepository(this);
        model = new ViewModelProvider(this,
                new MainModelFactory(repository, workManager)).get(MainViewModel.class);

        AppCompatDelegate.setDefaultNightMode(repository.getTheme());

        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        settingsBinding = DialogSettingsBinding.inflate(getLayoutInflater());

        DialogProxiesBinding proxyBinding = DialogProxiesBinding.inflate(getLayoutInflater());
        proxyBinding.proxies.setText(repository.getRawProxy());

        BottomSheetDialog proxyDialog = new BottomSheetDialog(this);
        proxyDialog.setContentView(proxyBinding.getRoot());
        proxyBinding.save.setOnClickListener(view -> {
            try {
                repository.parseProxy(proxyBinding.proxies.getText() == null ? "" : proxyBinding.proxies.getText().toString());
                repository.setRawProxy(proxyBinding.proxies.getText().toString());

                proxyDialog.cancel();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                Snackbar.make(proxyBinding.getRoot(), R.string.proxy_format_error, Snackbar.LENGTH_LONG).show();
            }
        });

        TooltipCompat.setTooltipText(proxyBinding.save, getString(R.string.save));
        TooltipCompat.setTooltipText(settingsBinding.donateTile, getString(R.string.donate));
        TooltipCompat.setTooltipText(settingsBinding.proxyTile, getString(R.string.proxy));
        TooltipCompat.setTooltipText(settingsBinding.sourceCodeTile, getString(R.string.source_code));

        setContentView(mainBinding.getRoot());

        model.isPromotionShown().observe(this, shown -> {
            if (!shown)
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.ic_baseline_perm_device_information_24)
                        .setTitle(R.string.information)
                        .setMessage(R.string.promotion)
                        .setCancelable(false)
                        .setPositiveButton(R.string.open, (dialogInterface, i) -> {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/androidsmsbomber")));
                            model.closePromotion();
                        })
                        .setNegativeButton(R.string.close, (dialogInterface, i) -> model.showPromotion())
                        .show();
        });

        model.isProxyEnabled().observe(this, enabled -> settingsBinding.proxyTile.setChecked(enabled));

        model.getCurrentProgress().observe(this, progress -> mainBinding.progress.setProgress(progress));
        model.getMaxProgress().observe(this, maxProgress -> mainBinding.progress.setMax(maxProgress));

        model.getAttackStatus().observe(this, attackStatus -> {
            if (attackStatus) {
                mainBinding.main.requestLayout();
                mainBinding.main.getViewTreeObserver().addOnGlobalLayoutListener(new BlurListener());
            } else {
                mainBinding.main.setVisibility(View.VISIBLE);
                mainBinding.attackScreen.setVisibility(View.GONE);
            }
        });

        BomberWorkAdapter bomberWorkAdapter = new BomberWorkAdapter(this, workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromStates(Arrays.asList(
                        WorkInfo.State.RUNNING,
                        WorkInfo.State.ENQUEUED
                )).build()),
                workInfo -> workManager.cancelWorkById(workInfo.getId()));

        bomberWorkAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                settingsBinding.empty.setVisibility(bomberWorkAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                super.onChanged();
            }
        });

        settingsBinding.tasks.setLayoutManager(new LinearLayoutManager(this));
        settingsBinding.tasks.setAdapter(bomberWorkAdapter);

        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_all},
                MainViewModel.countryCodes);

        String[] hints = getResources().getStringArray(R.array.hints);
        mainBinding.phoneNumber.setHint(hints[0]);

        mainBinding.phoneCode.setAdapter(countryCodeAdapter);
        mainBinding.phoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                mainBinding.phoneNumber.setHint(hints[index]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        BottomSheetDialog settings = new BottomSheetDialog(this);
        settings.setContentView(settingsBinding.getRoot());

        mainBinding.startAttack.setOnClickListener(view -> {
            InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(mainBinding.getRoot().getWindowToken(), 0);

            String phoneNumber = mainBinding.phoneNumber.getText().toString();
            String repeats = mainBinding.repeats.getText().toString();

            if (phoneNumber.length() < 7) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            repository.setLastCountryCode(mainBinding.phoneCode.getSelectedItemPosition());
            repository.setLastPhone(phoneNumber);

            model.startAttack(mainBinding.phoneCode.getSelectedItemPosition(), phoneNumber,
                    repeats.isEmpty() ? 1 : Integer.parseInt(repeats));
        });

        mainBinding.startAttack.setOnLongClickListener(view -> {
            String phoneNumber = mainBinding.phoneNumber.getText().toString();
            String repeats = mainBinding.repeats.getText().toString();

            if (phoneNumber.length() < 7) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return true;
            }

            final Calendar currentDate = Calendar.getInstance();
            final Calendar date = Calendar.getInstance();

            new DatePickerDialog(MainActivity.this, (datePicker, year, monthOfYear, dayOfMonth) -> {
                date.set(year, monthOfYear, dayOfMonth);

                new TimePickerDialog(MainActivity.this, (timePicker, hourOfDay, minute) -> {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);

                    if (date.getTimeInMillis() < currentDate.getTimeInMillis()) {
                        Snackbar.make(view, R.string.time_is_incorrect, Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    model.scheduleAttack(mainBinding.phoneCode.getSelectedItemPosition(), phoneNumber,
                            repeats.isEmpty() ? 1 : Integer.parseInt(repeats),
                            date.getTimeInMillis() - currentDate.getTimeInMillis());

                    settings.show();

                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

            return true;
        });

        mainBinding.bomb.setOnClickListener(view -> view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(90)
                                .setListener(null)
                                .start();
                    }
                })
                .start());

        mainBinding.phoneNumber.setOnLongClickListener(view -> {
            if (mainBinding.phoneNumber.getText().toString().isEmpty() && clipText != null)
                processText(clipText);

            return false;
        });

        mainBinding.settings.setOnClickListener(view -> settings.show());

        settingsBinding.themeTile.setChecked((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        settingsBinding.themeTile.setOnClickListener(view -> {
            settings.cancel();

            setCurrentTheme(settingsBinding.themeTile.isChecked() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        settingsBinding.themeTile.setOnLongClickListener(view -> {
            settings.cancel();

            setCurrentTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            return true;
        });

        settingsBinding.proxyTile.setOnCheckedChangeListener((button, checked) -> {
            if (!button.isPressed())
                return;

            if (checked) {
                proxyDialog.show();
                settings.cancel();
            }

            model.setProxyEnabled(checked);
        });

        settingsBinding.sourceCodeTile.setOnClickListener(view -> startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dmitrijkotov634/android-bomber/"))));

        settingsBinding.donateTile.setOnClickListener(view -> startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegra.ph/donate-01-19-2"))));

        mainBinding.phoneCode.setSelection(repository.getLastCountryCode());
        mainBinding.phoneNumber.setText(repository.getLastPhone());

        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_DIAL.equals(intent.getAction()))
            processText(intent.getData().getSchemeSpecificPart());
    }

    private void processText(String data) {
        if (data.matches("(8|\\+(7|380))([0-9()\\-\\s])*")) {

            if (data.startsWith("8"))
                data = "+7" + data.substring(1);

            data = data.substring(1);
            for (int i = 0; i < MainViewModel.countryCodes.length; i++) {
                if (data.startsWith(MainViewModel.countryCodes[i])) {
                    mainBinding.phoneCode.setSelection(i);
                    mainBinding.phoneNumber.setText(data.substring(MainViewModel.countryCodes[i].length()).replaceAll("[^\\d.]", ""));

                    break;
                }
            }
        }
    }

    private void setCurrentTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        repository.setTheme(theme);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            if (clipboard.hasPrimaryClip()) {
                ClipData clipData = clipboard.getPrimaryClip();

                if (clipData != null)
                    clipText = clipData.getItemAt(0).coerceToText(this).toString();
            }
        }
    }

    private class BlurListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            Blurry.with(MainActivity.this)
                    .radius(20)
                    .sampling(1)
                    .async()
                    .capture(mainBinding.getRoot())
                    .getAsync(bitmap -> {
                        mainBinding.blur.setImageBitmap(bitmap);

                        mainBinding.main.setVisibility(View.GONE);
                        mainBinding.attackScreen.setVisibility(View.VISIBLE);
                    });

            mainBinding.main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        model.stopAttack();

        if (mainBinding.attackScreen.getVisibility() != View.VISIBLE)
            finish();
    }
}