package com.example.exchange

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


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

            val prefPeriod: EditTextPreference = preferenceManager.findPreference("period")!!
            val prefCurrency: ListPreference = preferenceManager.findPreference("currency")!!

            prefPeriod.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    if (validateInputDays(newValue)) {
                        sharedPref.edit().putInt(
                            getString(R.string.preference_file_key_period),
                            newValue.toString().toInt()
                        ).apply()
                    } else {
                        Toast.makeText(
                            context,
                            "Количество дней должно быть положительным число до 2000",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    validateInputDays(newValue)
                }

            prefCurrency.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_currency),
                        newValue.toString()
                    ).apply()
                    true
                }
        }
    }
}