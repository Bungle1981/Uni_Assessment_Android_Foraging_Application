package com.example.foragingapplicationv2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foragingapplicationv2.R
import com.example.foragingapplicationv2.databinding.ItemsRowBinding
import com.example.foragingapplicationv2.roomdatabase.ForageSpotEntity

class RecyclerItemAdapter(
    private val items: List<ForageSpotEntity>,
    private val deleteListener:(id:Int)->Unit,
    private val gotoMapListener:(id:Int)->Unit
): RecyclerView.Adapter<RecyclerItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvForageName = binding.tvNameOfForage
        val tvAddressDescription = binding.tvAddressDescription
        val tvNotes = binding.tvForageNotes
        val ivDelete = binding.ivDelete
        val ivGoToMap = binding.ivGoToMap
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.tvForageName.text = item.forageType
        holder.tvAddressDescription.text = item.addressDescription
        holder.tvNotes.text = item.notes

        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(item.spotID)
        }

        holder.ivGoToMap.setOnClickListener{
            gotoMapListener.invoke(item.spotID)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}