package peterfajdiga.listenwhile

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.core.content.edit

const val POSITION_RESTORE_REWIND_MS = 3000

class PlayerViewModel(app: Application, audioUri: Uri, config: Player.Config) : AndroidViewModel(app) {
    val player = Player(app.applicationContext, audioUri, config)
    private val prefs: SharedPreferences = app.getSharedPreferences("audio_positions", 0)
    private val uriKey = audioUri.toString()

    init {
        val savedPosition = prefs.getInt(uriKey, 0) - POSITION_RESTORE_REWIND_MS
        if (savedPosition > 0) {
            player.seekTo(savedPosition)
        }
    }

    fun saveCurrentPosition() {
        val pos = player.getCurrentPosition()
        prefs.edit { putInt(uriKey, pos) }
    }

    override fun onCleared() {
        saveCurrentPosition()
        player.release()
        super.onCleared()
    }
}
