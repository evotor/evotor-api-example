package ru.fhs.evotor.remonline

import android.app.Application
import ru.fhs.evotor.remonline.network.RetrofitBuilder

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitBuilder.init(this)
    }
}