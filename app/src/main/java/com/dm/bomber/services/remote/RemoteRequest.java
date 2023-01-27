package com.dm.bomber.services.remote;

import java.util.HashMap;

public class RemoteRequest {
    private final String method;
    private final String url;
    private final Object json;
    private final HashMap<String, String> data;
    private final HashMap<String, String> headers;

    public RemoteRequest(String method, String url, Object json, HashMap<String, String> data, HashMap<String, String> headers) {
        this.method = method;
        this.url = url;
        this.json = json;
        this.data = data;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public Object getJson() {
        return json;
    }

    public String getUrl() {
        return url;
    }
}
