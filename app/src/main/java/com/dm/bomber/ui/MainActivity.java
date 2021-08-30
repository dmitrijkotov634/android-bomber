package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.dm.bomber.AppPreferences;
import com.dm.bomber.AttackManager;
import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity implements AttackManager.Callback {
    private ActivityMainBinding binding;

    private AttackManager attackManager;
    private AppPreferences preferences;

    private final String[] phoneCodes = {"7", "380", "375", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new AppPreferences(this);
        AppCompatDelegate.setDefaultNightMode(preferences.getTheme());

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CountryCodeAdapter adapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_by, R.drawable.ic_all},
                phoneCodes);

        String[] hints = getResources().getStringArray(R.array.hints);
        binding.phoneNumber.setHint(hints[0]);

        binding.footer.setMovementMethod(LinkMovementMethod.getInstance());

        binding.phoneCode.setAdapter(adapter);
        binding.phoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                binding.phoneNumber.setHint(hints[index]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        attackManager = new AttackManager(this);
        attackManager.setIgnoreCode(preferences.getIgnoreCode());

        binding.startAttack.setOnClickListener(view -> {
            String phoneNumber = binding.phoneNumber.getText().toString();
            String numberOfCycles = binding.cyclesCount.getText().toString();

            if (phoneNumber.length() < 7) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            int numberOfCyclesNum = numberOfCycles.isEmpty() ? 1 : Integer.parseInt(numberOfCycles);

            if (!attackManager.hasAttack())
                attackManager.performAttack(phoneCodes[binding.phoneCode.getSelectedItemPosition()], phoneNumber, numberOfCyclesNum);
        });

        binding.openMenu.setOnClickListener(view -> {
            binding.menu.setVisibility(View.VISIBLE);
            blurMain(true);
        });

        binding.appThemeTile.setOnClickListener(view -> {
            int mode = binding.appThemeTile.isChecked() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

            preferences.setTheme(mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        binding.appThemeTile.setOnLongClickListener(view -> {
            preferences.setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

            return true;
        });

        binding.donateTile.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://qiwi.com/n/PHOSS105"))));

        binding.appThemeTile.setChecked((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        binding.ignoreCode.setChecked(preferences.getIgnoreCode());

        binding.ignoreCode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.setIgnoreCode(isChecked);
            attackManager.setIgnoreCode(isChecked);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (attackManager.hasAttack() || !binding.phoneNumber.getText().toString().isEmpty())
            return;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (!clipboard.hasPrimaryClip())
            return;

        String text = clipboard.getPrimaryClip().getItemAt(0).coerceToText(this).toString();

        if (text.matches("\\+(7|380|375)([0-9()\\-\\s])*")) {
            text = text.substring(1);

            for (int i = 0; i < phoneCodes.length; i++) {
                if (text.startsWith(phoneCodes[i])) {
                    binding.phoneCode.setSelection(i);
                    binding.phoneNumber.setText(text.substring(phoneCodes[i].length()).replaceAll("[^\\d.]", ""));

                    return;
                }
            }
        }
    }

    @Override
    public void onAttackEnd() {
        runOnUiThread(() -> {
            binding.attack.setVisibility(View.GONE);
            blurMain(false);
        });
    }

    @Override
    public void onAttackStart(int serviceCount, int numberOfCycles) {
        runOnUiThread(() -> {
            InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

            binding.progress.setMax(serviceCount * numberOfCycles);
            binding.progress.setProgress(0);

            binding.attack.setVisibility(View.VISIBLE);
            blurMain(true);
        });
    }

    @Override
    public void onProgressChange(int progress) {
        runOnUiThread(() -> binding.progress.setProgress(progress));
    }

    private void blurMain(boolean visible) {
        if (visible) {
            binding.blur.setImageBitmap(Blurry.with(this)
                    .sampling(1)
                    .radius(20)
                    .capture(binding.main)
                    .get());

            binding.blur.setVisibility(View.VISIBLE);
            binding.main.setVisibility(View.GONE);
        } else {
            binding.main.setVisibility(View.VISIBLE);

            binding.blur.animate()
                    .alpha(0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.blur.setAlpha(1f);
                            binding.blur.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (attackManager.hasAttack())
            attackManager.stopAttack();
        else if (binding.menu.getVisibility() == View.VISIBLE) {
            binding.menu.setVisibility(View.GONE);
            blurMain(false);
        } else
            super.onBackPressed();
    }
}