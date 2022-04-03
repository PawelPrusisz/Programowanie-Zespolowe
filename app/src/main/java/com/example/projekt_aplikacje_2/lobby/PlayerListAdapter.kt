package com.example.projekt_aplikacje_2.lobby

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PlayerListAdapter() : ListAdapter<Pair<String, Int>, PlayerViewHolder>(PlayerDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



    class PlayerDiffCallback : DiffUtil.ItemCallback<Pair<String, Int>>() {
        override fun areItemsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>): Boolean =
            oldItem.first == newItem.first

        override fun areContentsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>): Boolean =
            oldItem == newItem

    }
}