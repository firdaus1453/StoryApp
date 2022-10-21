package com.firdaus1453.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.databinding.ItemStoriesBinding

class HomeAdapter(private val onClickListener: (iv: ImageView, id: String) -> Unit) :
    ListAdapter<Stories, HomeAdapter.HoursViewHolder>(MyDiffUtil) {

    companion object MyDiffUtil : DiffUtil.ItemCallback<Stories>() {
        override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val binding = ItemStoriesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story, onClickListener)
    }

    class HoursViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Stories, onClickListener: (iv: ImageView, id: String) -> Unit) {
            story.apply {
                binding.tvName.text = name
                binding.tvFirstLetterName.text = name?.substring(0, 1)
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
                onClickListener(binding.ivImgPoster, story.id ?: "")
            }
        }
    }

//    class OnClickListener(val onClickListener: (iv: ImageView, id: String) -> Unit) {
//        fun onClick(iv: ImageView, id: String) = onClickListener(iv, id)
//    }
}