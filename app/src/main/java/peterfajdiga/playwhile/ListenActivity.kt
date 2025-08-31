package peterfajdiga.playwhile

import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import peterfajdiga.playwhile.ui.theme.PlayWhileTheme

class ListenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioUri: Uri = intent?.data
            ?: throw IllegalArgumentException("No audio URI provided in intent")
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
fun AudioPlayerScreen(audioUri: Uri, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember { Player(context, audioUri) }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Audio URI: $audioUri")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                player.play()
            }) {
                Text("Play")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                player.pause()
            }) {
                Text("Pause")
            }
        }
    }
}
