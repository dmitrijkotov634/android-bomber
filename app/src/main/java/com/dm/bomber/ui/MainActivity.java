package com.dm.bomber.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.dm.bomber.AttackManager;
import com.dm.bomber.R;
import com.dm.bomber.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AttackManager attackManager;

    private final String[] phoneCodes = {"7", "380", "375", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CountryCodeAdapter adapter = new CountryCodeAdapter(this,
                new int[]{R.drawable.ic_ru, R.drawable.ic_uk, R.drawable.ic_by, R.drawable.ic_all},
                phoneCodes);

        String[] hints = getResources().getStringArray(R.array.hints);
        binding.phoneNumber.setHint(hints[0]);

        binding.countrySelect.setAdapter(adapter);
        binding.countrySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                binding.phoneNumber.setHint(hints[index]);
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
                        binding.attackScreen.setVisibility(View.GONE);

                        binding.fogging.setAlpha(0.7f);
                        binding.fogging.animate()
                                .alpha(0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.fogging.setVisibility(View.GONE);
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
                        binding.progress.setMax(serviceCount * numberOfCycles);
                        binding.progress.setProgress(0);

                        binding.fogging.setVisibility(View.VISIBLE);
                        binding.attackScreen.setVisibility(View.VISIBLE);

                        binding.fogging.setAlpha(0f);
                        binding.fogging.animate()
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
                        binding.progress.setProgress(progress);
                    }
                });
            }
        });

        binding.startAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = binding.phoneNumber.getText().toString();
                String numberOfCycles = binding.cyclesCount.getText().toString();

                if (phoneNumber.length() < 7) {
                    binding.phoneNumber.setError(getString(R.string.length_error));
                    return;
                }

                int numberOfCyclesNum;
                if (numberOfCycles.isEmpty() || numberOfCycles.equals("-") ||
                        (numberOfCyclesNum = Integer.parseInt(numberOfCycles)) < 0) {
                    binding.cyclesCount.setError(getString(R.string.cycles_error));
                    return;
                }

                attackManager.performAttack(phoneCodes[binding.countrySelect.getSelectedItemPosition()], phoneNumber, numberOfCyclesNum);
            }
        });
    }

    public void setEnabledMain(boolean enabled) {
        binding.phoneNumber.setEnabled(enabled);
        binding.cyclesCount.setEnabled(enabled);
        binding.countrySelect.setEnabled(enabled);
        binding.startAttack.setEnabled(enabled);
    }

    @Override
    public void onBackPressed() {
        if (attackManager.hasAttack())
            attackManager.stopAttack();
        else
            super.onBackPressed();
    }
}