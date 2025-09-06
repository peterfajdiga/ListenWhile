package peterfajdiga.listenwhile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import peterfajdiga.listenwhile.ui.theme.ListenWhileTheme

class PickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenWhileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioPickerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AudioPickerScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var rssUrl by remember { mutableStateOf("") }

    if (selectedAudioUri != null) {
        val intent = Intent(context, ListenActivity::class.java).apply {
            data = selectedAudioUri
        }
        @Suppress("AssignedValueIsNeverRead")
        selectedAudioUri = null // allow picking the same file again
        context.startActivity(intent)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> selectedAudioUri = uri }
    )
    Box(modifier = modifier.padding(16.dp).fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                launcher.launch(arrayOf("audio/*"))
            }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Pick Audio File")
            }
            androidx.compose.material3.OutlinedTextField(
                value = rssUrl,
                onValueChange = { rssUrl = it },
                label = { Text("Podcast RSS URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val intent = Intent(context, PodcastEpisodesActivity::class.java).apply {
                        putExtra("rss_url", rssUrl)
                    }
                    context.startActivity(intent)
                },
                enabled = rssUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Show Podcast Episodes")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListenWhileTheme {
        AudioPickerScreen()
    }
}
