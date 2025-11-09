package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ItemAvatarBinding

typealias AvatarItem = Pair<String, Int>

class AvatarAdapter(
    private val avatars: List<AvatarItem>,
    private val onAvatarClicked: (AvatarItem) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(val binding: ItemAvatarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AvatarViewHolder(binding)
    }

    override fun getItemCount() = avatars.size

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val (name, resourceId) = avatars[position]
        val context = holder.itemView.context

        holder.binding.tvAvatarName.text = name

        Glide.with(context)
            .load(resourceId)
            .into(holder.binding.ivAvatar)

        holder.itemView.setOnClickListener {
            onAvatarClicked(avatars[position])
        }
    }

}
