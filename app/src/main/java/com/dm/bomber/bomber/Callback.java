package com.dm.bomber.bomber;

public interface Callback {
    void onAttackEnd();

    void onAttackStart(int serviceCount, int numberOfCycles);

    void onProgressChange(int progress);
}
