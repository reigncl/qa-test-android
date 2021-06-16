package com.uharris.wedding.presentation.injection.modules

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ApplicationModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideId(context: Context): String {
            return PreferenceManager.getDefaultSharedPreferences(context).getString("id", "") ?: ""
        }
    }

    @Binds
    abstract fun bindContext(application: Application): Context
}