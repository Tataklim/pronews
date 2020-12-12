package com.example.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ListAdapter(private val clickListener: (DayInfo) -> Unit) : RecyclerView.Adapter<ListAdapter.ElemViewHolder> () {
    var data = mutableListOf<DayInfo> ()
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
        private val dateViewRow: TextView = itemView.findViewById(R.id.dateId)
        private val textViewRow: TextView = itemView.findViewById(R.id.textId)
        private val currencyViewRow : TextView = itemView.findViewById(R.id.currencyId)

        fun setDataAndListener(
            data: DayInfo,
            currency: String,
            clickListener: (DayInfo) -> Unit
        ) {
            dateViewRow.text = getDateTime(data.time)
            textViewRow.text = data.high
            currencyViewRow.text= currency
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