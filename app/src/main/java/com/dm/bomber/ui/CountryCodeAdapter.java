package com.dm.bomber.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dm.bomber.databinding.CountryCodeRowBinding;

public class CountryCodeAdapter extends BaseAdapter {
    private CountryCodeRowBinding binding;

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
    public View getView(int index, View view, ViewGroup parent) {
        binding = CountryCodeRowBinding.inflate(inflter);

        binding.icon.setImageResource(flags[index]);
        binding.code.setText("+" + countryCodes[index]);

        return binding.getRoot();
    }
}