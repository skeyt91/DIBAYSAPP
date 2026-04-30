package com.example.dibays.di

import android.content.Context
import com.example.dibays.BuildConfig
import com.example.dibays.data.auth.AuthRepository
import com.example.dibays.data.dashboard.DashboardRepository
import com.example.dibays.data.session.SessionStore

class AppContainer(context: Context) {
    val sessionStore = SessionStore(context.applicationContext)
    val authRepository = AuthRepository(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        anonKey = BuildConfig.SUPABASE_ANON_KEY,
    )
    val dashboardRepository = DashboardRepository(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        anonKey = BuildConfig.SUPABASE_ANON_KEY,
    )
}
