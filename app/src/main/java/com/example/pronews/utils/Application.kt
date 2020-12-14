package com.example.pronews.utils

import android.content.Context

abstract class MyApplication {

    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context=con
        }

        fun getContext(): Context {
            return context
        }
    }
}