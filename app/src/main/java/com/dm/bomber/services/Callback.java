package com.dm.bomber.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface Callback extends okhttp3.Callback {
    void onSuccess();

    void onError(Exception e);

    @Override
    default void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        onSuccess();
    }

    @Override
    default void onFailure(@NonNull Call call, @NonNull IOException e) {
        onError(e);
    }
}
