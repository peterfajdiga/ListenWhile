package peterfajdiga.playwhile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class PlayerViewModel(app: Application, audioUri: Uri, config: Player.Config) : AndroidViewModel(app) {
    val player = Player(app.applicationContext, audioUri, config)

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
