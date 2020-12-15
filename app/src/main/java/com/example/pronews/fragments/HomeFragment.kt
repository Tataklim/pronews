package com.example.pronews.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.activities.SingleNewsActivity
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.NewsData
import com.example.pronews.utils.SerializedSingleNews
import kotlin.properties.Delegates

const val NO_DATA_FOUND = "There isnt any news";

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var buttonCategory: Button
    private lateinit var buttonCountry: Button
    private lateinit var emptyListTextView: TextView

    private lateinit var sharedPref: SharedPreferences

    private var category by Delegates.notNull<String>()
    private var country by Delegates.notNull<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("KEK size lol", "onCreateView")
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewId)
        buttonCategory = root.findViewById<Button>(R.id.button_category_id)
        buttonCountry = root.findViewById<Button>(R.id.button_country_id)
        emptyListTextView = root.findViewById<TextView>(R.id.empty_list_id)
        sharedPref = MyApplication.getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        category = ""
        country = ""
        setCategoryButtonEventListener()
        setCountryButtonEventListener()
        return root
    }

    override fun onStart() {
        super.onStart()
        getAndSetDataForRecyclerView()
    }

    private fun getAndSetDataForRecyclerView(prefChanges: Boolean = false) {
        Log.v("KEK size lol", "getAndSetDataForRecyclerView")
        if (prefChanges || NewsData.checkIfEmpty()) {
            NewsData.update(category, country, ::setRecyclerViewData)
        } else {
            setRecyclerViewData(NewsData.getData())
        }
    }

    private fun setRecyclerViewData(dataSet: MutableList<SingleNews>) {
        Log.v("KEK size lol", "setRecyclerViewData" + dataSet.size)
        if (dataSet.size == 0) {
            emptyListTextView.text = NO_DATA_FOUND
            recyclerView.visibility = View.GONE;
            return
        }
        emptyListTextView.text = ""
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

    private fun categorySelected(categoryName: String) {
        buttonCategory.text = categoryName
        category = categoryName
        sharedPref.edit().putString(
            getString(R.string.preference_file_key_category),
            categoryName
        ).apply()
        getAndSetDataForRecyclerView(true)
    }

    private fun countrySelected(countryName: String) {
        buttonCountry.text = countryName
        country = countryName
        sharedPref.edit().putString(
            getString(R.string.preference_file_key_country),
            countryName
        ).apply()
        getAndSetDataForRecyclerView(true)
    }

    private fun setCategoryButtonEventListener() {
        val popupMenu = PopupMenu(MyApplication.getContext(), buttonCategory)
        popupMenu.inflate(R.menu.popupmenu_category)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_category_general -> {
                    categorySelected(getString(R.string.general))
                    true
                }
                R.id.menu_category_business -> {
                    categorySelected( getString(R.string.business))
                    true
                }
                R.id.menu_category_health -> {
                    categorySelected(getString(R.string.health))
                    true
                }
                R.id.menu_category_science -> {
                    categorySelected(getString(R.string.science))
                    true
                }

                R.id.menu_category_sports -> {
                    categorySelected( getString(R.string.sports))
                    true
                }

                R.id.menu_category_technology -> {
                    categorySelected(getString(R.string.technology))
                    true
                }

                else -> false
            }

        }
        buttonCategory.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun setCountryButtonEventListener() {
        val popupMenu = PopupMenu(MyApplication.getContext(), buttonCountry)
        popupMenu.inflate(R.menu.popupmenu_country)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_country_de -> {
                    countrySelected(getString(R.string.de))
                    true
                }
                R.id.menu_country_en -> {
                    countrySelected( getString(R.string.en))
                    true
                }
                R.id.menu_country_fr -> {
                    countrySelected(getString(R.string.fr))
                    true
                }
                R.id.menu_country_ru -> {
                    countrySelected(getString(R.string.ru))
                    true
                }

                else -> false
            }
        }
        buttonCountry.setOnClickListener {
            popupMenu.show()
        }
    }
}