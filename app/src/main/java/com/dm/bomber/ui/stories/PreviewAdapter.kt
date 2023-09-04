package com.dm.bomber.ui.stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dm.bomber.R
import com.dm.bomber.databinding.StoryPreviewCardBinding

class PreviewAdapter(
    private val stories: List<Story>,
    private val open: (Int) -> Unit
) : RecyclerView.Adapter<PreviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = StoryPreviewCardBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.story_preview_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide
            .with(holder.binding.root.context)
            .load(stories[position].preview)
            .override(144, 144)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade(150))
            .into(holder.binding.preview)

        holder.binding.root.setOnClickListener {
            open(position)
        }
    }

    override fun getItemCount() = stories.size
}