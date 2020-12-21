package com.example.pronews.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.pronews.R
import com.example.pronews.utils.MyApplication
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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

    class SettingsFragment : PreferenceFragmentCompat() {

        private fun setLocale(language: String) {
            val dm: DisplayMetrics = resources.displayMetrics
            val config: Configuration = resources.configuration
            config.setLocale(Locale(language.toLowerCase()))
            resources.updateConfiguration(config, dm)
            activity?.recreate();
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
            val prevTheme: ListPreference = preferenceManager.findPreference(resources.getString(R.string.preference_file_key_theme))!!
            val prevCheckNew: CheckBoxPreference = preferenceManager.findPreference(
                resources.getString(
                    R.string.preference_file_key_check_new
                )
            )!!

            prevLanguage.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    setLocale(newValue.toString())
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_language),
                        newValue.toString()
                    ).apply()
                    true
                }
            prevTheme.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    var newValueTranslated = newValue
                    when(newValue) {
                        "темный" -> newValueTranslated = "dark"
                        "светлый" -> newValueTranslated = "light"
                    }
                    Log.v("KEK WHAT", newValueTranslated.toString())
                    when(newValueTranslated) {
                        "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_theme),
                        newValueTranslated.toString()
                    ).apply()
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