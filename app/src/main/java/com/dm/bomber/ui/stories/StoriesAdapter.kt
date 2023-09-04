package com.dm.bomber.ui.stories

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dm.bomber.R
import com.dm.bomber.databinding.StoryCardBinding


class StoriesAdapter(
    val stories: List<Story>,
    val next: () -> Boolean,
    val previous: () -> Boolean,
    val dismiss: () -> Unit
) :
    RecyclerView.Adapter<StoriesAdapter.PagerVH>() {

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StoryCardBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.story_card, parent, false))

    override fun getItemCount(): Int = stories.size

    private val selections = MutableList(stories.size) { 0 }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.binding.run {
        val story = stories[position]

        caption.movementMethod = LinkMovementMethod.getInstance()
        captionTop.movementMethod = LinkMovementMethod.getInstance()
        captionCenter.movementMethod = LinkMovementMethod.getInstance()

        indicator.initDots(story.pages.size)
        indicator.setDotSelection(selections[position])

        root.setOnClickListener { dismiss() }

        indicator.onSelectListener = {
            selections[position] = it
            selectPage(position)
        }

        image.post { selectPage(position) }
        image.setOnTouchListener onClick@{ image, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.x > image.x + image.width / 2) {
                    if (selections[position] == story.pages.size - 1)
                        return@onClick next()
                    selections[position]++
                } else {
                    if (selections[position] == 0)
                        return@onClick previous()
                    selections[position]--
                }
                selectPage(position)
            }
            true
        }
    }

    private fun StoryCardBinding.selectPage(position: Int) {
        val currentPage = stories[position].pages[selections[position]]

        indicator.setDotSelection(selections[position])

        caption.text = HtmlCompat.fromHtml(currentPage.caption, HtmlCompat.FROM_HTML_MODE_LEGACY)
        captionTop.text = HtmlCompat.fromHtml(currentPage.captionTop, HtmlCompat.FROM_HTML_MODE_LEGACY)
        captionCenter.text = HtmlCompat.fromHtml(currentPage.captionCenter, HtmlCompat.FROM_HTML_MODE_LEGACY)

        Glide
            .with(root.context)
            .load(currentPage.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(70))
            .into(image)
    }
}