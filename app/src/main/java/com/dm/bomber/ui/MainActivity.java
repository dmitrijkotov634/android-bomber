package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import com.dm.bomber.BuildConfig;
import com.dm.bomber.BuildVars;
import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;
import com.dm.bomber.services.ServicesRepository;
import com.dm.bomber.ui.adapters.CountryCodeAdapter;
import com.dm.bomber.ui.dialog.SettingsDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MainViewModel model;
    private Repository repository;

    private String clipText;

    public static final String TASK_ID = "task_id";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WorkManager workManager = WorkManager.getInstance(this);

        repository = new MainRepository(this);
        model = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new MainModelFactory(repository, workManager)).get(MainViewModel.class);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        model.getProgress().observe(this, progress -> {
            binding.progress.setIndeterminate(progress.second == 0);

            if (progress.second == 0) {
                binding.progressText.setText(R.string.waiting);
                return;
            }

            binding.progress.setProgress(progress.first);
            binding.progress.setMax(progress.second);
            binding.progressText.setText(progress.first + "/" + progress.second);
        });

        model.getAttackStatus().observe(this, attackStatus -> {
            if (attackStatus) {
                binding.getRoot().requestLayout();
                binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new BlurListener());
            } else {
                for (int i = 0; i < binding.getRoot().getChildCount(); i++)
                    binding.getRoot().getChildAt(i).setVisibility(View.VISIBLE);
                binding.attackScreen.setVisibility(View.GONE);
            }
        });

        model.getUpdates().observe(this, dataSnapshot -> {
            if (dataSnapshot == null) return;
            Integer versionCode = dataSnapshot.child("versionCode").getValue(Integer.class);
            if (versionCode != null && versionCode > BuildConfig.VERSION_CODE) {
                String key = "description-" + Locale.getDefault().getLanguage();
                if (!dataSnapshot.hasChild(key))
                    key = "description";
                CharSequence description = Html.fromHtml(dataSnapshot.child(key).getValue(String.class));
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setIcon(R.drawable.ic_baseline_update_24)
                        .setTitle(R.string.update_available)
                        .setMessage(description)
                        .setPositiveButton(R.string.download, (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.child("uri").getValue(String.class)))))
                        .show();
            }
            model.cancelUpdates();
        });

        InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        View.OnLongClickListener limitSchedule = view -> {
            input.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

            Snackbar.make(view, R.string.limit_reached, Snackbar.LENGTH_LONG).show();
            return true;
        };

        View.OnLongClickListener schedule = view -> {
            input.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

            String phoneNumber = binding.phoneNumber.getText().toString();
            String repeats = binding.repeats.getText().toString();

            int length = BuildVars.MAX_PHONE_LENGTH[binding.phoneCode.getSelectedItemPosition()];
            if (phoneNumber.length() != length && length != 0) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return false;
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

                    model.scheduleAttack(BuildVars.COUNTRY_CODES[binding.phoneCode.getSelectedItemPosition()], phoneNumber,
                            repeats.isEmpty() ? 1 : Integer.parseInt(repeats),
                            date.getTimeInMillis(), currentDate.getTimeInMillis());

                    new SettingsDialog().show(getSupportFragmentManager(), null);

                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

            return true;
        };

        model.getScheduledAttacks().observe(this, attacks -> binding.startAttack.setOnLongClickListener(attacks.size() >= BuildVars.SCHEDULED_ATTACKS_LIMIT ? limitSchedule : schedule));

        model.isSnowfallEnabled().observe(this, enabled -> binding.snowfall.setVisibility(enabled ? View.VISIBLE : View.GONE));

        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(this, BuildVars.COUNTRY_FLAGS, BuildVars.COUNTRY_CODES);

        String[] hints = getResources().getStringArray(R.array.hints);
        binding.phoneNumber.setHint(hints[0]);

        binding.phoneCode.setAdapter(countryCodeAdapter);
        binding.phoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                binding.phoneNumber.setHint(hints[index]);
                binding.servicesCount.setText(String.valueOf(new ServicesRepository().getServices(BuildVars.COUNTRY_CODES[index]).size()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.repeats.setOnClickListener(view -> {
            if (binding.repeats.getText().toString().isEmpty())
                binding.repeats.setText("1");
        });

        binding.repeats.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            try {
                String value = dest.subSequence(0, dstart)
                        + source.subSequence(start, end).toString()
                        + dest.subSequence(dend, dest.length());
                int repeats = Integer.parseInt(value);
                if (repeats <= BuildVars.MAX_REPEATS_COUNT && value.length() <= BuildVars.REPEATS_MAX_LENGTH)
                    return null;
            } catch (NumberFormatException ignore) {
            }
            return "";
        }});

        binding.startAttack.setOnClickListener(view -> {
            input.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

            String phoneNumber = binding.phoneNumber.getText().toString();
            String repeats = binding.repeats.getText().toString();

            int length = BuildVars.MAX_PHONE_LENGTH[binding.phoneCode.getSelectedItemPosition()];
            if (phoneNumber.length() != length && length != 0) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            repository.setLastCountryCode(binding.phoneCode.getSelectedItemPosition());
            repository.setLastPhone(phoneNumber);

            model.startAttack(BuildVars.COUNTRY_CODES[binding.phoneCode.getSelectedItemPosition()], phoneNumber,
                    repeats.isEmpty() ? 1 : Integer.parseInt(repeats));
        });

        binding.closeAttack.setOnClickListener(view -> model.stopAttack());

        binding.bomb.setOnLongClickListener(view -> {
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.toast, Snackbar.LENGTH_SHORT);

            boolean state = binding.snowfall.getVisibility() != View.VISIBLE;
            snackbar.setAction(state ? R.string.enable_snowfall : R.string.disable_snowfall, v -> model.setSnowfallEnabled(state));

            snackbar.show();
            return false;
        });

        binding.bomb.setOnClickListener(view -> view.animate()
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

        binding.phoneNumber.setOnLongClickListener(view -> {
            if (binding.phoneNumber.getText().toString().isEmpty() && clipText != null && !processText(clipText)) {
                binding.phoneCode.setSelection(repository.getLastCountryCode());
                binding.phoneNumber.setText(repository.getLastPhone());
            }

            return false;
        });

        binding.settings.setOnClickListener(view -> new SettingsDialog().show(getSupportFragmentManager(), null));

        View.OnClickListener telegram = (view) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildVars.TELEGRAM_URL)));

        binding.telegramUrl.setOnClickListener(telegram);
        binding.telegramIcon.setOnClickListener(telegram);

        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_DIAL.equals(intent.getAction()))
                processText(intent.getData().getSchemeSpecificPart());

            if (intent.hasExtra(TASK_ID)) {
                workManager.cancelWorkById(UUID.fromString(intent.getStringExtra(TASK_ID)));
                new SettingsDialog().show(getSupportFragmentManager(), null);
            }
        }
    }

    private boolean processText(String data) {
        if (data.matches("(8|\\+(7|380|375))([0-9()\\-\\s])*")) {

            if (data.startsWith("8"))
                data = "+7" + data.substring(1);

            data = data.substring(1);
            for (int i = 0; i < BuildVars.COUNTRY_CODES.length; i++) {
                if (data.startsWith(BuildVars.COUNTRY_CODES[i])) {
                    binding.phoneCode.setSelection(i);
                    binding.phoneNumber.setText(data.substring(BuildVars.COUNTRY_CODES[i].length()).replaceAll("[^\\d.]", ""));

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            if (clipboard.hasPrimaryClip()) {
                try {
                    ClipData clipData = clipboard.getPrimaryClip();

                    if (clipData != null)
                        clipText = clipData.getItemAt(0).coerceToText(this).toString();

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class BlurListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            try {
                binding.blur.setImageBitmap(Blurry.with(MainActivity.this)
                        .radius(20)
                        .sampling(2)
                        .capture(binding.getRoot())
                        .get());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < binding.getRoot().getChildCount(); i++)
                binding.getRoot().getChildAt(i).setVisibility(View.GONE);

            binding.attackScreen.setVisibility(View.VISIBLE);

            binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        model.stopAttack();

        if (binding.attackScreen.getVisibility() != View.VISIBLE)
            finish();
    }
}