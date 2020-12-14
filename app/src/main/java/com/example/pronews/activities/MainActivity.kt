package com.example.pronews.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.databinding.ActivityMainBinding
import com.example.pronews.models.SingleNews
import com.example.pronews.network.ApiService
import com.example.pronews.utils.MyApplication
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var language by Delegates.notNull<String>()
    private var theme by Delegates.notNull<String>()
    private var checkNew by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValuesFromSharedPref()

        MyApplication.Companion.setContext(this);

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_liked, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val locale = Locale("ru")
        Locale.setDefault(locale)
        val config: Configuration = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )

    }

    override fun onResume() {
        super.onResume()
        setLocale()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }

        if (itemId == R.id.update_settings) {
            getAndSetDataForRecyclerView();
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setLocale() {
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(language.toLowerCase()))
        resources.updateConfiguration(config, dm)

//        val locale = Locale(language)
//        resources.configuration.setLocale(locale)
//        resources.updateConfiguration(resources.configuration, null)
//        recreate()
    }

    private fun setValuesFromSharedPref() {
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        val defaultTheme = resources.getString(R.string.preference_file_key_theme_default)
        val defaultCheckNew =
            resources.getBoolean(R.bool.preference_file_key_check_new_defaults)
        val defaultLanguage = resources.getString(R.string.preference_file_key_language_default);

        language =
            sharedPref.getString(getString(R.string.preference_file_key_language), defaultLanguage)
                .toString()

        checkNew = sharedPref.getBoolean(
            getString(R.string.preference_file_key_check_new),
            defaultCheckNew
        );

        theme = sharedPref.getString(
            getString(R.string.preference_file_key_theme),
            defaultTheme
        ).toString()

        when(theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun getAndSetDataForRecyclerView() {
        val newsSet: MutableList<SingleNews> = mutableListOf()

        val temp = ApiService.create()
        temp.news()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                result.List.map { elem -> newsSet.add(elem) }
                setRecyclerViewData(newsSet);
            }, { error ->
                error.message?.let { Log.v("Error", it) }
                error.printStackTrace()
            })
    }

    private fun setRecyclerViewData(dataSet: MutableList<SingleNews>) {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ListAdapter { item -> itemClicked(item) }
        viewAdapter.data = dataSet

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewId).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun itemClicked(item: SingleNews) {
        val intent = Intent(this, SingleNewsActivity::class.java)
        startActivity(intent)
    }

//    private fun setCryptoButtonEventListener() {
//        val popupMenu = PopupMenu(this, binding.buttonId)
//        popupMenu.inflate(R.menu.popupmenu)
//        popupMenu.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.menu1 -> {
//                    binding.buttonId.text = "Bitcoin"
//                    sharedPref.edit().putString(
//                        getString(R.string.preference_file_key_crypto),
//                        getString(R.string.btc)
//                    ).apply()
//                    true
//                }
//                R.id.menu2 -> {
//                    binding.buttonId.text = "Ethereum"
//                    sharedPref.edit().putString(
//                        getString(R.string.preference_file_key_crypto),
//                        getString(R.string.eth)
//                    ).apply()
//                    true
//                }
//                R.id.menu3 -> {
//                    binding.buttonId.text = "Litecoin"
//                    sharedPref.edit().putString(
//                        getString(R.string.preference_file_key_crypto),
//                        getString(R.string.ltc)
//                    ).apply()
//                    true
//                }
//                R.id.menu4 -> {
//                    binding.buttonId.text = "Chainlink"
//                    sharedPref.edit().putString(
//                        getString(R.string.preference_file_key_crypto),
//                        getString(R.string.link)
//                    ).apply()
//                    true
//                }
//                R.id.menu5 -> {
//                    binding.buttonId.text = "Bitcoin Cash"
//                    sharedPref.edit().putString(
//                        getString(R.string.preference_file_key_crypto),
//                        getString(R.string.bch)
//                    ).apply()
//                    true
//                }
//
//                else -> false
//            }
//        }
//        binding.buttonId.setOnClickListener {
//            popupMenu.show()
//        }
//    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            resources.getString(R.string.preference_file_key_language) -> {
                val defaultLanguage =
                    resources.getString(R.string.preference_file_key_language_default)
                language = sharedPref.getString(key, defaultLanguage).toString()
                getAndSetDataForRecyclerView()
                setLocale()
            }
            resources.getString(R.string.preference_file_key_theme) -> {
                val defaultTheme =
                    resources.getString(R.string.preference_file_key_theme_default)
                theme = sharedPref.getString(key, defaultTheme).toString()
                getAndSetDataForRecyclerView()
            }
            resources.getString(R.string.preference_file_key_check_new) -> {
                val defaultCheckNew =
                    resources.getBoolean(R.bool.preference_file_key_check_new_defaults)
                checkNew = sharedPref.getBoolean(key, defaultCheckNew)
                getAndSetDataForRecyclerView()
            }
            else -> {
                Log.v("KEK WHAT", "Error")
            }
        }
    }
}
