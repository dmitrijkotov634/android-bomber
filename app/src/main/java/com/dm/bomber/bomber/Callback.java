package com.dm.bomber.bomber;

public interface Callback {
    void onAttackEnd(boolean success);

    void onAttackStart(int serviceCount, int numberOfCycles);

    void onProgressChange(int progress);
}
