package com.y.company.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.di.mainModule
import com.y.company.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.logger.Level

open class YCompanyApp : Application(), LifecycleObserver {

    companion object {
        fun getContext(): Context? {
            return myContext
        }

        var myContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        myContext = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        SharedPrefManager.instance!!.init(this)

        startKoinInstance()
    }

    private fun startKoinInstance(){
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@YCompanyApp)
            modules(
                listOf(
                    mainModule,
                    viewModelModule
                   )
            )
        }
    }

    fun stopKoinInstance(){
        stopKoin()
    }
}