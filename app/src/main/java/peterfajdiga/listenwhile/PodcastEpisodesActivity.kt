package peterfajdiga.listenwhile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.net.HttpURLConnection

data class Episode(val title: String, val audioUrl: String?)

class PodcastEpisodesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rssUrl = intent.getStringExtra("rss_url") ?: ""
        setContent {
            peterfajdiga.listenwhile.ui.theme.ListenWhileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PodcastEpisodesScreen(rssUrl, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PodcastEpisodesScreen(rssUrl: String, modifier: Modifier = Modifier) {
    var episodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(rssUrl) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(rssUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"
                conn.connect()
                val inputStream = conn.inputStream
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(inputStream, null)
                val items = mutableListOf<Episode>()
                var eventType = parser.eventType
                var insideItem = false
                var title: String? = null
                var audioUrl: String? = null
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tagName = parser.name
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (tagName == "item") insideItem = true
                            if (insideItem && tagName == "title") title = parser.nextText()
                            if (insideItem && tagName == "enclosure") {
                                audioUrl = parser.getAttributeValue(null, "url")
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (tagName == "item") {
                                insideItem = false
                                if (title != null) {
                                    items.add(Episode(title!!, audioUrl))
                                }
                                title = null
                                audioUrl = null
                            }
                        }
                    }
                    eventType = parser.next()
                }
                episodes = items
                loading = false
            } catch (e: Exception) {
                error = "Failed to load RSS feed: ${e.localizedMessage}"
                loading = false
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Podcast Episodes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        when {
            loading -> Text("Loading...")
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            episodes.isEmpty() -> Text("No episodes found.")
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(episodes) { episode ->
                    episode.audioUrl?.let { audioUrl ->
                        ListItem(
                            headlineContent = { Text(episode.title) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = android.content.Intent(context, ListenActivity::class.java).apply {
                                        data = android.net.Uri.parse(audioUrl)
                                    }
                                    context.startActivity(intent)
                                }
                        )
                    } ?: ListItem(
                        headlineContent = { Text(episode.title) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
