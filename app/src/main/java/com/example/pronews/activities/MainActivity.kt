package com.example.pronews.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pronews.network.ApiService
import com.example.pronews.R
import com.example.pronews.adapters.ListAdapter
import com.example.pronews.databinding.ActivityMainBinding
import com.example.pronews.models.SingleNews
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var period by Delegates.notNull<Int>()
    private var currency by Delegates.notNull<String>()
    private var crypt by Delegates.notNull<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setValuesFromSharedPref()

        setUrlEventListener()

        setCryptoButtonEventListener()

        getAndSetDataForRecyclerView();
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
            recreate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setValuesFromSharedPref() {
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        val defaultPeriodValue = resources.getInteger(R.integer.preference_file_key_period_default)
        val defaultCurrencyValue =
            resources.getString(R.string.preference_file_key_currency_default)
        val defaultCryptoValue =
            resources.getString(R.string.preference_file_key_crypto_default)

        period =
            sharedPref.getInt(getString(R.string.preference_file_key_period), defaultPeriodValue)

        currency = sharedPref.getString(
            getString(R.string.preference_file_key_currency),
            defaultCurrencyValue
        ).toString()

        crypt = sharedPref.getString(
            getString(R.string.preference_file_key_crypto),
            defaultCryptoValue
        ).toString()
    }

    private fun setUrlEventListener() {
        val urlTextView = findViewById<TextView>(R.id.url)
        urlTextView.setOnClickListener {
            val parsedUri: Uri = Uri.parse(urlTextView.text.toString())
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(parsedUri.toString())
            startActivity(intent)
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
                result.List.map { elem -> elem.author?.let { Log.v("KEK WHAT", it) } }
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
        viewAdapter.currency = currency

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewId).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun itemClicked(item: SingleNews) {
        Toast.makeText(
            this,
            "author: " + item.author + "\n" +
                    "category: " + item.category + "\n" +
                    "country: " + item.country + "\n" +
                    "description: " + item.description + "\n",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setCryptoButtonEventListener() {
        val popupMenu = PopupMenu(this, binding.buttonId)
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu1 -> {
                    binding.buttonId.text = "Bitcoin"
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_crypto),
                        getString(R.string.btc)
                    ).apply()
                    true
                }
                R.id.menu2 -> {
                    binding.buttonId.text = "Ethereum"
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_crypto),
                        getString(R.string.eth)
                    ).apply()
                    true
                }
                R.id.menu3 -> {
                    binding.buttonId.text = "Litecoin"
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_crypto),
                        getString(R.string.ltc)
                    ).apply()
                    true
                }
                R.id.menu4 -> {
                    binding.buttonId.text = "Chainlink"
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_crypto),
                        getString(R.string.link)
                    ).apply()
                    true
                }
                R.id.menu5 -> {
                    binding.buttonId.text = "Bitcoin Cash"
                    sharedPref.edit().putString(
                        getString(R.string.preference_file_key_crypto),
                        getString(R.string.bch)
                    ).apply()
                    true
                }

                else -> false
            }
        }
        binding.buttonId.setOnClickListener {
            popupMenu.show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            resources.getString(R.string.preference_file_key_period) -> {
                val defaultPeriodValue =
                    resources.getInteger(R.integer.preference_file_key_period_default)
                period = sharedPref.getInt(key, defaultPeriodValue)
                getAndSetDataForRecyclerView()
            }
            resources.getString(R.string.preference_file_key_currency) -> {
                val defaultCurrencyValue =
                    resources.getString(R.string.preference_file_key_currency_default)
                currency = sharedPref.getString(key, defaultCurrencyValue).toString()
                getAndSetDataForRecyclerView()
            }
            resources.getString(R.string.preference_file_key_crypto) -> {
                val defaultCryptoValue =
                    resources.getString(R.string.preference_file_key_crypto_default)
                crypt = sharedPref.getString(key, defaultCryptoValue).toString()
                getAndSetDataForRecyclerView()
            }
            else -> {
                Log.v("KEK WHAT", "Error")
            }
        }
    }
}
