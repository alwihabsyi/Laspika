package com.laspika.laspika.core.di

import com.laspika.laspika.presentation.auth.AuthViewModel
import com.laspika.laspika.presentation.detail.ReportDetailViewModel
import com.laspika.laspika.presentation.history.HistoryViewModel
import com.laspika.laspika.presentation.home.HomeViewModel
import com.laspika.laspika.presentation.notification.NotificationViewModel
import com.laspika.laspika.presentation.profile.ProfileViewModel
import com.laspika.laspika.presentation.tindakan.TindakanViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { TindakanViewModel(get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ReportDetailViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { NotificationViewModel(get()) }
}