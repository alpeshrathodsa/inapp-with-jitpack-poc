package com.sa.demo.inappwithjitpack

import android.app.Application
import com.sa.demo.inappwithjitpack.di.basicModule
import com.sa.demo.inappwithjitpack.di.viewmodelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Purpose -
 *
 * @author Alpesh Rathod
 *
 * Created on 11/2/21
 */
class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {
            androidContext(this@DemoApp)
            modules(listOf(basicModule, viewmodelModule))
        }
    }
}