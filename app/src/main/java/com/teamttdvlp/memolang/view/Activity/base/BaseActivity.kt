package com.teamttdvlp.memolang.view.Activity.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.android.AndroidInjection

abstract class BaseActivity <T : ViewDataBinding, V : ViewModel> : FragmentActivity() {

    lateinit var dataBinding : T
    private set

    lateinit var viewModel : V
    private set

    abstract fun getLayoutId () : Int

    abstract fun takeViewModel () : V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        performDependenciesInjection()
        performCreateViewModel()
        addViewControls()
        addViewEvents()
        addEventsListener()
    }

    private fun performCreateViewModel () {
        viewModel = takeViewModel()
    }

    private fun performDataBinding () {
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId())
    }

    private fun performDependenciesInjection () {
        AndroidInjection.inject(this)
    }

    open fun addViewEvents() {}

    open fun addViewControls() {}

    open fun addEventsListener() {}
}