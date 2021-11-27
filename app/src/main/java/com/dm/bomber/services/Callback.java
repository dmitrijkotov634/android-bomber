package com.dm.bomber.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;

public interface Callback extends okhttp3.Callback {

    void onError(Exception e);

    @Override
    default void onFailure(@NonNull Call call, @NonNull IOException e) {
        onError(e);
    }
}
