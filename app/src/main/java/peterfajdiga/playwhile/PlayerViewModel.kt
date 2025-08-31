package peterfajdiga.playwhile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class PlayerViewModel(app: Application, audioUri: Uri) : AndroidViewModel(app) {
    val player = Player(app.applicationContext, audioUri)

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
