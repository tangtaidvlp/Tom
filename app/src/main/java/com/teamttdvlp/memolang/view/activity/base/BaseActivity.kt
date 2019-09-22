package com.teamttdvlp.memolang.view.activity.base

import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.android.AndroidInjection
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import com.teamttdvlp.memolang.R


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
        overrideEnterAnim()
        performCreateViewModel()
        initProperties()
        addViewControls()
        addViewEvents()
        addEventsListener()
    }

    override fun finish() {
        super.finish()
        overrideExitAnim()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overrideExitAnim()
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

    // Create Enter Transition For Activity
    open fun overrideEnterAnim () {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.nothing)
    }

    // Create Exit Transition For Activity
    open fun overrideExitAnim () {
        overridePendingTransition(0, R.anim.from_centre_to_right)
    }

}