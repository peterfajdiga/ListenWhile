package peterfajdiga.playwhile

import android.content.Context
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.util.Log

class Player(
    context: Context,
    audioUri: Uri,
) {
    private val mediaPlayer = MediaPlayer.create(context, audioUri)
    private val mediaSession = MediaSession(context, "PlayWhilePlayerMediaSession")

    fun play() {
        mediaPlayer.start()
        updatePlaybackState(PlaybackState.STATE_PLAYING)
    }

    fun pause() {
        mediaPlayer.pause()
        updatePlaybackState(PlaybackState.STATE_PAUSED)
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
                PlaybackState.ACTION_SKIP_TO_PREVIOUS
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
                Log.d("MediaSession", "Skip to Previous pressed")
            }
        })
        mediaSession.isActive
        updatePlaybackState(PlaybackState.STATE_NONE)
    }
}
