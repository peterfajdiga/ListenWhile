package peterfajdiga.playwhile

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import peterfajdiga.playwhile.ui.theme.PlayWhileTheme
import kotlin.time.Duration.Companion.milliseconds

const val REWIND_S = 10
const val ADVANCE_S = 4

class ListenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioUri: Uri = intent?.data
            ?: throw IllegalArgumentException("No audio URI provided in intent")
        setContent {
            PlayWhileTheme(densityScale = 2f) {
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
    val playerViewModel: PlayerViewModel = viewModel(
        factory = viewModelFactory {
            initializer { PlayerViewModel(
                context.applicationContext as Application,
                audioUri,
                Player.Config(REWIND_S * 1000, ADVANCE_S * 1000),
            ) }
        }
    )
    val player = playerViewModel.player
    val duration = player.getDuration()
    var position by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var seekbarPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    val updatePosition: () -> Unit = {
        position = player.getCurrentPosition()
        if (!isSeeking) {
            seekbarPosition = position.toFloat()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            isPlaying = player.isPlaying()
            updatePosition()
            delay(500)
        }
    }

    fun formatPosition(ms: Int): String {
        val dur = ms.milliseconds
        val h = dur.inWholeHours
        val m = dur.inWholeMinutes % 60
        val s = dur.inWholeSeconds % 60
        return if (h > 0) {
            "%d:%02d:%02d".format(h, m, s)
        } else {
            "%d:%02d".format(m, s)
        }
    }

    Box(modifier = modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.weight(1f)) {
                    val opacityModifier = if (isSeeking) Modifier.alpha(0.25f) else Modifier
                    Text(text = formatPosition(position), modifier = opacityModifier)
                    if (isSeeking) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "->", opacityModifier)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = formatPosition(seekbarPosition.toInt()))
                    }
                }
                Text(
                    text = formatPosition(duration),
                    textAlign = TextAlign.End
                )
            }
            Slider(
                value = seekbarPosition.coerceIn(0f, duration.toFloat()),
                onValueChange = {
                    seekbarPosition = it
                    isSeeking = true
                },
                onValueChangeFinished = {
                    player.seekTo(seekbarPosition.toInt())
                    isSeeking = false
                },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    player.rewind()
                    updatePosition()
                }, modifier = Modifier.height(48.dp).weight(1f)) {
                    Text("-%ds".format(REWIND_S))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    player.advance()
                    updatePosition()
                }, modifier = Modifier.height(48.dp).weight(1f)) {
                    Text("+%ds".format(ADVANCE_S))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    if (isPlaying) {
                        player.pause()
                        isPlaying = false
                    } else {
                        player.play()
                        isPlaying = true
                    }
                }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text(if (isPlaying) "Pause" else "Play")
                }
            }
        }
    }
}
