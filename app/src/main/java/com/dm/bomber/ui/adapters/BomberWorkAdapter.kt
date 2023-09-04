package com.dm.bomber.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.dm.bomber.BuildVars
import com.dm.bomber.databinding.AttackWorkRowBinding
import com.dm.bomber.ui.MainViewModel
import com.dm.bomber.worker.AttackWorker
import java.text.SimpleDateFormat
import java.util.*

class BomberWorkAdapter(
    private val context: Context,
    private val callback: (WorkInfo?) -> Unit
) :
    RecyclerView.Adapter<BomberWorkAdapter.ViewHolder>() {

    private var workInfo: List<WorkInfo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AttackWorkRowBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workInfo = workInfo[position]

        val isRunning = workInfo.state == WorkInfo.State.RUNNING

        holder.binding.taskProgress.visibility = if (isRunning) View.VISIBLE else View.INVISIBLE
        holder.binding.taskTime.visibility = if (isRunning) View.GONE else View.VISIBLE

        if (isRunning) {
            holder.binding.taskProgress.setMax(workInfo.progress.getInt(MainViewModel.KEY_MAX_PROGRESS, 0))
            holder.binding.taskProgress.setProgress(workInfo.progress.getInt(MainViewModel.KEY_PROGRESS, 0))
        }

        val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

        for (tag in workInfo.tags) {
            if (tag.startsWith(AttackWorker::class.java.getCanonicalName()!!) || tag == MainViewModel.ATTACK)
                continue

            val parts = tag.split(";")

            holder.binding.taskTitle.text = parts[0]
            for (i in BuildVars.COUNTRY_CODES.indices) {
                if (parts[0].substring(1).startsWith(BuildVars.COUNTRY_CODES[i])) {
                    holder.binding.countryFlag.setImageResource(BuildVars.COUNTRY_FLAGS[i])
                    break
                }
            }

            if (parts.size == 2)
                holder.binding.taskTime.text = dateFormat.format(Date(parts[1].toLong()))
        }
    }

    fun setWorkInfo(workInfo: List<WorkInfo>) {
        this.workInfo = workInfo
    }

    override fun getItemCount(): Int = workInfo.size

    inner class ViewHolder(val binding: AttackWorkRowBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    ), View.OnClickListener {
        init {
            binding.stopAttack.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            callback(workInfo[layoutPosition])
        }
    }
}
