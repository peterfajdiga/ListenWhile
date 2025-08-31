package peterfajdiga.playwhile

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import peterfajdiga.playwhile.ui.theme.PlayWhileTheme

class PickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayWhileTheme {
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
    Column(modifier = modifier.padding(16.dp)) {
        Button(onClick = {
            launcher.launch(arrayOf("audio/*"))
        }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Pick Audio File")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlayWhileTheme {
        AudioPickerScreen()
    }
}
