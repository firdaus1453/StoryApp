package com.firdaus1453.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.databinding.ItemStoriesBinding

class HomeAdapter(private val storiesList: List<Stories> = listOf()) :
    RecyclerView.Adapter<HomeAdapter.HoursViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val binding = ItemStoriesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursViewHolder(binding)
    }

    override fun getItemCount() = storiesList.size

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        with(holder) {
            with(storiesList[position]) {
                binding.tvName.text = name
                binding.tvFirstLetterName.text = name?.substring(0,1)
                binding.tvItemDesc.text = description
                Glide.with(itemView.context)
                    .load(photoUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .into(binding.ivImgPoster)

                holder.itemView.setOnClickListener {
                    Toast.makeText(
                        holder.itemView.context, name,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    inner class HoursViewHolder(val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root)
}