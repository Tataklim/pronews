package com.example.pronews.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.pronews.R
import com.example.pronews.utils.MyApplication
import java.util.*
import kotlin.properties.Delegates


class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private var language by Delegates.notNull<String>()
    private var theme by Delegates.notNull<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setValuesFromSharedPref()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)


        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setValuesFromSharedPref() {
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        val defaultTheme = resources.getString(R.string.preference_file_key_theme_default)
        val defaultLanguage = resources.getString(R.string.preference_file_key_language_default);

        language =
            sharedPref.getString(getString(R.string.preference_file_key_language), defaultLanguage)
                .toString()

        theme = sharedPref.getString(
            getString(R.string.preference_file_key_theme),
            defaultTheme
        ).toString()



        when (theme) {
            "dark" -> setTheme(R.style.Theme_Pronews_Dark_Green)
            "light" -> setTheme(R.style.Theme_Pronews_Light_Green)
        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private fun setLocale(language: String) {
            val dm: DisplayMetrics = resources.displayMetrics
            val config: Configuration = resources.configuration
            config.setLocale(Locale(language.toLowerCase()))
            resources.updateConfiguration(config, dm)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val sharedPref = this.requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )

            val prevLanguage: ListPreference = preferenceManager.findPreference(
                resources.getString(
                    R.string.preference_file_key_language
                )
            )!!
            val prevTheme: ListPreference =
                preferenceManager.findPreference(resources.getString(R.string.preference_file_key_theme))!!
            val prevCheckNew: CheckBoxPreference = preferenceManager.findPreference(
                resources.getString(
                    R.string.preference_file_key_check_new
                )
            )!!

            prevLanguage.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    var newValueTranslated = newValue
                    when (newValue) {
                        "русский" -> newValueTranslated = "ru"
                        "английский" -> newValueTranslated = "en"
                    }
                    setLocale(newValueTranslated.toString())
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_language),
                        newValueTranslated.toString()
                    ).apply()
                    true
                }
            prevTheme.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    var newValueTranslated = newValue
                    when (newValue) {
                        "темный" -> newValueTranslated = "dark"
                        "светлый" -> newValueTranslated = "light"
                    }
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_theme),
                        newValueTranslated.toString()
                    ).apply()
//                    when(newValueTranslated) {
//                        "dark" -> setTheme(R.style.Theme_Pronews_Dark_Green)
//                        "light" -> setTheme(R.style.Theme_Pronews_Light_Green)
//                    }
                    true
                }
            prevCheckNew.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    sharedPref.edit().putBoolean(
                        getString(R.string.preference_file_key_check_new),
                        newValue as Boolean
                    ).apply()
                    true
                }
        }
    }
}