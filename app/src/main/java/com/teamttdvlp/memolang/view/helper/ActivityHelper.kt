package com.teamttdvlp.memolang.view.helper

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

fun Activity.quickStartActivity (target : Class<*>) {
  startActivity(Intent(this, target))
}

fun Activity.quickToast (message : String) {
  Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

inline fun <reified T : ViewModel> FragmentActivity.getActivityViewModel(factory: ViewModelProviderFactory? = null): T {
  return if (factory == null)
    ViewModelProviders.of(this).get(T::class.java)
  else
    ViewModelProviders.of(this, factory).get(T::class.java)
}

