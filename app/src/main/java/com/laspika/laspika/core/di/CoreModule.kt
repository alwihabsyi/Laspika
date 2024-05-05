package com.laspika.laspika.core.di

import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.laspika.laspika.core.notification.database.NotificationDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val firebaseModule = module {
    single { Firebase.firestore }
    single { FirebaseAuth.getInstance() }
    single { Firebase.storage }
}

val databaseModule = module {
    factory { get<NotificationDatabase>().notificationDao }
    single {
        Room.databaseBuilder(
            androidContext(), NotificationDatabase::class.java, "notification_db"
        ).fallbackToDestructiveMigration().build()
    }
}