package peterfajdiga.playwhile

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import peterfajdiga.playwhile.ui.theme.PlayWhileTheme

class ListenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioUri: Uri? = intent?.data
        setContent {
            PlayWhileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioPlayerScreen(audioUri, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AudioPlayerScreen(audioUri: Uri?, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    DisposableEffect(audioUri) {
        if (audioUri != null) {
            mediaPlayer = MediaPlayer.create(context, audioUri)
        }
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Audio URI: ${audioUri ?: "None"}")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                mediaPlayer?.start()
                isPlaying = true
            }, enabled = !isPlaying && mediaPlayer != null) {
                Text("Play")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                mediaPlayer?.pause()
                isPlaying = false
            }, enabled = isPlaying && mediaPlayer != null) {
                Text("Pause")
            }
        }
    }
}
