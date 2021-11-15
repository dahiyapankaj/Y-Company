package com.y.company.di
 
import com.google.firebase.firestore.FirebaseFirestore
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.utils.FirebaseUtil
import com.y.company.viewmodels.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { provideSharedPreferences() }
    single { provideFireStore() }
}

fun provideFireStore(): FirebaseFirestore? {
    return FirebaseUtil.firestore
}


fun provideSharedPreferences(): SharedPrefManager? {
    return SharedPrefManager.instance
}
