package com.teamttdvlp.memolang.view.base

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.teamttdvlp.memolang.R
import dagger.android.AndroidInjection


abstract class BaseActivity <T : ViewDataBinding, V : ViewModel> : FragmentActivity() {

    lateinit var dB : T
        private set

    lateinit var viewModel : V
        private set

    lateinit var imm : InputMethodManager

    abstract fun getLayoutId () : Int

    abstract fun takeViewModel () : V

    override fun onCreate(savedInstanceState: Bundle?) {
        performDataBinding()
        performDependenciesInjection()
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        overrideEnterAnim()
        performCreateViewModel()
        initProperties()
        addViewSettings()
        addViewEvents()
        addEventsListener()
        addAnimationEvents()
        updateTheme()
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
        dB = DataBindingUtil.setContentView(this, getLayoutId())
    }

    private fun performDependenciesInjection () {
        AndroidInjection.inject(this)
    }

    open fun hideVirtualKeyboard () {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    open fun showVirtualKeyboard () {
        imm.toggleSoftInput(RESULT_UNCHANGED_SHOWN, SHOW_IMPLICIT)
    }

    fun setStatusBarColor (color : Int) {
        window.statusBarColor = color
    }

    open fun addViewEvents() {}

    open fun addViewSettings() {}

    open fun addEventsListener() {}

    open fun initProperties() {}

    open fun updateTheme() {}

    // Create Enter Transition For Activity
    open fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.nothing)
    }

    // Create Exit Transition For Activity
    open fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.from_centre_to_right)
    }

    open fun addAnimationEvents() {

    }

}

