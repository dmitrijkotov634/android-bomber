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
    private Spinner phoneCodeView;
    private EditText phoneNumberView;
    private EditText numberOfCyclesView;
    private CircularProgressIndicator attackProgressView;
    private Chip startAttackButton;

    private FrameLayout darkLayout;
    private LinearLayout attackScreenView;

    private AttackManager attackManager;

    private final String[] phoneCodes = {"7", "380", "375", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneCodeView = findViewById(R.id.country_select);
        phoneNumberView = findViewById(R.id.phone_number);
        numberOfCyclesView = findViewById(R.id.cycles_count);
        attackProgressView = findViewById(R.id.progress);
        startAttackButton = findViewById(R.id.start_attack);

        darkLayout = findViewById(R.id.dark_layout);
        attackScreenView = findViewById(R.id.attack_screen);

        CountryCodeAdapter adapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_by, R.drawable.ic_all},
                phoneCodes);

        String[] hints = getResources().getStringArray(R.array.hints);
        phoneNumberView.setHint(hints[0]);

        phoneCodeView.setAdapter(adapter);
        phoneCodeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                phoneNumberView.setHint(hints[index]);
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
                        attackScreenView.setVisibility(View.GONE);

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
                        attackProgressView.setMax(serviceCount * numberOfCycles);
                        attackProgressView.setProgress(0);

                        darkLayout.setVisibility(View.VISIBLE);
                        attackScreenView.setVisibility(View.VISIBLE);

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
                        attackProgressView.setProgress(progress);
                    }
                });
            }
        });

        startAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberView.getText().toString();
                String numberOfCycles = numberOfCyclesView.getText().toString();

                if (phoneNumber.length() < 7) {
                    phoneNumberView.setError(getString(R.string.length_error));
                    return;
                }

                int numberOfCyclesNum;
                if (numberOfCycles.isEmpty() || numberOfCycles.equals("-") ||
                        (numberOfCyclesNum = Integer.parseInt(numberOfCycles)) < 0) {
                    numberOfCyclesView.setError(getString(R.string.cycles_error));
                    return;
                }

                attackManager.performAttack(phoneCodes[phoneCodeView.getSelectedItemPosition()], phoneNumber, numberOfCyclesNum);
            }
        });
    }

    public void setEnabledMain(boolean enabled) {
        phoneNumberView.setEnabled(enabled);
        phoneCodeView.setEnabled(enabled);
        startAttackButton.setEnabled(enabled);
        numberOfCyclesView.setEnabled(enabled);
    }

    @Override
    public void onBackPressed() {
        if (attackManager.hasAttack())
            attackManager.stopAttack();
        else
            super.onBackPressed();
    }
}