package com.teamttdvlp.memolang.view.Activity.di.module

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun providesFirebaseAuth () : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}