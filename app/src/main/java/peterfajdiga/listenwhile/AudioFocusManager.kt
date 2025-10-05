package peterfajdiga.listenwhile

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

class AudioFocusManager(
    context: Context,
    val audioFocusLossCallback: () -> Unit,
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        )
        .setOnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    hasFocus = false
                    audioFocusLossCallback()
                }
            }
        }
        .build()
    private var hasFocus = false

    fun requestAudioFocus() {
        if (hasFocus) {
            return
        }

        val requestResult = audioManager.requestAudioFocus(focusRequest)
        if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            hasFocus = true
        }
    }

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}
