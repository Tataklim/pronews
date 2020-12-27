package com.example.pronews.fragments

import DataBaseHandler
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.activities.SingleNewsActivity
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.db.NewsItem
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.NewsDataDB
import com.example.pronews.utils.SerializedSingleNews

class LikedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var emptyListTextView: TextView
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_liked, container, false)

        emptyListTextView = root.findViewById<TextView>(R.id.empty_list_id)
        newsRecyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewId)

        getAndSetDataForRecyclerView()
        return root
    }

    private fun getAndSetDataForRecyclerView() {
        val db = DataBaseHandler(MyApplication.getContext())
        val news = db.readData()
        NewsDataDB.setData(news)
        setRecyclerViewData()
    }

    private fun setRecyclerViewData() {
        if (NewsDataDB.getData().size == 0) {
            setNoLikedMode()
            return
        }
        setShowLikedMode()
        viewManager = LinearLayoutManager(activity)
        viewAdapter = ListAdapter { item -> itemClicked(item) }
        viewAdapter.data = NewsDataDB.getData()

        recyclerView = newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        setShowLikedMode()
    }

    private fun itemClicked(item: SingleNews) {
        val intent = Intent(activity, SingleNewsActivity::class.java)
        val itemSerialized = SerializedSingleNews(item)
        intent.putExtra("item", itemSerialized)
        startActivity(intent)
    }

    private fun setNoLikedMode() {
        emptyListTextView.visibility = View.VISIBLE
    }

    private fun setShowLikedMode() {
        emptyListTextView.visibility = View.GONE
    }
}