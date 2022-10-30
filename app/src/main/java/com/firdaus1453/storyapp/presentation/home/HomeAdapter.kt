package com.firdaus1453.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.databinding.ItemStoriesBinding

class HomeAdapter(private val onClickListener: (iv: ImageView, id: String) -> Unit) :
    PagingDataAdapter<StoriesEntity, HomeAdapter.StoryViewHolder>(MyDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoriesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story, onClickListener)
        }
    }

    class StoryViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoriesEntity, onClickListener: (iv: ImageView, id: String) -> Unit) {
            story.apply {
                binding.tvName.text = name
                binding.tvFirstLetterName.text = name.substring(0, 1)
                binding.tvItemDesc.text = description
                Glide.with(itemView.context)
                    .load(photoUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .into(binding.ivImgPoster)
            }
            binding.root.setOnClickListener {
                onClickListener(binding.ivImgPoster, story.id)
            }
        }
    }

    companion object MyDiffUtil : DiffUtil.ItemCallback<StoriesEntity>() {
        override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
            return oldItem.id == newItem.id
        }
    }
}