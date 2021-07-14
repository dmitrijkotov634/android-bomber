package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.dm.bomber.AttackManager;
import com.dm.bomber.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {
    private Spinner phoneCode;
    private EditText phoneNumber;
    private EditText numberOfCycles;
    private CircularProgressIndicator attackProgress;
    private Chip startAttack;

    private LinearLayout main;
    private FrameLayout darkLayout;
    private LinearLayout attackScreen;
    private AttackManager attackManager;

    private String[] phoneCodes = {"7", "380", "375", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneCode = findViewById(R.id.country_select);
        phoneNumber = findViewById(R.id.phone_number);
        numberOfCycles = findViewById(R.id.cycles_count);
        attackProgress = findViewById(R.id.progress);
        startAttack = findViewById(R.id.start_attack);

        main = findViewById(R.id.main);
        darkLayout = findViewById(R.id.dark_layout);
        attackScreen = findViewById(R.id.attack_screen);

        CountryCodeAdapter adapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_by, R.drawable.ic_all},
                phoneCodes);

        String[] hints = getResources().getStringArray(R.array.hints);
        phoneNumber.setHint(hints[0]);

        phoneCode.setAdapter(adapter);
        phoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                phoneNumber.setHint(hints[index]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        attackManager = new AttackManager(new AttackManager.AttackCallback() {
            @Override
            public void onAttackEnd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attackScreen.setVisibility(View.GONE);

                        darkLayout.setAlpha(0.7f);
                        darkLayout.animate()
                                .alpha(0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        darkLayout.setVisibility(View.GONE);
                                        setEnabledMain(true);
                                    }
                                });
                    }
                });
            }

            @Override
            public void onAttackStart(int serviceCount, int numberOfCycles) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attackProgress.setMax(serviceCount * numberOfCycles);
                        attackProgress.setProgress(0);

                        darkLayout.setVisibility(View.VISIBLE);
                        attackScreen.setVisibility(View.VISIBLE);

                        darkLayout.setAlpha(0f);
                        darkLayout.animate()
                                .alpha(0.7f)
                                .setListener(null);

                        setEnabledMain(false);
                    }
                });
            }


            @Override
            public void onProgressChange(int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attackProgress.setProgress(progress);
                    }
                });
            }
        });

        startAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inumberOfCycles;

                if (phoneNumber.getText().toString().length() < 7) {
                    phoneNumber.setError(getString(R.string.length_error));
                    return;
                }

                if (numberOfCycles.getText().toString().isEmpty() ||
                        numberOfCycles.getText().toString().equals("-") ||
                        (inumberOfCycles = Integer.parseInt(numberOfCycles.getText().toString())) < 0) {

                    numberOfCycles.setError(getString(R.string.cycles_error));
                    return;
                }

                attackManager.performAttack(phoneCodes[phoneCode.getSelectedItemPosition()],
                        phoneNumber.getText().toString(), inumberOfCycles);
            }
        });
    }

    public void setEnabledMain(boolean enabled) {
        phoneNumber.setEnabled(enabled);
        phoneCode.setEnabled(enabled);
        startAttack.setEnabled(enabled);
        numberOfCycles.setEnabled(enabled);
    }

    @Override
    public void onBackPressed() {
        if (attackManager.hasAttack())
            attackManager.stopAttack();
        else
            super.onBackPressed();
    }
}