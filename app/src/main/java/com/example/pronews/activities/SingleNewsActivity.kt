package com.example.pronews.activities

import DataBaseHandler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pronews.R
import com.example.pronews.adapters.DEFAULT_IMAGE
import com.example.pronews.db.NewsItem
import com.example.pronews.models.SingleNews
import com.example.pronews.utils.MyApplication
import com.example.pronews.utils.SerializedSingleNews
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class SingleNewsActivity : AppCompatActivity() {

    private lateinit var element: SingleNews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_news)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setItemData()

        setAddToLikedUrlEventListener()

        setUrlEventListener()
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
                .into(findViewById<ImageView>(R.id.singleImageId))
        } else {
            Glide.with(MyApplication.getContext()).load(item?.image)
                .into(findViewById<ImageView>(R.id.singleImageId))
        }

        findViewById<TextView>(R.id.singleCategoryId).text = item?.category
        findViewById<TextView>(R.id.singleCountryId).text = item?.country
        findViewById<TextView>(R.id.singleAuthorId).text = item?.author
        findViewById<TextView>(R.id.singleDescriptionId).text = item?.description
        findViewById<TextView>(R.id.singleDate).text = item?.published_at
        findViewById<TextView>(R.id.singleUrl).text = item?.url
    }

    private fun setUrlEventListener() {
        val urlTextView = findViewById<TextView>(R.id.singleUrl)
        urlTextView.setOnClickListener {
            val parsedUri: Uri = Uri.parse(urlTextView.text.toString())
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(parsedUri.toString())
            startActivity(intent)
        }
    }

    private fun setAddToLikedUrlEventListener() {
        findViewById<FloatingActionButton>(R.id.like_button).setOnClickListener {
            val db = DataBaseHandler(this)
            val news = NewsItem(element)
            val alreadyAdded = db.checkIfNewsExists(news)
            if (alreadyAdded) {
                return@setOnClickListener
            }
            db.insertData(news)
        }
    }
}