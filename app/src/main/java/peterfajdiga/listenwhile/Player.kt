package peterfajdiga.listenwhile

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri

class Player(
    context: Context,
    audioUri: Uri,
    val config: Config,
) {
    data class Config(
        val rewindMs: Int,
        val advanceMs: Int,
    )

    private val mediaPlayer = MediaPlayer.create(context, audioUri)
    private val mediaSession = MediaSession(context, "ListenWhilePlayerMediaSession")
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var hasFocus = false

    fun play() {
        requestAudioFocus()
        mediaPlayer.start()
        updatePlaybackState(PlaybackState.STATE_PLAYING)
    }

    fun pause() {
        mediaPlayer.pause()
        updatePlaybackState(PlaybackState.STATE_PAUSED)
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer.seekTo(
            positionMs.coerceIn(0, mediaPlayer.duration),
        )
    }

    fun seekBy(deltaMs: Int) {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + deltaMs)
    }

    fun rewind() {
        seekBy(-config.rewindMs)
    }

    fun advance() {
        seekBy(config.advanceMs)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun release() {
        mediaPlayer.release()
    }

    init {
        setupMediaSession()
    }

    private fun updatePlaybackState(state: Int) {
        val playbackState = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_REWIND or
                PlaybackState.ACTION_FAST_FORWARD
            )
            .setState(state, mediaPlayer.currentPosition.toLong(), 1.0f)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun setupMediaSession() {
        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                play()
            }

            override fun onPause() {
                pause()
            }

            override fun onSkipToPrevious() {
                rewind()
            }

            override fun onSkipToNext() {
                advance()
            }

            override fun onRewind() {
                rewind()
            }

            override fun onFastForward() {
                advance()
            }
        })
        mediaSession.isActive
        updatePlaybackState(PlaybackState.STATE_NONE)
    }

    private fun requestAudioFocus() {
        if (hasFocus) {
            return
        }

        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
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
                        pause()
                    }
                }
            }
            .build()
        if (audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            hasFocus = true
        }
    }
}
