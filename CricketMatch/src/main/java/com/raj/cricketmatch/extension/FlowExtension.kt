package com.raj.cricketmatch.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> MutableSharedFlow<T>.toSharedFlow(): SharedFlow<T> = this
fun <T> MutableStateFlow<T>.toStateFlow(): SharedFlow<T> = this

inline fun <T : Any, S : SharedFlow<T>> LifecycleOwner.collectSharedFlowData(
    sharedFlow: S,
    crossinline body: (T) -> Unit
) {
    lifecycleScope.launch(Dispatchers.IO) {
        sharedFlow.flowWithLifecycle(lifecycle).collect { body(it) }
    }
}

inline fun <T : Any, S : StateFlow<T>> LifecycleOwner.collectStateFlowData(
    stateFlow: S,
    crossinline body: (T) -> Unit
) {
    lifecycleScope.launch(Dispatchers.IO) {
        stateFlow.flowWithLifecycle(lifecycle).collect { body(it) }
    }
}

inline fun <T : Any, S : SharedFlow<T>> LifecycleOwner.collectViewEvent(
    sharedFlow: S,
    crossinline body: (T) -> Unit
) {
    lifecycleScope.launch(Dispatchers.Main) {
        sharedFlow.flowWithLifecycle(lifecycle).collect { body(it) }
    }
}

inline fun LifecycleOwner.launchCoroutine(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline scope: suspend CoroutineScope.() -> Unit
) {
    lifecycle.coroutineScope.launch(coroutineContext) {
        repeatOnLifecycle(state = state) { scope(this) }
    }
}