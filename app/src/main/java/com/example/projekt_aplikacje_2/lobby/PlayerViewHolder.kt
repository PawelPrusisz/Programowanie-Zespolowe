package com.example.projekt_aplikacje_2.lobby

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_aplikacje_2.R
import com.example.projekt_aplikacje_2.databinding.ItemPlayerListBinding

class PlayerViewHolder private constructor(private val binding: ItemPlayerListBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : PlayerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlayerListBinding.inflate(inflater, parent, false)
            return PlayerViewHolder(binding)
        }
    }

    fun bind(player : Pair<String, Int>) {
        binding.playerName.text = player.first
        binding.root.backgroundTintList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(player.second))
    }
}