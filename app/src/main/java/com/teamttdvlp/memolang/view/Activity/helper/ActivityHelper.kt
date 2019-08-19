package com.teamttdvlp.memolang.view.Activity.helper

import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

fun FragmentActivity.quickStartActivity (target : Class<*>) {
    startActivity(Intent(this, target))
}

fun FragmentActivity.quickToast (message : String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.disappear() {
    visibility = GONE
}

fun View.appear() {
    visibility = VISIBLE
}

inline fun <reified T : ViewModel> Fragment.getFragmentViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getActivityViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

