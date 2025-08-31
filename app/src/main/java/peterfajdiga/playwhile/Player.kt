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
    private val mediaSession = setupMediaSession(context)

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun release() {
        mediaPlayer.release()
    }
}

private fun setupMediaSession(context: Context): MediaSession {
    val mediaSession = MediaSession(context, "PlayWhilePlayerMediaSession").apply {
        setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                Log.d("MediaSession", "Play pressed")
            }

            override fun onPause() {
                Log.d("MediaSession", "Pause pressed")
            }

            override fun onSkipToPrevious() {
                Log.d("MediaSession", "Skip to Previous pressed")
            }
        })
        isActive = true
    }

    val playbackState = PlaybackState.Builder()
        .setActions(
            PlaybackState.ACTION_PLAY or
            PlaybackState.ACTION_PAUSE or
            PlaybackState.ACTION_SKIP_TO_PREVIOUS
        )
        .setState(PlaybackState.STATE_PAUSED, 0L, 1.0f)
        .build()

    mediaSession.setPlaybackState(playbackState)

    return mediaSession
}
