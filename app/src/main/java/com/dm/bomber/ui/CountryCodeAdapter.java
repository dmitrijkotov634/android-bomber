package com.dm.bomber.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dm.bomber.R;

public class CountryCodeAdapter extends BaseAdapter {
    private final int[] flags;
    private final String[] countryCodes;

    private final LayoutInflater inflter;

    public CountryCodeAdapter(Activity context, int[] flags, String[] countryCodes) {
        this.flags = flags;
        this.countryCodes = countryCodes;

        inflter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.country_code_row, null);

        ImageView icon = view.findViewById(R.id.icon_flag);
        TextView names = view.findViewById(R.id.country_code);

        icon.setImageResource(flags[index]);
        names.setText("+" + countryCodes[index]);

        return view;
    }
}