package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import com.dm.bomber.BuildConfig;
import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;
import com.dm.bomber.ui.adapters.CountryCodeAdapter;
import com.dm.bomber.ui.dialog.SettingsDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_ID = "task_id";

    private ActivityMainBinding mainBinding;

    private MainViewModel model;
    private Repository repository;

    private String clipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WorkManager workManager = WorkManager.getInstance(this);

        repository = new MainRepository(this);
        model = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new MainModelFactory(repository, workManager)).get(MainViewModel.class);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(mainBinding.getRoot());

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

        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_kz, R.drawable.ic_all},
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

        mainBinding.startAttack.setOnClickListener(view -> {
            InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(mainBinding.getRoot().getWindowToken(), 0);

            String phoneNumber = mainBinding.phoneNumber.getText().toString();
            String repeats = mainBinding.repeats.getText().toString();

            int length = MainViewModel.phoneLength[mainBinding.phoneCode.getSelectedItemPosition()];
            if (phoneNumber.length() != length && length != 0) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            repository.setLastCountryCode(mainBinding.phoneCode.getSelectedItemPosition());
            repository.setLastPhone(phoneNumber);

            model.startAttack(mainBinding.phoneCode.getSelectedItemPosition(), phoneNumber,
                    repeats.isEmpty() ? 1 : Integer.parseInt(repeats));
        });

        mainBinding.startAttack.setOnLongClickListener(view -> {
            InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(mainBinding.getRoot().getWindowToken(), 0);

            String phoneNumber = mainBinding.phoneNumber.getText().toString();
            String repeats = mainBinding.repeats.getText().toString();

            int length = MainViewModel.phoneLength[mainBinding.phoneCode.getSelectedItemPosition()];
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

                    model.scheduleAttack(mainBinding.phoneCode.getSelectedItemPosition(), phoneNumber,
                            repeats.isEmpty() ? 1 : Integer.parseInt(repeats),
                            date.getTimeInMillis(), currentDate.getTimeInMillis());

                    new SettingsDialog().show(getSupportFragmentManager(), null);

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
            if (mainBinding.phoneNumber.getText().toString().isEmpty() && clipText != null && !processText(clipText)) {
                mainBinding.phoneCode.setSelection(repository.getLastCountryCode());
                mainBinding.phoneNumber.setText(repository.getLastPhone());
            }

            return false;
        });

        mainBinding.settings.setOnClickListener(view -> new SettingsDialog().show(getSupportFragmentManager(), null));

        View.OnClickListener telegram = (view) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/androidsmsbomber")));

        mainBinding.telegramUrl.setOnClickListener(telegram);
        mainBinding.telegramIcon.setOnClickListener(telegram);

        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_DIAL.equals(intent.getAction()))
                processText(intent.getData().getSchemeSpecificPart());

            if (intent.hasExtra(TASK_ID)) {
                workManager.cancelWorkById(UUID.fromString(intent.getStringExtra(TASK_ID)));
                new SettingsDialog().show(getSupportFragmentManager(), null);
            }
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidsmsbomber-default-rtdb.europe-west1.firebasedatabase.app");
        database.getReference().child("updates").get().addOnSuccessListener(dataSnapshot -> {
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
        });
    }

    private boolean processText(String data) {
        if (data.matches("(8|\\+(7|380))([0-9()\\-\\s])*")) {

            if (data.startsWith("8"))
                data = "+7" + data.substring(1);

            data = data.substring(1);
            for (int i = 0; i < MainViewModel.countryCodes.length; i++) {
                if (data.startsWith(MainViewModel.countryCodes[i])) {
                    mainBinding.phoneCode.setSelection(i);
                    mainBinding.phoneNumber.setText(data.substring(MainViewModel.countryCodes[i].length()).replaceAll("[^\\d.]", ""));

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
                mainBinding.blur.setImageBitmap(Blurry.with(MainActivity.this)
                        .radius(20)
                        .sampling(1)
                        .capture(mainBinding.getRoot())
                        .get());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            mainBinding.main.setVisibility(View.GONE);
            mainBinding.attackScreen.setVisibility(View.VISIBLE);

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