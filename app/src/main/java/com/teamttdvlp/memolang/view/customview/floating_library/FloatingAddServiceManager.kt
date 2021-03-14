package com.teamttdvlp.memolang.view.customview.floating_library

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.dictionary.model.TranslationAndExample
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardProperty
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.language.Language
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Using
import com.teamttdvlp.memolang.data.model.other.vocabulary.VocabularyOtherStructure
import com.teamttdvlp.memolang.databinding.LayoutFloatAddBodyBinding
import com.teamttdvlp.memolang.databinding.LayoutFloatAddIconBinding
import com.teamttdvlp.memolang.view.activity.MenuActivity
import com.teamttdvlp.memolang.view.activity.SearchOnlineActivity
import com.teamttdvlp.memolang.view.activity.iview.FloatAddServiceView
import com.teamttdvlp.memolang.view.adapter.RCVRecent_SearchDictionary_Adapter
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListChooseSetNameAdapter
import com.teamttdvlp.memolang.view.customview.interpolator.StuckAtFirstInterpolator
import com.teamttdvlp.memolang.view.customview.see_vocabulary.Vocabulary_ExampleTranslation_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.Vocabulary_Mean_TextView
import com.teamttdvlp.memolang.view.customview.vocabulary_info.Vocabulary_Type_TextView
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_ExampleTranslation_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Example_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Mean_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Text_View
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.FloatAddServiceViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * Created by ericbhatti on 11/24/15.
 *
 * @author Eric Bhatti
 * @since 24 November, 2015
 */
class FloatingAddServiceManager private constructor(
    private val context: Context,
    private val headDB: LayoutFloatAddIconBinding,
    private val bodyDB: LayoutFloatAddBodyBinding,
    private val floatyOrientationListener: FloatyOrientationListener) {

    private var ratioY = 0f
    private var oldWidth = 0f
    private var oldX = 0f
    private var confChange = false

    fun getHead(): View {
        return floatManager.headDB.root
    }

    fun getBody(): View {
        return floatManager.bodyDB.root
    }

    fun startService() {
        val intent = Intent(context, FloatAddService::class.java)
        context.startService(intent)
    }

    fun stopService() {
        val intent = Intent(context, FloatAddService::class.java)
        context.stopService(intent)
    }

    // SERVICE HERE
    class FloatAddService : LifecycleService(), FloatAddServiceView {
        private val EMPTY_STRING: String = " "
        private lateinit var windowManager: WindowManager
        private lateinit  var rootViewParams: WindowManager.LayoutParams
        private lateinit var rootView: LinearLayout
        lateinit var iconTouchEventListener: GestureDetectorCompat
        lateinit var metrics: DisplayMetrics

        private lateinit var imm: InputMethodManager
        private lateinit var clipboardManager: ClipboardManager

        private val addFCPanelAppear : AnimatorSet = AnimatorSet()

        private val addFCPanelDisappear : AnimatorSet = AnimatorSet()

        private val removeZoneRadius : Int by lazy {
            removeFuncView.width
        }

        private val haftScreenWidth : Int
            get () = metrics.widthPixels / 2

        private val removeView_Y_Pos
            get () = metrics.heightPixels * 5 / 6

        private val clickLocation = IntArray(2)

        private var userWantToStopFloatAddService = false

        private var icon_Is_InRemoveZone = false

        private var isBodyDisappearAnimRunning = false

        // Turn off widgets
        private lateinit var removeFuncView : View
        private lateinit var removeFuncParams : WindowManager.LayoutParams

        private lateinit var removeFuncBackground : View
        private lateinit var removeFuncBackgroundParams : WindowManager.LayoutParams

        lateinit var inputMethodManager: InputMethodManager

        private lateinit var currentFocusedLanguageTextView : TextView

        private val STICK_ANIM_DURATION = 200L

        private val STICK_ANIM_INTERPOLATOR  = AccelerateInterpolator(0.75f)

        private val CHANNEL_ID = "FloatViewChannel"


        private var iconTargetX = 0f

        private var iconTargetY = 0f


        private var userIsTouchingScreen = false

        private var iconIsBeingMovedByAnimator = false

        private var iconIsInRemoveZone = false

        private var iconIsReadyToBeDismissed = false

        private var allowIconAutoDismissed = false

        private val MOVE_ANIM_DURATION = 100L

        private val SCALE_ANIM_DURATION = 150L
        private val SCALE_ANIM_INTERPOLATOR = DecelerateInterpolator()

        private val REMOVE_FUNCTION_APPEAR_DURATION = 200L

        private var DELETE_MOVE_ANIM_DURATION = 200L
        private var DELETE_MOVE_ANIM_INTERPOLATOR = OvershootInterpolator(3f)


        // SEARCH AND SEE VOCA PART

        lateinit var viewModel: FloatAddServiceViewModel
            @Inject set

        lateinit var rcvSearchDictionaryAdapter: RCVSearchDictionaryAdapter
            @Inject set

        lateinit var rcvRecentSearchDicAdapter: RCVRecent_SearchDictionary_Adapter
            @Inject set

        lateinit var rcvChooseSetNameAdapter : RCVSimpleListChooseSetNameAdapter
            @Inject set

        lateinit var viewModelProviderFactory: ViewModelProviderFactory
            @Inject set

        private lateinit var usingList : ArrayList<Using>

        private lateinit var translationAndExampsList: ArrayList<TranslationAndExample>



        override fun onStartCommand (intent: Intent?, flags: Int, startId: Int) : Int {
            super.onStartCommand(intent, flags, startId)
            createNotificationChannel()
            val notificationIntent = Intent(applicationContext, MenuActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0)

            val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Quick add is running")
                    .setContentText("Have a nice day !!!")
                    .setSmallIcon(android.R.drawable.ic_input_add)
                    .setContentIntent(pendingIntent)
                    .build()
            } else {
                Notification.Builder(this)
                    .setContentTitle("Quick add is running")
                    .setContentText("Have a nice day !!!")
                    .setSmallIcon(android.R.drawable.ic_input_add)
                    .setContentIntent(pendingIntent)
                    .setVibrate(null)
                    .build()
            }

            startForeground(1, notification)
            return START_STICKY
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel = NotificationChannel(
                    CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT
                )
                serviceChannel.vibrationPattern = null
                val manager: NotificationManager = getSystemService(NotificationManager::class.java)!!
                manager.createNotificationChannel(serviceChannel)
            }
        }

        private fun createViewByVocabulary (vocabulary : Vocabulary) { floatManager.bodyDB.apply {
            txtPronunciation.text = vocabulary.pronunciation
            txtText.text = vocabulary.text
            for (using in vocabulary.usings) {
                var isFirstMeanTextView = true
                val newTxtPanelType = Vocabulary_Type_TextView(applicationContext)
                newTxtPanelType.text = using.type
                contentParent.addView(newTxtPanelType)

                for (usingTranslationAndExample in using.translationAndExamsList) {
                    val newVocaMean = Vocabulary_Mean_TextView(
                        applicationContext, usingTranslationAndExample.translation
                    )

                    newVocaMean.addButton.setOnClickListener {
                        edtPanelType.setText(using.type)
                        updateTxtTrans(usingTranslationAndExample.translation)
                        updateChooseExampleFields(usingTranslationAndExample)
                        showPanelAddFlashcard()
                    }

                    if (isFirstMeanTextView) {
//                    newVocaMean.clearMarginTop()
                        isFirstMeanTextView = false
                    }
                    contentParent.addView(newVocaMean)

                    if (usingTranslationAndExample.subExampleList.size != 0) {
                        for (example in usingTranslationAndExample.subExampleList) {
                            if (example is SingleMeanExample) {
                                val newTxtExample =
                                    OtherStructure_Mean_View(
                                        applicationContext, example.text
                                    )
                                contentParent.addView(newTxtExample)
                                val exHasMean = (example.mean != "")
                                if (exHasMean) {
                                    val newTxtMeanEx =
                                        Vocabulary_ExampleTranslation_View(applicationContext)
                                    newTxtMeanEx.text = example.mean
                                    contentParent.addView(newTxtMeanEx)
                                }

                            } else if (example is VocabularyOtherStructure) {
                                val newTxtMultiMeanExample =
                                    OtherStructure_Text_View(applicationContext, example.text)
                                systemOutLogging("OtherStructure_Text_View: ${example.text}")
                                contentParent.addView(newTxtMultiMeanExample)
                                example.translationAndExample_List.forEach { transAndSubExamp ->
                                    val newSubExample = OtherStructure_Mean_View(
                                        applicationContext,
                                        text = transAndSubExamp.translation
                                    )
                                    systemOutLogging("OtherStructure_ExampleTranslation_View: ${transAndSubExamp.translation}")
                                    contentParent.addView(newSubExample)

                                    transAndSubExamp.subExampleList.forEach { example ->
                                        example as SingleMeanExample

                                        val newTxtExample = OtherStructure_Example_View(
                                            applicationContext,
                                            example.text
                                        )
                                        contentParent.addView(newTxtExample)

                                        val exHasMean = (example.mean != "")
                                        if (exHasMean) {
                                            val newTxtMeanEx =
                                                OtherStructure_ExampleTranslation_View(
                                                    applicationContext
                                                )
                                            newTxtMeanEx.text = example.mean
                                            contentParent.addView(newTxtMeanEx)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }}

        private fun updateRCVChooseTrans (using : Using) {
            translationAndExampsList = using.translationAndExamsList
            val transList = ArrayList<String>()

            translationAndExampsList.forEach { transAndEx ->
                transList.add(transAndEx.translation)
            }

            if (translationAndExampsList.size != 0) {
                updateTxtTrans(transList.first())
            }
        }

        private fun updateChooseExampleFields(translationAndEx: TranslationAndExample) {
            if (translationAndEx.subExampleList.size != 0) {
                val fullSingleExampleList =
                    convertExampleListTo_FullSingleExampleList(translationAndEx.subExampleList)
                updateTxtExampleAndExampleMean(fullSingleExampleList.first())
            } else {
                updateTxtExampleAndExampleMean(null)
            }
        }

        private fun updateTxtExampleAndExampleMean(example: SingleMeanExample?) {
            if (example != null) {
                floatManager.bodyDB.edtPanelExample.setText(example.text)
                floatManager.bodyDB.edtPanelMeanExample.setText(example.mean)
            } else {
                floatManager.bodyDB.edtPanelExample.setText("")
                floatManager.bodyDB.edtPanelMeanExample.setText("")
            }
        }

        private fun updateTxtTrans (trans : String) {
            floatManager.bodyDB.edtPanelTranslation.setText(trans)
        }

        private fun convertExampleListTo_FullSingleExampleList (exampleList : ArrayList<Example>) : ArrayList<SingleMeanExample> {
            val result = ArrayList<SingleMeanExample>()
            exampleList.forEach { example ->
                if (example is SingleMeanExample) {
                    result.add(example)
                } else if (example is VocabularyOtherStructure) {
                    example.translationAndExample_List.forEach { transAndExam ->
                        result.add(SingleMeanExample(example.text, transAndExam.translation))

                        transAndExam.subExampleList.forEach { example ->
                            if (example is SingleMeanExample) {
                                result.add(example)
                            } else Log.e("Error", "It can not contain multi-mean")
                        }

                    }


                }
            }
            return result
        }


        private fun setUpAddFCPanel(vocabulary : Vocabulary) { floatManager.bodyDB.apply {
            val usingList = vocabulary.usings
            edtPanelText.setText(vocabulary.text)
            edtPanelType.setText(usingList.first().type)

            if (usingList != null) {
                val typeList = ArrayList<String>()
                usingList.forEach { using ->
                    typeList.add(using.type)
                }

//                rcvChooseTypeAdapter.setData(typeList)
                val firstUsing = usingList.first()
                updateRCVChooseTrans(firstUsing)
                val firstTransAndExamp = firstUsing.translationAndExamsList.first()
                updateChooseExampleFields(firstTransAndExamp)
            }
        }}

        @SuppressLint("ClickableViewAccessibility")
        override fun onCreate() {
            super.onCreate()
            imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            performAndroidInjection()
            bindViewToViewModel()
            createClipBoardManager()

            initProperties()

            viewModel.vocabulary.observe(this, Observer { gottenVocabulary ->
                floatManager.bodyDB.contentParent.removeAllViews()
                createViewByVocabulary(gottenVocabulary)
                setUpAddFCPanel(gottenVocabulary)
            })

            createRootViewAndSetUpOnBackPressed()

            iconTouchEventListener = GestureDetectorCompat(floatManager.context, object : SimpleOnGestureListener() {
                private var initRootViewX = 0
                private var initRootViewY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onDown(event: MotionEvent): Boolean {
                    initRootViewX = rootViewParams.x
                    initRootViewY = rootViewParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return false
                }

                /**
                 * @param currEvent : current Motion event
                 * @param prevEvent : previous Motion event
                 */
                override fun onScroll(prevEvent: MotionEvent, currEvent: MotionEvent,
                                      distanceX: Float, distanceY: Float): Boolean {

                    hideBodyIfVisible()
                    showRemoveFunction_IfInvisible()

                    val scroll_In_Top_RemoveZone = currEvent.rawY > (removeView_Y_Pos - removeZoneRadius)
                    val scroll_In_Left_RemoveZone = (currEvent.rawX < haftScreenWidth) && (currEvent.rawX > haftScreenWidth - removeZoneRadius)
                    val scroll_In_Right_RemoveZone = (currEvent.rawX > haftScreenWidth) && (currEvent.rawX < haftScreenWidth + removeZoneRadius)
                    val scroll_InRemoveZone = scroll_In_Top_RemoveZone &&
                            (scroll_In_Left_RemoveZone or scroll_In_Right_RemoveZone)

                    if (scroll_InRemoveZone) {
                        userWantToStopFloatAddService = true
                        if (icon_Is_InRemoveZone.not()) {
                            if (iconIsBeingMovedByAnimator == false) {
                                moveIconToCenterRemoveZone ()
                                iconIsBeingMovedByAnimator = true
                            }
                            icon_Is_InRemoveZone = true
                        }
                        return false
                    } else { // Scroll out Remove Zone
                        userWantToStopFloatAddService = false

                        iconTargetX = initRootViewX + currEvent.rawX - initialTouchX
                        iconTargetY = initRootViewY + currEvent.rawY - initialTouchY

                        if (icon_Is_InRemoveZone) {
                            if (iconIsBeingMovedByAnimator == false) {
                                moveIconFromRemoveZoneTo_TargetPosition()
                            }
                            icon_Is_InRemoveZone = false
                            return false
                        }

                        if (iconIsBeingMovedByAnimator.not()) {
                            updateIconToTargetPosition()
                        }
                    }

                    return false
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    val bodyIsBeingClosed = (floatManager.bodyDB.root.visibility == GONE)
                    if (bodyIsBeingClosed) {
                        showBody()
                    }
                    return false
                }
            })

            rootViewParams = getFloatingParams()

            val configurationChange = floatManager.confChange
            if (configurationChange) {
                relayoutRespondingToChangedConfiguration()
            } else {
                rootViewParams.x = metrics.widthPixels
                rootViewParams.y = metrics.heightPixels * 1 / 2
            }

            floatManager.bodyDB.root.visibility = GONE
            floatManager.headDB.root.setOnTouchListener { _, event ->
                iconTouchEventListener.onTouchEvent(event)
                val userStartTouchScreen = (event.action == MotionEvent.ACTION_DOWN)
                val userLeaveScreen = (event.action == MotionEvent.ACTION_UP)

                if (userStartTouchScreen) {
                    userIsTouchingScreen = true
                } else if (userLeaveScreen) {
                    userIsTouchingScreen = false
                    onUserLeaveScreen()
                }

                return@setOnTouchListener  true
            }

            windowManager.addView(rootView, rootViewParams)

            addRemoveFunctionViews ()

            if (floatManager.bodyDB.root.parent != null) {
                (floatManager.bodyDB.root.parent as ViewGroup).removeView(floatManager.bodyDB.root)
            }

            addViewControls()
            addViewEvents()

            // ADD  HEAD
            val headParams = LinearLayout.LayoutParams( WRAP_CONTENT, WRAP_CONTENT)
            headParams.gravity = TOP or LEFT
            rootView.addView(floatManager.headDB.root, headParams)

            // ADD  BODY
            val bodyParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            bodyParams.gravity = TOP or LEFT
            bodyParams.setMargins(10.dp())
            floatManager.bodyDB.root.scaleX = 0f
            floatManager.bodyDB.root.scaleY = 0f

            // Set Animation Pivot to Top-Left (Don't misunderstanding with set position x and y
            floatManager.bodyDB.root.pivotX = 0f
            floatManager.bodyDB.root.pivotY = 0f

            rootView.addView(floatManager.bodyDB.root, bodyParams)
        }

        private fun createClipBoardManager() {
            clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            resetClipboardByAddEmptyClip()
        }

        private fun resetClipboardByAddEmptyClip() {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("empty", EMPTY_STRING))
        }

        private fun updateIconToTargetPosition() {
            rootViewParams.x = iconTargetX.toInt()
            rootViewParams.y = iconTargetY.toInt()
            windowManager.updateViewLayout(rootView, rootViewParams)
        }

        private fun hideBodyIfVisible() {
            if (floatManager.bodyDB.root.visibility == VISIBLE) {
                hideBody()
            }
        }

        private fun addRemoveFunctionViews () {

            removeFuncView = inflateRemoveFunctionView ()
            removeFuncParams = createRemoveFunctionParams ()

            removeFuncView.doOnPreDraw {
                // Move this view to Bottom Center position
                removeFuncView.visibility = GONE
                removeFuncParams.x = (metrics.widthPixels - removeFuncView.width) / 2
                removeFuncParams.y = removeView_Y_Pos - removeFuncView.height / 2
                windowManager.updateViewLayout(removeFuncView, removeFuncParams)
                removeFuncView.translationY = (metrics.heightPixels - removeView_Y_Pos).toFloat()
            }
            windowManager.addView(removeFuncView, removeFuncParams)

            removeFuncBackground = inflateRemoveFunctionBackground ()
            removeFuncBackgroundParams = createRemoveFunctionBackgroundParams ()
            removeFuncBackground.alpha = 0f
            removeFuncBackground.visibility = GONE
            windowManager.addView(removeFuncBackground, removeFuncBackgroundParams)

        }

        private fun relayoutRespondingToChangedConfiguration() {
            floatManager.confChange = false
            if (floatManager.oldX < floatManager.oldWidth / 2) {
                rootViewParams.x = 0
            } else {
                rootViewParams.x = metrics.widthPixels
            }
            rootViewParams.y = (metrics.heightPixels * floatManager.ratioY).toInt()
        }

        private fun getFloatingParams(): WindowManager.LayoutParams {
            val params = WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    TYPE_APPLICATION_OVERLAY
                else TYPE_PHONE,
                FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
            params.gravity = TOP or LEFT
            return params
        }

        private fun onUserLeaveScreen () {
            if (userWantToStopFloatAddService) {
                if (iconIsReadyToBeDismissed) {
                    stopServiceAfterHideDismissAllWidgets()
                } else {
                    dismissIconWhenIt_IsReady()
                }
            } else {
                hide_DismissFloatAddWidgets()

                floatManager.headDB.root.alpha = 1.0f
                val iconNearScreenRightSide = rootViewParams.x >= metrics.widthPixels / 2
                if (iconNearScreenRightSide) {
                    moveIconToScreenRightSide()
                } else { // Near left side
                    moveIconToScreenLeftSide()
                }
            }
        }

        private fun dismissIconWhenIt_IsReady() {
            allowIconAutoDismissed = true
        }

        private fun performAndroidInjection() {
            AndroidInjection.inject(this)
        }

        private fun bindViewToViewModel() {
            viewModel.setView(this)
        }

        private fun initProperties () {
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        private fun createRootViewAndSetUpOnBackPressed () {
            rootView = object : LinearLayout(applicationContext) {
                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_HOME) {
                        onBackPressed()
                        return false
                    }
                    return super.dispatchKeyEvent(event)
                }
            }
            rootView.isFocusable = true
            rootView.gravity = LEFT
            rootView.setOnClickListener {
                hideBody()
            }
            rootView.orientation = LinearLayout.VERTICAL
        }

        private fun onBackPressed () {
            if (isBodyDisappearAnimRunning.not()) {
                hideBody()
            }
        }

        private fun inflateRemoveFunctionView(): View {
            return LayoutInflater.from(applicationContext).inflate(R.layout.layout_float_add_icon_remove, null)
        }

        private fun inflateRemoveFunctionBackground(): View {
            return LayoutInflater.from(applicationContext).inflate(R.layout.layout_float_add_remove_background, null)
        }

        private fun createRemoveFunctionParams(): WindowManager.LayoutParams {
            val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                    WRAP_CONTENT, WRAP_CONTENT,
                    TYPE_APPLICATION_OVERLAY,
                    FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
            } else {
                WindowManager.LayoutParams(
                    WRAP_CONTENT, WRAP_CONTENT,
                    TYPE_PHONE,
                    FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
            }
            params.gravity = TOP or LEFT
            return params
        }

        private fun createRemoveFunctionBackgroundParams(): WindowManager.LayoutParams {
            val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                    MATCH_PARENT, WRAP_CONTENT,
                    TYPE_APPLICATION_OVERLAY,
                    FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
            } else {
                WindowManager.LayoutParams(
                    MATCH_PARENT, WRAP_CONTENT,
                    TYPE_PHONE,
                    FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
            }
            params.gravity = BOTTOM or LEFT
            return params
        }


        override fun onConfigurationChanged(newConfig: Configuration) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                val location = IntArray(2)
//                rootView.getLocationOnScreen(location)
//                floaty.oldWidth = metrics.widthPixels.toFloat()
//                floaty.confChange = true
//                if (floaty.getBody().visibility == VISIBLE) {
//                    floaty.oldX = clickLocation[0].toFloat()
//                    floaty.ratioY =clickLocation[1].toFloat() / metrics.heightPixels.toFloat()
//                } else {
//                    floaty.oldX = location[0].toFloat()
//                    floaty.ratioY = location[1].toFloat() / metrics.heightPixels.toFloat()
//                }
//                floaty.floatyOrientationListener.beforeOrientationChange(floaty)
                floatManager.stopService()
                floatManager.startService()
//                floaty.floatyOrientationListener.afterOrientationChange(floaty)
            }
        }

        private fun addViewControls () { floatManager.bodyDB.apply {
            // Choose SET NAME
            rcvChooseSetName.adapter = rcvChooseSetNameAdapter
            viewModel.getAllFlashcardSetWithNoCardList {
                rcvChooseSetNameAdapter.setData(it)
            }

            // SEARCH dictionary
            rcvDictionary.adapter = rcvSearchDictionaryAdapter
            rcvRecentSearchDic.adapter = rcvRecentSearchDicAdapter
            rcvSearchDictionaryAdapter.textColor = resources.getColor(R.color.app_blue)
            rcvRecentSearchDicAdapter.textColor = resources.getColor(R.color.app_blue)

            viewModel.getAll_RecentSearchedVocaList {
                rcvRecentSearchDicAdapter.setData(it)
            }

            edtPanelSetName.setText(viewModel.getLastedUseFlashcardSetName())

            show_LastedChosenOptions_OnScreen()
        }}

        private fun show_LastedChosenOptions_OnScreen() { floatManager.bodyDB.apply {
            edtPanelSetName.setText(viewModel.getLastedUseFlashcardSetName())
        }}

        private fun addViewEvents() {
            floatManager.bodyDB.apply {

                btnAdd.setOnClickListener {
                    showPanelAddFlashcard()
                }

                btnSpeaker.setOnClickListener {
                    viewModel.speak(txtText.text.toString())
                }

                btnPanelAdd.setOnClickListener {
                    val setName = edtPanelSetName.text.toString()
                    val text = edtPanelText.text.toString()
                    val translation = edtPanelTranslation.text.toString()
                    val example = edtPanelExample.text.toString()
                    val meanExample = edtPanelMeanExample.text.toString()
                    val type = edtPanelType.text.toString()
                    val pronunciation = txtPronunciation.text.toString()
                    val newCard = Flashcard(
                        frontLanguage = Language.ENGLISH_VALUE,
                        backLanguage = Language.VIETNAMESE_VALUE,
                        setOwner = setName,
                        type = type,
                        text = text,
                        translation = translation,
                        example = example,
                        meanOfExample = meanExample,
                        pronunciation = pronunciation,
                        cardProperty = CardProperty()
                    )
                    //TODO ( Check property)

                    viewModel.proceedAddFlashcard(newCard)
                }

                imgBlackBgAddFlashcardPanel.setOnClickListener {
                    addFCPanelDisappear.start()
                    rcvChooseSetName.goGONE()
                }

                imgChooseSetNameSpinner.setOnClickListener {
                    rcvChooseSetName.goVISIBLE()
                }

                rcvChooseSetNameAdapter.setOnItemClickListener { flashcardSet ->
                    floatManager.bodyDB.edtPanelSetName.setText(flashcardSet.name)
                    rcvChooseSetName.goGONE()
                }

                rcvSearchDictionaryAdapter.setOnItemClickListener { rawVocabulary ->
                    onChooseVocabulary(rawVocabulary as TypicalRawVocabulary)
                }

                rcvRecentSearchDicAdapter.setOnItemClickListener { rawVocabulary ->
                    onChooseVocabulary(rawVocabulary)
                }

                edtEngViDictionary.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val key = edtEngViDictionary.text.toString()
                        showVocabulary_IfKeyFound_Or_GoToSearchOnline(key = key)
                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }

            edtEngViDictionary.addTextChangeListener (onTextChanged = { text, _, _, _ ->
                if (text == "") {
                    rcvDictionary.goGONE()
                } else {
                    rcvDictionary.goVISIBLE()
                    if (rcvDictionary.alpha == 0f) {
                        rcvDictionary.alpha = 1f
                    }
                }
                rcvSearchDictionaryAdapter.search(text)
            })

                edtEngViDictionary.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        onNavigateToSearchBar()
                        if (clipboardManager.hasPrimaryClip()) {
                            val copiedText =
                                clipboardManager.primaryClip!!.getItemAt(0).text.toString()
                            if (copiedText != EMPTY_STRING) {
                                rcvSearchDictionaryAdapter.search(copiedText)
                                if (rcvSearchDictionaryAdapter.itemCount != 0) {
                                    showVocabulary_IfKeyFound_Or_GoToSearchOnline(key = copiedText)
                                }
                                resetClipboardByAddEmptyClip()
                            }
                        }
                    }
                }

                btnNavigateToSearchBar.setOnClickListener {
                    onNavigateToSearchBar()
                    showVirtualKeyboard()
                    edtEngViDictionary.setText("")
                    edtEngViDictionary.requestFocus()
                }
            }
        }

        private fun showVocabulary_IfKeyFound_Or_GoToSearchOnline(key: String) {
            val fullContent = rcvSearchDictionaryAdapter.getVocabularyByKey(key)
            if (fullContent != null) {
                onChooseVocabulary(fullContent)
            } else {
                SearchOnlineActivity.requestSearchOnline(applicationContext, key)
            }
        }


        private fun onNavigateToSearchBar() {
            val thereIsNoRecentVocabularyOnScreen = rcvRecentSearchDicAdapter.itemCount == 0
            if (thereIsNoRecentVocabularyOnScreen) {
                showDictionaryRecyclerView()
            }
            hideVocabularyInfo()
        }


        private fun onChooseVocabulary(item: TypicalRawVocabulary) {
            hideVirtualKeyboard()
            hideDictionaryRecyclerView()
            showVocabularyInfo()
            rcvRecentSearchDicAdapter.addData(item)
            floatManager.bodyDB.edtEngViDictionary.clearFocus()
            floatManager.bodyDB.edtEngViDictionary.setText("")

            viewModel.addVoca_ToRecentSearchedList(item)
            viewModel.convertToVocabularyAndSendToObserver(item)
        }

        private fun hideVirtualKeyboard () {
            imm.hideSoftInputFromWindow(floatManager.bodyDB.edtEngViDictionary.applicationWindowToken, 0)
        }

        private val COMMON_FADE_DURATION = 100L

        private fun showDictionaryRecyclerView() {
            floatManager.bodyDB.apply {
                rcvDictionary.animate().alpha(1f).setDuration(COMMON_FADE_DURATION)
                    .setLiteListener(onStart = {
                        rcvDictionary.goVISIBLE()
                    })
            }
        }

        private fun hideDictionaryRecyclerView() {
            floatManager.bodyDB.apply {
                rcvDictionary.animate().alpha(0f).setDuration(COMMON_FADE_DURATION)
                    .setLiteListener(onEnd = {
                        rcvDictionary.goGONE()
                    })
            }
        }

        private fun showVocabularyInfo() {
            floatManager.bodyDB.apply {
                vwgrpVocaInfo.animate().alpha(1f).setDuration(COMMON_FADE_DURATION)
                    .setLiteListener(onStart = {
                        vwgrpVocaInfo.goVISIBLE()
                    })
            }
        }

        private fun hideVocabularyInfo() {
            floatManager.bodyDB.apply {
                vwgrpVocaInfo.animate().alpha(0f).setDuration(COMMON_FADE_DURATION)
                    .setLiteListener(onEnd = {
                        vwgrpVocaInfo.goGONE()
                    })
            }
        }

//        private fun onChooseLanguage (language : String) {
//            currentFocusedLanguageTextView?.text = language
//            if (currentFocusedLanguageTextView == floatManager.bodyDB.txtFrontLang) {
//                viewModel.updateCurrentFrontLang(language)
//            } else if (currentFocusedLanguageTextView == floatManager.bodyDB.txtBackLang) {
//                viewModel.updateCurrentBackLanguage(language)
//            }
//            viewModel.addToUsedLanguageList(language)
//        }


        private fun minimizeAddPanel() {
            floatManager.bodyDB.apply {
//            groupExpandedPart.visibility = GONE
            }
        }

        private fun expandAddPanel() {
            floatManager.bodyDB.apply {
//            groupExpandedPart.visibility = VISIBLE
            }
        }

        private fun resetAllInputFields () { floatManager.bodyDB.apply {
            edtPanelText.setText("")
            edtPanelTranslation.setText("")
            edtPanelType.setText("")
            edtPanelExample.setText("")
            edtPanelMeanExample.setText("")
        }}

        private fun moveIconToTopLeft_And_Disappear () : ValueAnimator {
            val moveAnimator = ValueAnimator.ofInt(100, 0)
            moveAnimator.duration = MOVE_ANIM_DURATION
            moveAnimator.interpolator = FastOutLinearInInterpolator()
            val currentRootX = rootViewParams.x
            val currentRootY = rootViewParams.y
            moveAnimator.addUpdateListener {
                rootViewParams.x = (currentRootX * (1 - it.animatedFraction)).toInt()
                rootViewParams.y = (currentRootY * (1 - it.animatedFraction)).toInt()
                floatManager.headDB.root.alpha = 1 * (1 - it.animatedFraction)
                windowManager.updateViewLayout(rootView, rootViewParams)
            }
            moveAnimator.setTarget(rootView)
            moveAnimator.start()
            return moveAnimator
        }

        private fun moveRootFromTopLeft_To_PreviousPosition_And_Appear () {
            floatManager.headDB.root.visibility = VISIBLE

            val targetX = clickLocation[0]
            val targetY = clickLocation[1]

            val moveAnimator = ValueAnimator.ofInt(0, 100)
            moveAnimator.duration = MOVE_ANIM_DURATION
            moveAnimator.interpolator = FastOutLinearInInterpolator()
            moveAnimator.addUpdateListener {
                rootViewParams.x = (targetX * it.animatedFraction).toInt()
                rootViewParams.y = (targetY * it.animatedFraction).toInt()
                floatManager.headDB.root.alpha = 1 * it.animatedFraction
                windowManager.updateViewLayout(rootView, rootViewParams)
            }

            moveAnimator.setTarget(rootView)
            moveAnimator.start()
        }

        private fun showRemoveFunction () {
            removeFuncView.animate().alpha(1f)
                .translationY(0f)
                .setDuration(REMOVE_FUNCTION_APPEAR_DURATION)
                .setInterpolator(FastOutSlowInInterpolator()).setLiteListener(onStart = {
                    removeFuncView.visibility = VISIBLE
                })
                .start()

            removeFuncBackground.animate().alpha(1f).setDuration(REMOVE_FUNCTION_APPEAR_DURATION)
                .setInterpolator(FastOutSlowInInterpolator()).setLiteListener(onStart = {
                    removeFuncBackground.visibility = VISIBLE
                })
                .start()
        }

        private fun showRemoveFunction_IfInvisible () {
            if (removeFuncView.isVisible.not()) {
                showRemoveFunction()
            }
        }

        private fun hide_DismissFloatAddWidgets (stopService : Boolean = false) {
            removeFuncView.animate().alpha(0f)
                .translationY((metrics.heightPixels - removeView_Y_Pos).toFloat())
                .setDuration(REMOVE_FUNCTION_APPEAR_DURATION)
                .setInterpolator(FastOutSlowInInterpolator()).setLiteListener(onEnd = {
                    removeFuncView.visibility = GONE
                    if (stopService) {
                        floatManager.stopService()
                    }
                })
                .start()

            removeFuncBackground.animate().alpha(0f).setDuration(REMOVE_FUNCTION_APPEAR_DURATION)
                .setInterpolator(FastOutSlowInInterpolator()).setLiteListener(onEnd = {
                    removeFuncBackground.visibility = GONE
                })
                .start()
        }

        private fun hideFloatIcon () {
            rootView.animate().alpha(0f)
                .translationY((metrics.heightPixels - removeView_Y_Pos).toFloat())
                .setDuration(REMOVE_FUNCTION_APPEAR_DURATION)
                .setInterpolator(FastOutSlowInInterpolator()).setLiteListener(onEnd = {
                    removeFuncView.visibility = GONE
                })
                .start()
        }

        private fun stopServiceAfterHideDismissAllWidgets () {
            hide_DismissFloatAddWidgets(true)
            hideFloatIcon()
        }

        private fun moveIconToScreenRightSide () {
            val stickAnim = ValueAnimator.ofInt(rootViewParams.x, metrics.widthPixels)
            stickAnim.duration = STICK_ANIM_DURATION
            stickAnim.interpolator = STICK_ANIM_INTERPOLATOR
            stickAnim.addUpdateListener {
                rootViewParams.x = it.animatedValue as Int
                windowManager.updateViewLayout(rootView, rootViewParams)
            }
            stickAnim.setTarget(rootView)
            stickAnim.start()
        }

        private fun moveIconToScreenLeftSide () {
            val stickAnim = ValueAnimator.ofInt(rootViewParams.x, 0)
            stickAnim.duration = STICK_ANIM_DURATION
            stickAnim.interpolator = STICK_ANIM_INTERPOLATOR
            stickAnim.addUpdateListener {
                rootViewParams.x = it.animatedValue as Int
                windowManager.updateViewLayout(rootView, rootViewParams)
            }
            stickAnim.setTarget(rootView)
            stickAnim.start()
        }

        private fun moveIconToCenterRemoveZone () {
            val currentX = rootViewParams.x
            val currentY = rootViewParams.y
            val targetX = (metrics.widthPixels - floatManager.headDB.root.width) / 2
            val targetY = removeView_Y_Pos - floatManager.headDB.root.height / 2
            val anim = ValueAnimator.ofInt(0, 100)
            anim.addUpdateListener {
                rootViewParams.x = (currentX + (targetX - currentX) * it.animatedFraction).toInt()
                rootViewParams.y = (currentY + (targetY - currentY) * it.animatedFraction).toInt()
                windowManager.updateViewLayout(rootView, rootViewParams)
            }
            anim.duration = DELETE_MOVE_ANIM_DURATION
            anim.interpolator = DELETE_MOVE_ANIM_INTERPOLATOR
            anim.addListener (onEnd = {
                if (allowIconAutoDismissed) {
                    stopServiceAfterHideDismissAllWidgets()
                }

                iconIsBeingMovedByAnimator = false
                iconIsInRemoveZone = true
                iconIsReadyToBeDismissed = true
            })
            anim.start()
        }

        private fun moveIconFromRemoveZoneTo_TargetPosition() {
            val currentX = (metrics.widthPixels - floatManager.headDB.root.width) / 2
            val currentY = removeView_Y_Pos - floatManager.headDB.root.height / 2

            val anim = ValueAnimator.ofInt(0, 100)
            anim.addUpdateListener {
                if (userIsTouchingScreen) {
                    rootViewParams.x = (currentX + (iconTargetX - currentX) * it.animatedFraction).toInt()
                    rootViewParams.y = (currentY + (iconTargetY - currentY) * it.animatedFraction).toInt()
                    windowManager.updateViewLayout(rootView, rootViewParams)
                } else {
                    anim.cancel()
                }
            }
            anim.duration = DELETE_MOVE_ANIM_DURATION * 3
            anim.interpolator =
                StuckAtFirstInterpolator()
            anim.addListener (onStart = {
                iconIsBeingMovedByAnimator = true
            }, onEnd = {
                iconIsBeingMovedByAnimator = false
            })
            anim.start()
        }

        private fun startAndGet_AddPanelDisappearAnim () : ViewPropertyAnimator {
            isBodyDisappearAnimRunning = true
            return floatManager.bodyDB.root.animate()
                .scaleX(0f).scaleY(0f).alpha(0f)
                .setDuration(SCALE_ANIM_DURATION)
                .setInterpolator(SCALE_ANIM_INTERPOLATOR)
        }

        private fun startAndGetAddPanelAppearAnim () : ViewPropertyAnimator {
            return floatManager.bodyDB.root.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(SCALE_ANIM_DURATION)
                .setInterpolator(SCALE_ANIM_INTERPOLATOR)
        }

        private fun showBody () {
            floatManager.headDB.root.getLocationOnScreen(clickLocation)
            moveIconToTopLeft_And_Disappear ().addListener (onEnd = {

                rootViewParams.flags = FLAG_NOT_TOUCH_MODAL
                rootViewParams.width = MATCH_PARENT
                rootViewParams.height = MATCH_PARENT
                floatManager.headDB.root.visibility = GONE
                windowManager.updateViewLayout(rootView, rootViewParams)

                startAndGetAddPanelAppearAnim().setLiteListener(onStart = {
                    floatManager.bodyDB.root.visibility = VISIBLE
                }, onEnd = {
                    // TODO (MOVE THIS STUFF BELOW TO ANOTHER PLACE) WE need Clean Code
                    floatManager.bodyDB.edtEngViDictionary.requestFocus()
                    showVirtualKeyboard()
                }).start()
            })
        }

        private fun hideBody () {
            //Hide body
            startAndGet_AddPanelDisappearAnim().setLiteListener (onEnd = {
                isBodyDisappearAnimRunning = false
                floatManager.bodyDB.root.visibility = GONE
                rootViewParams.width = WRAP_CONTENT
                rootViewParams.height = WRAP_CONTENT
                rootViewParams.flags = FLAG_NOT_FOCUSABLE

                windowManager.updateViewLayout(rootView, rootViewParams)
                moveRootFromTopLeft_To_PreviousPosition_And_Appear ()
            }).start()
        }

        override fun onDestroy() {
            super.onDestroy()
            if (::rootView.isInitialized) {
                rootView.removeAllViews()
                windowManager.removeView(rootView)
                windowManager.removeView(removeFuncView)
                windowManager.removeView(removeFuncBackground)
            }
            stopForeground(true)
            viewModel.saveUsingHistory()
        }

        override fun onBind(intent: Intent): IBinder? {
            super.onBind(intent)
            return null
        }

        override fun showInvalidFlashcardSetError(errorMessage: String) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
        }


        private val TEXT_ERROR_TEXT = "Text must not be empty"

        private val TRANSLATION_ERROR_TEXT = "Translation must not be empty"

        override fun showTextInputError() {
            floatManager.bodyDB.edtPanelText.hint = TEXT_ERROR_TEXT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                floatManager.bodyDB.edtPanelText.setHintTextColor(applicationContext.getColor(R.color.app_red))
            }
        }

        override fun showTranslationInputError() {
            floatManager.bodyDB.edtPanelTranslation.hint = TRANSLATION_ERROR_TEXT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                floatManager.bodyDB.edtPanelTranslation.setHintTextColor(
                    applicationContext.getColor(
                        R.color.app_red
                    )
                )
            }
        }

        override fun onAddFlashcardSuccess() {
            hidePanelAddFlashcard()
            hideBody()
            val AddFCPanelDisappear_RelativeDuration = 100L
            playImageAddSuccessAnim(startDelay = AddFCPanelDisappear_RelativeDuration)
            resetAllInputFields()
        }

        private fun playImageAddSuccessAnim(startDelay: Long = 0) {
            floatManager.bodyDB.apply {
                imgAddFcSuccess.animate().scaleX(2f).scaleY(2f).alpha(1f).setDuration(250)
                    .setInterpolator(FastOutLinearInInterpolator()).setStartDelay(startDelay)
                    .setLiteListener(
                        onEnd = {
                            imgAddFcSuccess.animate().alpha(0f)
                                .setStartDelay(0) // Reset startDelay
                                .setLiteListener(onEnd = {
                                    // Do nothing, Reset the above setLiteListener()
                                })
                        })
            }
        }


        private fun hidePanelAddFlashcard() {
            addFCPanelDisappear.start()
        }

        private fun showPanelAddFlashcard() {
            addFCPanelAppear.start()
        }

        private fun showVirtualKeyboard() {
            inputMethodManager.toggleSoftInput(RESULT_UNCHANGED_SHOWN, SHOW_IMPLICIT)
        }


        @Inject
        fun initAddFlashcardPanelAnimations(
            @Named("Appear50Percents") blackBackgroundAppear: Animator,
            @Named("Disappear50Percents") blackBackgroundDisappear: Animator,
            @Named("FromNothingToNormalSize") panelAppear : Animator,
            @Named("FromNormalSizeToNothing") panelDisappear: Animator
        ) { floatManager.bodyDB.apply {

            blackBackgroundAppear.setTarget(imgBlackBgAddFlashcardPanel)
            panelAppear.setTarget(panelAddFlashcard)
            addFCPanelAppear.play(blackBackgroundAppear).before(panelAppear)
            addFCPanelAppear.addListener (onStart = {
                groupAddFlashcard.goVISIBLE()
            })

            blackBackgroundDisappear.setTarget(imgBlackBgAddFlashcardPanel)
            panelDisappear.setTarget(panelAddFlashcard)
            addFCPanelDisappear.play(panelDisappear).before(blackBackgroundDisappear)
            addFCPanelDisappear.addListener (onEnd = {
                groupAddFlashcard.goGONE()
            })
        }}
    }

    companion object {
        private lateinit var floatManager: FloatingAddServiceManager
        private const val LOG_TAG = "Floaty"

        @Synchronized
        fun createInstance( context: Context,
                            head: LayoutFloatAddIconBinding,
                            body: LayoutFloatAddBodyBinding,
                            floatyOrientationListener: FloatyOrientationListener ): FloatingAddServiceManager? {

            if (::floatManager.isInitialized) {
                floatManager = FloatingAddServiceManager(context, head, body, floatyOrientationListener)
            }
            return floatManager
        }

        @Synchronized
        fun createInstance(
            context: Context,  head: LayoutFloatAddIconBinding, body: LayoutFloatAddBodyBinding): FloatingAddServiceManager? {
            if (::floatManager.isInitialized.not()) {
                floatManager = FloatingAddServiceManager(context, head, body, object : FloatyOrientationListener {
                    override fun beforeOrientationChange(floatingQuickAddService: FloatingAddServiceManager) {
                        Log.e(LOG_TAG, "beforeOrientationChange")
                    }

                    override fun afterOrientationChange(floatingQuickAddService: FloatingAddServiceManager) {
                        Log.e(LOG_TAG, "afterOrientationChange")
                    }
                })
            }
            return floatManager
        }

        @get:Synchronized
        val instance: FloatingAddServiceManager?
            get() {
                if (::floatManager.isInitialized) {
                    throw NullPointerException("Floaty not initialized! First call createInstance method, then to access Floaty in any other class call getInstance()")
                }
                return floatManager
            }

    }

}