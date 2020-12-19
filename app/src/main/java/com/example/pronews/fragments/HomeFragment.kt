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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.pronews.R
import com.example.pronews.activities.SingleNewsActivity
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.NewsData
import com.example.pronews.utils.SerializedSingleNews
import com.example.pronews.workers.RefreshWorker
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var buttonCategory: Button
    private lateinit var buttonNewsLanguage: Button
    private lateinit var emptyListTextView: TextView
    private lateinit var loadingProgressBar: RelativeLayout

    private lateinit var sharedPref: SharedPreferences

    private var category by Delegates.notNull<String>()
    private var newsLanguage by Delegates.notNull<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        newsRecyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewId)
        buttonCategory = root.findViewById<Button>(R.id.button_category_id)
        buttonNewsLanguage = root.findViewById<Button>(R.id.button_news_language_id)
        emptyListTextView = root.findViewById<TextView>(R.id.empty_list_id)
        loadingProgressBar = root.findViewById<RelativeLayout>(R.id.loading_id)

        sharedPref = MyApplication.getContext().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        category = ""
        newsLanguage = ""

        setCategoryButtonEventListener()
        setNewsLanguageButtonEventListener()

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<RefreshWorker>()
                .build()
        WorkManager
            .getInstance(MyApplication.getContext())
            .enqueue(uploadWorkRequest)

        return root
    }

    override fun onStart() {
        super.onStart()
        getAndSetDataForRecyclerView()
    }

    private fun getAndSetDataForRecyclerView(prefChanges: Boolean = false) {
        Log.v("KEK size lol", "getAndSetDataForRecyclerView")
        emptyListTextView.visibility = View.GONE;
        if (prefChanges || NewsData.checkIfEmpty()) {
            NewsData.update(category, newsLanguage, ::setRecyclerViewData)
        } else {
            setRecyclerViewData(NewsData.getData())
        }
    }

    private fun setRecyclerViewData(dataSet: MutableList<SingleNews>) {
        Log.v("KEK size lol", "setRecyclerViewData" + dataSet.size)
        loadingProgressBar.visibility = View.GONE;
        if (dataSet.size == 0) {
            emptyListTextView.visibility = View.VISIBLE
            return
        }
        emptyListTextView.visibility = View.GONE;

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

    private fun newsLanguageSelected(newsLanguageName: String) {
        buttonNewsLanguage.text = newsLanguageName
        newsLanguage = newsLanguageName
//        sharedPref.edit().putString(
//            getString(R.string.preference_file_key_country),
//            countryName
//        ).apply()
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
                    categorySelected(getString(R.string.business))
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
                    categorySelected(getString(R.string.sports))
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

    private fun setNewsLanguageButtonEventListener() {
        val popupMenu = PopupMenu(MyApplication.getContext(), buttonNewsLanguage)
        popupMenu.inflate(R.menu.popupmenu_news_language)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_news_language_de -> {
                    newsLanguageSelected(getString(R.string.de))
                    true
                }
                R.id.menu_news_language_en -> {
                    newsLanguageSelected(getString(R.string.en))
                    true
                }
                R.id.menu_news_language_fr -> {
                    newsLanguageSelected(getString(R.string.fr))
                    true
                }
                R.id.menu_news_language_ru -> {
                    newsLanguageSelected(getString(R.string.ru))
                    true
                }

                else -> false
            }
        }
        buttonNewsLanguage.setOnClickListener {
            popupMenu.show()
        }
    }
}