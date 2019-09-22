package com.teamttdvlp.memolang.view.activity.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ViewModelFactory
//    @Inject constructor
//        (private val viewModelsMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>)
//             : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>) : T {
//        val creator = viewModelsMap[modelClass] ?:
//        viewModelsMap.asIterable().firstOrNull {
//            modelClass.isAssignableFrom(it.key)
//        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
//        return try {
//            creator.get() as T
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }
//
//}

class ViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
