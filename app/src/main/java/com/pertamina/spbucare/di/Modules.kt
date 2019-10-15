package com.pertamina.spbucare.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.pertamina.spbucare.network.SpbuCareApiService
import com.pertamina.spbucare.repository.OrderRepository
import com.pertamina.spbucare.viewmodel.*
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { OkHttpClient.Builder().build() }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://spbu-care.firebaseapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    factory { get<Retrofit>().create(SpbuCareApiService::class.java) }
}

val repositoryModule = module {
    factory { OrderRepository(get()) }
}

val viewModelModule = module {
    viewModel { (userType : String) -> OrderViewModel(get(), get(), userType) }
    viewModel { (type : String) -> UserViewModel(get(), get(), type) }
    viewModel { (quizId : String) -> QuizViewModel(get(), get(), quizId) }
    viewModel { NotificationViewModel(get(), get()) }
    viewModel { EducationViewModel(get(), get()) }
    viewModel { InformationViewModel(get(), get()) }
    viewModel { (category : String) -> HistoryViewModel(get(), get(), category) }
}

val firebaseModule = module {
    factory { FirebaseAuth.getInstance() }
    factory { FirebaseStorage.getInstance() }
}


val appComponent = listOf(networkModule, viewModelModule, repositoryModule, firebaseModule)