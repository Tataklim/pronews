package com.example.pronews.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.activities.SingleNewsActivity
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.NewsData
import com.example.pronews.utils.SerializedSingleNews

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var newsRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewId)
        return root
    }

    override fun onStart() {
        super.onStart()
        getAndSetDataForRecyclerView()
    }

    private fun getAndSetDataForRecyclerView() {
        if (NewsData.checkIfEmpty()) {
            NewsData.update(::setRecyclerViewData)
        } else {
            setRecyclerViewData(NewsData.getData())
        }
    }

    private fun setRecyclerViewData(dataSet: MutableList<SingleNews>) {
        viewManager = LinearLayoutManager(activity)
        viewAdapter = ListAdapter { item -> itemClicked(item) }
        viewAdapter.data = dataSet

        recyclerView = newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun itemClicked(item: SingleNews) {
        val intent = Intent(activity, SingleNewsActivity::class.java)
        val itemSerialized = SerializedSingleNews(item)
        intent.putExtra("item", itemSerialized)
        startActivity(intent)
    }
}