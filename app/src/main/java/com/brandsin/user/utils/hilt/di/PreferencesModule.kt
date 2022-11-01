//package com.hajaty.user.utils.hilt.di
//
//import android.content.Context
//import android.content.SharedPreferences
//import com.hajaty.user.model.constants.Const
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
////import dagger.hilt.android.components.ApplicationComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Singleton
//
////@Module
////@InstallIn(ApplicationComponent::class)
//object PreferencesModule {
//    @Singleton
//    @Provides
//    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
//        return context.getSharedPreferences(Const.APP_PREF_NAME, Context.MODE_PRIVATE)
//    }
//
//
//  /*  @Singleton
//    @Provides
//    fun provideSharedPreferenceDataSource(sharedPreferences: SharedPreferences): SharedPreferenceDataSource {
//        return SharedPreferenceDataSourceImpl(sharedPreferences)
//    }*/
//}