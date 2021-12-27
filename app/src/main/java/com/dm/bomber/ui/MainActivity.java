package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dm.bomber.MainRepository;
import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;
import com.dm.bomber.databinding.DialogProxiesBinding;
import com.dm.bomber.databinding.DialogSettingsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private DialogSettingsBinding settingsBinding;

    private MainViewModel model;
    private MainRepository repository;

    private String clipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repository = new MainRepository(this);
        model = new ViewModelProvider(this,
                new MainModelFactory(repository)).get(MainViewModel.class);

        AppCompatDelegate.setDefaultNightMode(repository.getTheme());

        super.onCreate(savedInstanceState);
        DynamicColors.applyIfAvailable(this);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        settingsBinding = DialogSettingsBinding.inflate(getLayoutInflater());

        DialogProxiesBinding proxyBinding = DialogProxiesBinding.inflate(getLayoutInflater());
        proxyBinding.proxies.setText(repository.getRawProxy());

        BottomSheetDialog proxyDialog = new BottomSheetDialog(this);
        proxyDialog.setContentView(proxyBinding.getRoot());
        proxyBinding.save.setOnClickListener(view -> {
            try {
                repository.parseProxy(proxyBinding.proxies.getText().toString());
                repository.setRawProxy(proxyBinding.proxies.getText().toString());

                proxyDialog.cancel();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                Snackbar.make(proxyBinding.getRoot(), R.string.proxy_format_error, Snackbar.LENGTH_LONG).show();
            }
        });

        TooltipCompat.setTooltipText(proxyBinding.save, getString(R.string.save));

        setContentView(mainBinding.getRoot());

        model.isSnowfallEnabled().observe(this, enabled -> {
            if (enabled)
                mainBinding.snowfall.setVisibility(View.VISIBLE);
        });

        model.isPromotionShown().observe(this, shown -> {
            if (!shown)
                new MaterialAlertDialogBuilder(this)
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
                mainBinding.main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Blurry.with(MainActivity.this)
                                .radius(20)
                                .sampling(1)
                                .async()
                                .capture(mainBinding.main)
                                .getAsync(bitmap -> {
                                    mainBinding.blur.setImageBitmap(bitmap);

                                    mainBinding.blur.setVisibility(View.VISIBLE);
                                    mainBinding.main.setVisibility(View.INVISIBLE);

                                    mainBinding.attack.setVisibility(View.VISIBLE);
                                });

                        mainBinding.main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

                mainBinding.main.requestLayout();
            } else {
                mainBinding.main.setVisibility(View.VISIBLE);
                mainBinding.blur.setVisibility(View.GONE);
                mainBinding.attack.setVisibility(View.GONE);
            }
        });

        CountryCodeAdapter adapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_all},
                MainViewModel.phoneCodes,
                getThemeColor(R.attr.colorOnSecondary));

        String[] hints = getResources().getStringArray(R.array.hints);
        mainBinding.phoneNumber.setHint(hints[0]);

        mainBinding.footer.setMovementMethod(LinkMovementMethod.getInstance());

        mainBinding.phoneCode.setAdapter(adapter);
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
            String numberOfCycles = mainBinding.cyclesCount.getText().toString();

            if (phoneNumber.length() < 7) {
                Snackbar.make(view, R.string.phone_error, Snackbar.LENGTH_LONG).show();
                return;
            }

            int numberOfCyclesNum = numberOfCycles.isEmpty() ? 1 : Integer.parseInt(numberOfCycles);

            model.startAttack(mainBinding.phoneCode.getSelectedItemPosition(), phoneNumber, numberOfCyclesNum);
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

        mainBinding.bomb.setOnLongClickListener(view -> {
            model.enableSnowfall();
            return true;
        });

        mainBinding.phoneNumber.setOnLongClickListener(view -> {
            if (mainBinding.phoneNumber.getText().toString().isEmpty() &&
                    clipText != null &&
                    clipText.matches("(8|\\+(7|380))([0-9()\\-\\s])*")) {

                if (clipText.startsWith("8"))
                    clipText = "+7" + clipText.substring(1);

                clipText = clipText.substring(1);
                for (int i = 0; i < MainViewModel.phoneCodes.length; i++) {
                    if (clipText.startsWith(MainViewModel.phoneCodes[i])) {
                        mainBinding.phoneCode.setSelection(i);
                        mainBinding.phoneNumber.setText(clipText.substring(MainViewModel.phoneCodes[i].length()).replaceAll("[^\\d.]", ""));

                        break;
                    }
                }
            }

            return false;
        });

        BottomSheetDialog settings = new BottomSheetDialog(this);
        settings.setContentView(settingsBinding.getRoot());

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

        settingsBinding.donateTile.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://qiwi.com/n/PHOSS105"))));

        int lastPhoneCode = repository.getLastPhoneCode();
        if (lastPhoneCode > 2)
            lastPhoneCode = 0;

        mainBinding.phoneCode.setSelection(lastPhoneCode);
        mainBinding.phoneNumber.setText(repository.getLastPhone());

        if (DynamicColors.isDynamicColorAvailable())
            mainBinding.footer.setBackgroundColor(getThemeColor(R.attr.colorOnPrimary));
    }

    public void setCurrentTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        repository.setTheme(theme);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            if (clipboard.hasPrimaryClip())
                clipText = clipboard.getPrimaryClip().getItemAt(0).coerceToText(this).toString();
        }
    }

    @Override
    public void onBackPressed() {
        if (!model.stopAttack())
            super.onBackPressed();
    }

    public int getThemeColor(@AttrRes int attrRes) {
        int materialColor = MaterialColors.getColor(this, attrRes, Color.BLUE);

        if (materialColor < 0)
            return materialColor;

        TypedValue resolvedAttr = new TypedValue();
        getTheme().resolveAttribute(attrRes, resolvedAttr, true);

        return ContextCompat.getColor(this,
                resolvedAttr.resourceId == 0 ? resolvedAttr.data : resolvedAttr.resourceId);
    }
}