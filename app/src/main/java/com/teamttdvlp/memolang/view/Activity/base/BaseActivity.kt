package com.teamttdvlp.memolang.view.Activity.base

import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.android.AndroidInjection
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN


abstract class BaseActivity <T : ViewDataBinding, V : ViewModel> : FragmentActivity() {

    lateinit var dataBinding : T
    private set

    lateinit var viewModel : V
    private set

    private lateinit var imm : InputMethodManager


    abstract fun getLayoutId () : Int

    abstract fun takeViewModel () : V

    override fun onCreate(savedInstanceState: Bundle?) {
        performDataBinding()
        performDependenciesInjection()
        super.onCreate(savedInstanceState)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        performCreateViewModel()
        initProperties()
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

    fun hideVirtualKeyboard () {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun showVirtualKeyboard () {
        imm.toggleSoftInput(RESULT_UNCHANGED_SHOWN, SHOW_IMPLICIT)
    }

    open fun addViewEvents() {}

    open fun addViewControls() {}

    open fun addEventsListener() {}

    open fun initProperties () {}
}