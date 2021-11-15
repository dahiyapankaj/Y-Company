package com.y.company.di

import com.y.company.viewmodels.CartViewModel
import com.y.company.viewmodels.MainActivityViewModel
import com.y.company.viewmodels.OrdersViewModel
import com.y.company.viewmodels.ProductDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainActivityViewModel(get(),get()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get()) }
    viewModel { OrdersViewModel(get(), get()) }
}