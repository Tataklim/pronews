package com.example.pronews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.models.SingleNews
import java.text.SimpleDateFormat
import java.util.*

class ListAdapter(private val clickListener: (SingleNews) -> Unit) : RecyclerView.Adapter<ListAdapter.ElemViewHolder> () {
    var data = mutableListOf<SingleNews> ()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var currency = String ()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElemViewHolder {
        return ElemViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ElemViewHolder, position: Int) {
        val item = data[position]
        holder.setDataAndListener(item, currency, clickListener)
    }

    class ElemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val authorTextView: TextView = itemView.findViewById(R.id.authorId)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryId)
        private val countryTextView : TextView = itemView.findViewById(R.id.countryId)

        fun setDataAndListener(
            data: SingleNews,
            currency: String,
            clickListener: (SingleNews) -> Unit
        ) {
//            dateViewRow.text = getDateTime(data.time)
            authorTextView.text = data.author
            categoryTextView.text = data.category
            countryTextView.text= data.country
            itemView.setOnClickListener{
                clickListener(data)
            }
        }

        private fun getDateTime(s: String): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        }

        companion object {
            fun from(parent: ViewGroup) : ElemViewHolder {
                val context = parent.context
                val layoutIdForListItem = R.layout.list_item_1
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