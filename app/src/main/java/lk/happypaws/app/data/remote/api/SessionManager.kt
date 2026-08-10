package lk.happypaws.app.data.remote.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _sessionExpiredEvent = MutableSharedFlow<String>()
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    suspend fun notifySessionExpired(message: String = "Your session has expired for your security. Please log in again to continue.") {
        _sessionExpiredEvent.emit(message)
    }
}
