package com.nguyen.fetchrewards

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nguyen.fetchrewards.databinding.ItemEmployeeBinding

class HireAdapter(private val context: Context, private val hires: MutableList<Hire>) : RecyclerView.Adapter<HireAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hire: Hire) {
            binding.textListId.text = hire.listId.toString()
            binding.textName.text = hire.name
            binding.textId.text = hire.id.toString()
        }
    }

    private lateinit var binding: ItemEmployeeBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        binding = ItemEmployeeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hire = hires[position]
        holder.bind(hire)
    }

    override fun getItemCount() = hires.size
}
