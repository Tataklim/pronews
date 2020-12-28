package com.example.pronews.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.example.pronews.R
import com.example.pronews.activities.SingleNewsActivity
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.NewsData
import com.example.pronews.utils.SerializedSingleNews
import com.example.pronews.workers.CATEGORY_ARG
import com.example.pronews.workers.KEY_RESULT
import com.example.pronews.workers.LANGUAGE_ARG
import com.example.pronews.workers.RefreshWorker
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

var DEFAULT_IMAGE =
    "https://w7.pngwing.com/pngs/503/7/png-transparent-camera-euclidean-green-computer-file-material-green-camera-purple-violet-rectangle.png"

var DEFAULT_IMAGE_LIGHT =
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTc5_p-BTfBWbpSrZ9UuJE0TbH_PF6w4YOWyQ&usqp=CAU"


var DEFAULT_IMAGE_DARK =
    "https://thumbs.dreamstime.com/b/%D0%B1%D0%B5%D0%BB%D1%8B%D0%B9-%D1%81%D0%B8%D0%BB%D1%83%D1%8D%D1%82-%D0%BA%D0%B0%D0%BC%D0%B5%D1%80%D1%8B-%D0%B2%D0%B8%D0%B4%D0%B5%D0%BE%D0%BD%D0%B0%D0%B1%D0%BB%D1%8E%D0%B4%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BD%D0%B0-%D1%87%D0%B5%D1%80%D0%BD%D0%BE%D0%BC-%D1%84%D0%BE%D0%BD%D0%B5-157275481.jpg"

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var buttonCategory: Button
    private lateinit var buttonNewsLanguage: Button
    private lateinit var emptyListTextView: TextView
    private lateinit var loadingProgressBar: RelativeLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var snackbarRefreshed: Snackbar

    private lateinit var sharedPref: SharedPreferences

    private var category by Delegates.notNull<String>()
    private var newsLanguage by Delegates.notNull<String>()
    private var language by Delegates.notNull<String>()
    private var theme by Delegates.notNull<String>()

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
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout_id)

        setSnackbar()

        swipeRefreshLayout.setOnRefreshListener {
            NewsData.update(category, newsLanguage, ::hideRefresh)
            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
            }, 10000)
        }

        sharedPref = MyApplication.getContext().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        category = ""
        newsLanguage = ""

        val defaultTheme = resources.getString(R.string.preference_file_key_theme_default)
        val defaultLanguage = resources.getString(R.string.preference_file_key_language_default);

        language =
            sharedPref.getString(getString(R.string.preference_file_key_language), defaultLanguage)
                .toString()

        theme = sharedPref.getString(
            getString(R.string.preference_file_key_theme),
            defaultTheme
        ).toString()

        if (theme == defaultTheme) {
            DEFAULT_IMAGE = DEFAULT_IMAGE_DARK
        } else {
            DEFAULT_IMAGE = DEFAULT_IMAGE_LIGHT
        }

        setCategoryButtonEventListener()
        setNewsLanguageButtonEventListener()

        Handler().postDelayed({
            setWorker()
        }, 1000)

        return root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//    }

    override fun onStart() {
        super.onStart()
        getAndSetDataForRecyclerView()
    }

    private fun setWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val myData: Data = workDataOf(
            CATEGORY_ARG to category,
            LANGUAGE_ARG to newsLanguage
        )

        val uploadWorkRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setInputData(myData)
            .build()

        WorkManager
            .getInstance(MyApplication.getContext())
            .enqueue(uploadWorkRequest)

        WorkManager.getInstance().getWorkInfoByIdLiveData(uploadWorkRequest.id)
            .observe(viewLifecycleOwner, Observer { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            Log.v("KEK WorkManager", "Download enqueued.")
                        }
                        WorkInfo.State.BLOCKED -> {
                            Log.v("KEK WorkManager", "Download blocked.")
                        }
                        WorkInfo.State.RUNNING -> {
                            Log.v("KEK WorkManager", "Download running.")
                        }
                    }
                }
                // По окончанию работы
                if (workInfo != null && workInfo.state.isFinished) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        Log.v("KEK WorkManager", "Download finished.")
                        val successOutputData = workInfo.outputData
                        val hasUpdates = successOutputData.getBoolean(KEY_RESULT, false)
                        Log.v("KEK WorkManager", hasUpdates.toString())
                        if (hasUpdates) {
                            snackbarRefreshed.show()
                        }
                    } else if (workInfo.state == WorkInfo.State.FAILED) {
                        Log.v("KEK WorkManager", "Failed")
                    } else if (workInfo.state == WorkInfo.State.CANCELLED) {
                        Log.v("KEK WorkManager", "cancelled")
                    }
                }
            })
    }

    private fun setSnackbar() {
        snackbarRefreshed = Snackbar.make(
            requireActivity().findViewById(R.id.navigation_home),
            getString(R.string.snackbar_title),
            Snackbar.LENGTH_LONG
        );
        snackbarRefreshed.setAction(getString(R.string.snackbar_button), View.OnClickListener {
            itemClicked(NewsData.newsSetWorker[0])
//            NewsData.changeData() // Убрать вместе с хардкодом в NewsData
//            setRecyclerViewData(NewsData.getData())
        })
    }

    private fun hideRefresh(dataSet: MutableList<SingleNews>) {
        setRecyclerViewData(dataSet)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun getAndSetDataForRecyclerView(prefChanges: Boolean = false) {
        setLoadingMode()
        if (prefChanges || NewsData.checkIfEmpty()) {
            NewsData.update(category, newsLanguage, ::setRecyclerViewData)
        } else {
            setRecyclerViewData(NewsData.getData())
        }
    }

    private fun setRecyclerViewData(dataSet: MutableList<SingleNews>) {
        if (dataSet.size == 0) {
            setNoDataMode()
            return
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = ListAdapter { item -> itemClicked(item) }
        viewAdapter.data = NewsData.getData()

        recyclerView = newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        setShowDataMode()
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
        getAndSetDataForRecyclerView(true)
    }

    private fun setCategoryButtonEventListener() {
        val popupMenu = PopupMenu(MyApplication.getContext(), buttonCategory)
        val wrapper: Context = ContextThemeWrapper(context, R.style.Theme_Pronews_Light_Green_Popup)
//        if (e)
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

    private fun setLoadingMode() {
        loadingProgressBar.visibility = View.VISIBLE;
        emptyListTextView.visibility = View.GONE;
    }

    private fun setNoDataMode() {
        loadingProgressBar.visibility = View.GONE;
        emptyListTextView.visibility = View.VISIBLE;
    }

    private fun setShowDataMode() {
        loadingProgressBar.visibility = View.GONE;
        emptyListTextView.visibility = View.GONE;
    }
}