package com.dm.bomber;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

import com.dm.bomber.ui.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QSTile extends TileService {
    @Override
    public void onClick() {
        startActivityAndCollapse(new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
