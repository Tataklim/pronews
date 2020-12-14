package com.example.pronews.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.*
import com.example.pronews.R


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
        private fun validateInputDays(newValue: Any): Boolean {
            return newValue.toString().toIntOrNull() != null && newValue.toString()
                .toInt() > 0 && newValue.toString().toInt() < 2000
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val sharedPref = this.requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )

            val prevLanguage: ListPreference = preferenceManager.findPreference(resources.getString(R.string.preference_file_key_language))!!
            val prevTheme: ListPreference = preferenceManager.findPreference(resources.getString(R.string.preference_file_key_theme))!!
            val prevCheckNew: CheckBoxPreference = preferenceManager.findPreference(resources.getString(R.string.preference_file_key_check_new))!!

//            prefPeriod.onPreferenceChangeListener =
//                Preference.OnPreferenceChangeListener { _, newValue ->
//                    if (validateInputDays(newValue)) {
//                        sharedPref.edit().putInt(
//                            getString(R.string.preference_file_key_period),
//                            newValue.toString().toInt()
//                        ).apply()
//                    } else {
//                        Toast.makeText(
//                            context,
//                            "Количество дней должно быть положительным число до 2000",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                    validateInputDays(newValue)
//                }

            prevLanguage.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
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
                        "темный" ->  newValueTranslated = "dark"
                        "светлый" ->  newValueTranslated = "light"
                    }
                    Log.v("KEK WHAT", newValueTranslated.toString())
                    when(newValueTranslated) {
                        "light" ->  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "dark" ->  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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