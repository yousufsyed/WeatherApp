package com.yousuf.weatherapp.provider

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class MessageDelegate {

    companion object {
        private val _events = Channel<SnackbarEvent>()
        val events = _events.receiveAsFlow()

        fun showSnackbar(message: String, action: SnackbarAction? = null) {
            _events.trySend(SnackbarEvent(message, action))
        }
    }
}

data class SnackbarAction(val name: String, val action: () -> Unit)

data class SnackbarEvent(val message: String, val action: SnackbarAction? = null)