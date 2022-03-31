package com.dm.bomber.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import com.dm.bomber.databinding.WorkItemBinding;
import com.dm.bomber.workers.AttackWorker;

import java.util.List;

public class BomberWorkAdapter extends RecyclerView.Adapter<BomberWorkAdapter.ViewHolder> {
    private List<WorkInfo> workInfos;

    private final Context context;
    public Callback callback;

    @SuppressLint("NotifyDataSetChanged")
    public BomberWorkAdapter(LifecycleOwner context, LiveData<List<WorkInfo>> data, Callback callback) {
        this.context = (Context) context;
        this.callback = callback;

        data.observe(context, workInfosResult -> {
            workInfos = workInfosResult;

            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WorkItemBinding rowItem = WorkItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkInfo workInfo = workInfos.get(position);

        boolean isRunning = workInfo.getState().equals(WorkInfo.State.RUNNING);
        holder.binding.taskProgress.setVisibility(isRunning ? View.VISIBLE : View.INVISIBLE);
        holder.binding.taskTime.setVisibility(isRunning ? View.GONE : View.VISIBLE);

        if (isRunning) {
            holder.binding.taskProgress.setMax(workInfo.getProgress().getInt(AttackWorker.KEY_MAX_PROGRESS, 0));
            holder.binding.taskProgress.setProgress(workInfo.getProgress().getInt(AttackWorker.KEY_PROGRESS, 0));
        }

        for (String tag : workInfo.getTags()) {
            if (tag.startsWith("com"))
                continue;

            holder.binding.taskTitle.setText(tag);
        }
    }

    @Override
    public int getItemCount() {
        return workInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final WorkItemBinding binding;

        public ViewHolder(WorkItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            callback.onItemLongClicked(workInfos.get(getLayoutPosition()));

            return true;
        }
    }

    public interface Callback {
        void onItemLongClicked(WorkInfo workInfo);
    }
}
