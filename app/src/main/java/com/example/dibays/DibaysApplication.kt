package com.example.dibays

import android.app.Application
import com.example.dibays.di.AppContainer

class DibaysApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(this)
    }
}
