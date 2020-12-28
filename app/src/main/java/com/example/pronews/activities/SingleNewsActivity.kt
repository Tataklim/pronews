package com.example.pronews.activities

import DataBaseHandler
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pronews.R
import com.example.pronews.db.NewsItem
import com.example.pronews.fragments.DEFAULT_IMAGE
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.SerializedSingleNews
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import java.util.*
import kotlin.properties.Delegates


class SingleNewsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private var language by Delegates.notNull<String>()
    private var theme by Delegates.notNull<String>()

    private lateinit var element: SingleNews
    private lateinit var likeButton: FloatingActionButton
    private var isLiked by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setValuesFromSharedPref()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_news)

        setSupportActionBar(findViewById(R.id.toolbar))
        likeButton = findViewById<FloatingActionButton>(R.id.like_button)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setItemData()

        setAddToLikedUrlEventListener()

        setIsLiked()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_news, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.share_settings) {
            val defaultSmsPackageName =
                Telephony.Sms.getDefaultSmsPackage(MyApplication.getContext()) //Need to change the build to API 19
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            val smsText = "Нашла классную новость, посмотри - " + element.url
            sendIntent.putExtra(Intent.EXTRA_TEXT, smsText)
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName)
            }
            startActivity(sendIntent)
            return true
        }

        if (itemId == R.id.open_settings) {
            val parsedUri: Uri = Uri.parse(element.url)
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(parsedUri.toString())
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setIsLiked() {
        val db = DataBaseHandler(this)
        val news = NewsItem(element)
        isLiked = db.checkIfNewsExists(news)
        if (isLiked) {
            likeButton.setImageResource(R.drawable.star_full)
            return
        }
        likeButton.setImageResource(R.drawable.star_empty)
    }

    private fun makeLikedToast() {
        if (isLiked) {
            Toast.makeText(MyApplication.getContext(), "Liked", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(MyApplication.getContext(), "Remove from liked", Toast.LENGTH_SHORT).show()
    }

    private fun setItemData() {
        val i = intent
        val item: SerializedSingleNews? = i.getSerializableExtra("item") as SerializedSingleNews?

        element = SingleNews(
            item?.author,
            item?.category,
            item?.country,
            item?.description,
            item?.image,
            item?.language,
            item?.published_at,
            item?.source,
            item?.title,
            item?.url
        )

        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = item?.title

        if (item?.image.equals(null)) {
            Glide.with(MyApplication.getContext()).load(DEFAULT_IMAGE)
                .into(findViewById<ShapeableImageView>(R.id.singleImageId))
        } else {
            Glide.with(MyApplication.getContext()).load(item?.image)
                .into(findViewById<ShapeableImageView>(R.id.singleImageId))
        }

        findViewById<TextView>(R.id.singleCategoryId).text = item?.category
        findViewById<TextView>(R.id.singleCountryId).text = item?.country
        findViewById<TextView>(R.id.singleAuthorId).text = item?.author
        findViewById<TextView>(R.id.singleDescriptionId).text = item?.description

        findViewById<TextView>(R.id.singleDate).text = item?.published_at
    }

    private fun setAddToLikedUrlEventListener() {
        findViewById<FloatingActionButton>(R.id.like_button).setOnClickListener {
            val db = DataBaseHandler(this)
            val news = NewsItem(element)
            val alreadyAdded = db.checkIfNewsExists(news)
            if (alreadyAdded) {
                db.deleteData(news)
                setIsLiked()
                makeLikedToast()
                return@setOnClickListener
            }
            db.insertData(news)
            setIsLiked()
            makeLikedToast()
        }
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
            "dark" -> setTheme(R.style.Theme_Pronews_Dark_Green_NoActionBar)
            "light" -> setTheme(R.style.Theme_Pronews_Light_Green_NoActionBar)
        }

    }
}