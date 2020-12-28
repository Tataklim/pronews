package com.example.pronews.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pronews.R
import com.example.pronews.fragments.DEFAULT_IMAGE
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication

class ListAdapter(private val clickListener: (SingleNews) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ElemViewHolder>() {
    var data = mutableListOf<SingleNews>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElemViewHolder {
        return ElemViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ElemViewHolder, position: Int) {
        val item = data[position]
        holder.setDataAndListener(item, clickListener)
    }

    class ElemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleId)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryId)
        private val countryTextView: TextView = itemView.findViewById(R.id.countryId)
        private val image: ImageView = itemView.findViewById(R.id.imageId)

        fun setDataAndListener(
            data: SingleNews,
            clickListener: (SingleNews) -> Unit
        ) {
            titleTextView.text = data.title
            categoryTextView.text = data.category
            countryTextView.text = data.country
            if (data.image.equals(null)) {
                Glide.with(MyApplication.getContext()).load(DEFAULT_IMAGE).into(image)
            } else {
                Glide.with(MyApplication.getContext()).load(data.image)
                    .error(Glide.with(MyApplication.getContext()).load(DEFAULT_IMAGE)).into(image)
            }
            itemView.setOnClickListener {
                clickListener(data)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ElemViewHolder {
                val context = parent.context
                val layoutIdForListItem = R.layout.list_item
                val inflater = LayoutInflater.from(context)
                val shouldAttachToParentImmediately = false
                val view = inflater.inflate(
                    layoutIdForListItem,
                    parent,
                    shouldAttachToParentImmediately
                )
                return ElemViewHolder(view)
            }
        }
    }
}
