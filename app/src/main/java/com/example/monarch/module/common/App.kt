package com.example.monarch.module.common

import android.app.Application
import android.content.Context


class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun getContextInstanse() : Context {
            return instance!!.applicationContext
        }
    }
}