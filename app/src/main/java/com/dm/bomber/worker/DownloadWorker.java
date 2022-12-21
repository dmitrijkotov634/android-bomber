package com.dm.bomber.worker;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dm.bomber.BuildConfig;
import com.dm.bomber.ui.MainViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadWorker extends Worker {

    public static final String URL = "url";

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String filePath = getApplicationContext().getExternalFilesDir("/") + "update.apk";

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(getInputData().getString(URL)))
                .build();

        try {
            Response response = new OkHttpClient()
                    .newCall(request)
                    .execute();

            ResponseBody body = response.body();
            InputStream input = Objects.requireNonNull(body).byteStream();
            FileOutputStream output = new FileOutputStream(filePath, false);
            byte[] dataBuffer = new byte[1024];

            int readBytes, total = 0;
            int lastProgress = 0;
            while ((readBytes = input.read(dataBuffer)) != -1) {
                total += readBytes;
                int progress = (int) (total * 100 / body.contentLength());
                if (progress != lastProgress)
                    setProgressAsync(
                            new Data.Builder()
                                    .putInt(MainViewModel.KEY_PROGRESS, progress)
                                    .putInt(MainViewModel.KEY_MAX_PROGRESS, 100)
                                    .build());
                lastProgress = progress;

                output.write(dataBuffer, 0, readBytes);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(
                    FileProvider.getUriForFile(
                            getApplicationContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(filePath)
                    ),
                    "application/vnd.android.package-archive"
            );

            getApplicationContext().startActivity(intent);
            return Result.success();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
