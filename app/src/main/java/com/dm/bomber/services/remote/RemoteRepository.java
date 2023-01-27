package com.dm.bomber.services.remote;

import com.dm.bomber.services.core.Callback;
import com.dm.bomber.services.core.Phone;
import com.dm.bomber.services.core.Service;
import com.dm.bomber.services.core.ServicesRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRepository implements ServicesRepository {

    private final OkHttpClient client;
    private final String url;

    public RemoteRepository(OkHttpClient client, String url) {
        this.client = client;
        this.url = url;
    }

    private final static Pattern phonePattern = Pattern.compile("\\{formatted_phone\\:(.*)\\}");

    @Override
    public List<Service> collect() {
        ArrayList<Service> services = new ArrayList<>();

        try {
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .get()
                    .build()).execute();

            List<RemoteService> remoteServices = new Gson().fromJson(
                    Objects.requireNonNull(response.body()).string(),
                    new TypeToken<List<RemoteService>>() {
                    }.getType());

            for (RemoteService remoteService : remoteServices) {
                for (RemoteRequest request : remoteService.getRequests()) {
                    services.add(
                            new Service(remoteService.getPhoneCodes()) {
                                @Override
                                public void run(OkHttpClient client, Callback callback, Phone phone) {
                                    RequestBody body = null;
                                    if (request.getJson() != null) {
                                        body = RequestBody.create(inject(new Gson().toJson(request.getJson()), phone),
                                                MediaType.parse("application/json"));
                                    } else if (request.getData() != null) {
                                        FormBody.Builder formBody = new FormBody.Builder();
                                        for (Map.Entry<String, String> entry : request.getData().entrySet())
                                            formBody.add(entry.getKey(), inject(entry.getValue(), phone));
                                        body = formBody.build();
                                    }

                                    Headers.Builder headersBuilder = new Headers.Builder();
                                    if (request.getHeaders() != null)
                                        for (Map.Entry<String, String> entry : request.getHeaders().entrySet())
                                            headersBuilder.add(entry.getKey(), inject(entry.getValue(), phone));

                                    client.newCall(new Request.Builder()
                                                    .url(inject(request.getUrl(), phone))
                                                    .headers(headersBuilder.build())
                                                    .method(request.getMethod(), body)
                                                    .build())
                                            .enqueue(callback);
                                }

                                private String inject(String text, Phone phone) {
                                    String newString = text;

                                    Matcher matcher = phonePattern.matcher(text);
                                    if (matcher.find())
                                        newString = newString.replace(matcher.group(),
                                                format(phone.toString(), Objects.requireNonNull(matcher.group(1))));

                                    return newString
                                            .replaceAll("\\{full_phone\\}", phone.toString())
                                            .replaceAll("\\{phone\\}", phone.getPhone());
                                }
                            }
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }
}
