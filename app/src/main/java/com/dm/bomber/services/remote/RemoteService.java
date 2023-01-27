package com.dm.bomber.services.remote;

import com.google.gson.annotations.SerializedName;

public class RemoteService {
    @SerializedName("phone_codes")
    private final int[] phoneCodes;
    private final RemoteRequest[] requests;

    public RemoteService(int[] phoneCodes, RemoteRequest[] requests) {
        this.phoneCodes = phoneCodes;
        this.requests = requests;
    }

    public int[] getPhoneCodes() {
        return phoneCodes;
    }

    public RemoteRequest[] getRequests() {
        return requests;
    }
}
