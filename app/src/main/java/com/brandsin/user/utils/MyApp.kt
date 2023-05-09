package com.brandsin.user.utils

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.myfatoorah.sdk.utils.MFCountry
import com.myfatoorah.sdk.utils.MFEnvironment
import com.myfatoorah.sdk.views.MFSDK
import timber.log.Timber


// hilt
class MyApp : Application()
{
    var proxy: HttpProxyCacheServer? = null
    companion object{
        var simpleCache: SimpleCache? = null
        private lateinit var mInstance: MyApp
        lateinit var context : Context
        fun getInstance(): MyApp {
            return mInstance
        }
    }

    private fun initTimber() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return super.createStackElementTag(element) + " line: " + element.lineNumber
            }
        })
    }

    fun getAppContext(): Context? {
        return context
    }


    fun getProxy(context: Context): HttpProxyCacheServer? {
        val app: MyApp = context.applicationContext as MyApp
        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
    }

    private fun newProxy(): HttpProxyCacheServer? {
        return HttpProxyCacheServer.Builder(this)
            .maxCacheSize((1024 * 1024 * 1024).toLong())
            .build()
        //return new HttpProxyCacheServer(this);
    }
    override fun onCreate() {
        super.onCreate()
        mInstance = this

        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(100)
        if (simpleCache == null) {
            simpleCache =
                SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, ExoDatabaseProvider(this))
        }
//        DataBindingUtil.setDefaultComponent(AppDataBindingComponent())

        context = applicationContext

       /* startKoin {
            androidContext(this@MyApp)
            modules(
                listOf(
                    setAppointmentModule,
                    salonDetailsModule
                )
            )
        }*/


        MFSDK.init(Config.API_KEY, MFCountry.SAUDI_ARABIA, MFEnvironment.LIVE)





        initTimber()

    }

    protected override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}