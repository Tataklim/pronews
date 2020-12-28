package com.example.pronews.activities

import DataBaseHandler
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
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.R
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.db.NewsItem
import com.example.pronews.models.SingleNews
import com.example.pronews.network.ApiService
import com.example.pronews.utils.BaseActivity
import com.example.pronews.utils.MyApplication
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.properties.Delegates


class MainActivity : BaseActivity() {
    private lateinit var sharedPref: SharedPreferences

    private var category by Delegates.notNull<String>()
    private var country by Delegates.notNull<String>()

    private var language by Delegates.notNull<String>()
    private var theme by Delegates.notNull<Boolean>()
    private var checkNew by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setValuesFromSharedPref()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyApplication.setContext(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_liked
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        setLocale()
    }

    override fun onDestroy() {
        super.onDestroy()
//        PreferenceManager.getDefaultSharedPreferences(this)
//            .unregisterOnSharedPreferenceChangeListener(this)
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
//            getAndSetDataForRecyclerView();
            return true
        }

        if (itemId == R.id.delete_db_settings) {
            val db = DataBaseHandler(this)
            db.deleteDB()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLocale() {
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(language.toLowerCase()))
        resources.updateConfiguration(config, dm)
    }

    private fun setValuesFromSharedPref() {
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
//        sharedPref.registerOnSharedPreferenceChangeListener(this)

        val defaultTheme = resources.getBoolean(R.bool.preference_file_key_theme_default)
        Log.v("LOL size defaultTheme", defaultTheme.toString())
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

        theme = sharedPref.getBoolean(getString(R.string.preference_file_key_dark_theme), defaultTheme)
        Log.v("LOL size theme", theme.toString())
        category = ""
        country = ""

        if (theme) {
            setTheme(R.style.Theme_Pronews_Dark_Green)
        } else {
            setTheme(R.style.Theme_Pronews_Light_Green)
        }
    }

//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
//        when (key) {
//            resources.getString(R.string.preference_file_key_language) -> {
//                val defaultLanguage =
//                    resources.getString(R.string.preference_file_key_language_default)
//                language = sharedPref.getString(key, defaultLanguage).toString()
//                Log.v("KEK onShared", resources.getString(R.string.preference_file_key_language))
//            }
//            resources.getString(R.string.preference_file_key_theme) -> {
//                val defaultTheme =
//                    resources.getString(R.string.preference_file_key_theme_default)
//                theme = sharedPref.getString(key, defaultTheme).toString()
//                var newValueTranslated = theme
//                when(theme) {
//                    "темный" -> newValueTranslated = "dark"
//                    "светлый" -> newValueTranslated = "light"
//                }
//
//                when(newValueTranslated) {
//                    "dark" -> setTheme(R.style.Theme_Pronews_Dark_Green)
//                    "light" -> setTheme(R.style.Theme_Pronews_Light_Green)
//                }
//                Log.v("KEK onShared", resources.getString(R.string.preference_file_key_theme))
//            }
//            resources.getString(R.string.preference_file_key_check_new) -> {
//                val defaultCheckNew =
//                    resources.getBoolean(R.bool.preference_file_key_check_new_defaults)
//                checkNew = sharedPref.getBoolean(key, defaultCheckNew)
//                Log.v("KEK onShared", resources.getString(R.string.preference_file_key_check_new))
//            }
//
//            else -> {
//                Log.v("KEK onShared", "Error")
//            }
//        }
//    }
}
