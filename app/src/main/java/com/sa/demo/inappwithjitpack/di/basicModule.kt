package com.sa.demo.inappwithjitpack.di

import android.content.Context
import android.content.res.Resources
import com.sa.demo.baselibrary.util.NetworkUtils
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Purpose -
 *
 * @author Alpesh Rathod
 *
 * Created on 2/1/21
 */
val basicModule = module {
    /**
     * Provides basic resources
     **/
    single<Resources> { provideResources(androidContext()) }

    /**
     * Provides network utilities
     **/
    single { provideNetworkHelper(androidContext()) }
}

//basic dependencies
private fun provideResources(context: Context) = context.resources

private fun provideNetworkHelper(context: Context) = NetworkUtils(context)

