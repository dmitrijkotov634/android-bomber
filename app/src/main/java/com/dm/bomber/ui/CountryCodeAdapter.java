package com.dm.bomber.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dm.bomber.R;

public class CountryCodeAdapter extends BaseAdapter {
    private Context context;
    private int flags[];
    private String[] countryCodes;

    private LayoutInflater inflter;

    public CountryCodeAdapter(Activity context, int[] flags, String[] countryCodes) {
        this.context = context;
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

        ImageView icon = (ImageView) view.findViewById(R.id.icon_flag);
        TextView names = (TextView) view.findViewById(R.id.country_code);

        icon.setImageResource(flags[index]);
        names.setText("+" + countryCodes[index]);

        return view;
    }
}