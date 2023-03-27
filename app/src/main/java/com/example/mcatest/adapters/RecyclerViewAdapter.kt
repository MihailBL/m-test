package com.example.mcatest.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.mcatest.Constants
import com.example.mcatest.databinding.RecyclerViewItemBinding
import com.example.mcatest.dto.MainDTO
import com.example.mcatest.dto.TempDTO
import com.example.mcatest.utils.GetImageFromURL


class RecyclerViewAdapter(private var dataList: MutableList<MainDTO>, private val context: Context): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    /** Listener for each item */
    private var onItemClickListener: OnItemClickListener? = null
    private var tempPosition: Int = 0

    inner class ViewHolder(binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root){
        val title = binding.tvItemTitle
        val image = binding.ivItemThumbnailImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Initialize the temp List
        if (Constants.tList.size < dataList.size){
            Constants.tList.add(position, TempDTO(position, null))
        }

        holder.title.text = this.dataList[position].title

        if(dataList[position].changedImageURIString != null){
            holder.image.setImageBitmap(Constants.tList[position].bitmap)
        }
        else
            GetImageFromURL(holder.image).execute(this.dataList[position].url)

        this.dataList[position].position = position

        holder.itemView.setOnClickListener {
            this.onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return this.dataList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.onItemClickListener = listener
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    /** Method for deleting item */
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: MainDTO){
        this.dataList.removeAt(item.position)
        Constants.tList.removeAt(item.position)      // Remove the corresponding item from temp List also
        notifyDataSetChanged()
    }

    /** Method for updating changes in items */
    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(item: MainDTO){
        this.dataList[item.position].title = item.title
        this.dataList[item.position].changedImageURIString = item.changedImageURIString
        this.tempPosition = item.position
        notifyDataSetChanged()
    }
}