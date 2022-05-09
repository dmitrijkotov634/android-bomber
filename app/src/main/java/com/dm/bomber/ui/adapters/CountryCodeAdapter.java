package com.dm.bomber.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dm.bomber.databinding.CountryCodeRowBinding;

public class CountryCodeAdapter extends BaseAdapter {

    private final int[] flags;
    private final String[] countryCodes;

    private final LayoutInflater inflater;

    public CountryCodeAdapter(Activity context, int[] flags, String[] countryCodes) {
        this.flags = flags;
        this.countryCodes = countryCodes;

        inflater = LayoutInflater.from(context);
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
        ViewHolder holder;

        if (view == null) {
            CountryCodeRowBinding binding = CountryCodeRowBinding.inflate(inflater);

            view = binding.getRoot();
            view.setTag(holder = new ViewHolder(binding));
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.binding.icon.setImageResource(flags[index]);
        holder.binding.code.setText(String.format("+%s", countryCodes[index]));

        return view;
    }

    private static class ViewHolder {
        CountryCodeRowBinding binding;

        public ViewHolder(CountryCodeRowBinding binding) {
            this.binding = binding;
        }
    }
}