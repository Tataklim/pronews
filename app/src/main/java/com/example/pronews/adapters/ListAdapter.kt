package com.example.pronews.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.models.SingleNews
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ListAdapter(private val clickListener: (SingleNews) -> Unit) : RecyclerView.Adapter<ListAdapter.ElemViewHolder> () {
    var data = mutableListOf<SingleNews> ()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//    var currency = String ()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElemViewHolder {
        return ElemViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ElemViewHolder, position: Int) {
        val item = data[position]
        holder.setDataAndListener(item, clickListener)
    }

    class ElemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleId)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryId)
        private val countryTextView : TextView = itemView.findViewById(R.id.countryId)
        private val image : ImageView = itemView.findViewById(R.id.imageId)

        fun setDataAndListener(
            data: SingleNews,
            clickListener: (SingleNews) -> Unit
        ) {
//            dateViewRow.text = getDateTime(data.time)
            titleTextView.text = data.title
            categoryTextView.text = data.category
            countryTextView.text= data.country
//            if (data.image.equals(null)) {
                image.setImageResource(R.drawable.ic_launcher_background)
//            } else {
//                val newurl = URL(data.image)
//                val pic = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
//                image.setImageBitmap(pic);
//            }
//            image.setImageURI(data.image)
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
