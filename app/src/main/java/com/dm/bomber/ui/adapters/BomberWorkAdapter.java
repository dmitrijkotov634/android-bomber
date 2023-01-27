package com.dm.bomber.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import com.dm.bomber.BuildVars;
import com.dm.bomber.databinding.AttackWorkRowBinding;
import com.dm.bomber.ui.MainViewModel;
import com.dm.bomber.worker.AttackWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BomberWorkAdapter extends RecyclerView.Adapter<BomberWorkAdapter.ViewHolder> {

    private List<WorkInfo> workInfo = new ArrayList<>();

    private final Context context;
    private final Callback callback;

    public BomberWorkAdapter(LifecycleOwner owner, Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AttackWorkRowBinding rowItem = AttackWorkRowBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkInfo workInfo = this.workInfo.get(position);

        boolean isRunning = workInfo.getState().equals(WorkInfo.State.RUNNING);
        holder.binding.taskProgress.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        holder.binding.taskTime.setVisibility(isRunning ? View.GONE : View.VISIBLE);

        if (isRunning) {
            holder.binding.taskProgress.setMax(workInfo.getProgress().getInt(MainViewModel.KEY_MAX_PROGRESS, 0));
            holder.binding.taskProgress.setProgress(workInfo.getProgress().getInt(MainViewModel.KEY_PROGRESS, 0));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        for (String tag : workInfo.getTags()) {
            if (tag.startsWith(AttackWorker.class.getCanonicalName()) || tag.equals(MainViewModel.ATTACK))
                continue;

            String[] parts = tag.split(";");

            holder.binding.taskTitle.setText(parts[0]);

            for (int i = 0; i < BuildVars.COUNTRY_CODES.length; i++) {
                if (parts[0].substring(1).startsWith(BuildVars.COUNTRY_CODES[i])) {
                    holder.binding.countryFlag.setImageResource(BuildVars.COUNTRY_FLAGS[i]);
                    break;
                }
            }

            if (parts.length == 2)
                holder.binding.taskTime.setText(dateFormat.format(new Date(Long.parseLong(parts[1]))));
        }
    }

    public void setWorkInfo(List<WorkInfo> workInfo) {
        this.workInfo = workInfo;
    }

    @Override
    public int getItemCount() {
        return workInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final AttackWorkRowBinding binding;

        public ViewHolder(AttackWorkRowBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

            binding.stopAttack.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onTaskStopClicked(workInfo.get(getLayoutPosition()));
        }
    }

    public interface Callback {
        void onTaskStopClicked(WorkInfo workInfo);
    }
}
