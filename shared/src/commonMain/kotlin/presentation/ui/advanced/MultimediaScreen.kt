package presentation.ui.advanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import presentation.ui.components.AdvancedAnimations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultimediaScreen(
    onVideoRecord: () -> Unit,
    onVideoStop: () -> Unit,
    onPlayPause: () -> Unit,
    isRecording: Boolean,
    isPlaying: Boolean,
    currentProgress: Float,
    recentItems: List<MediaItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showControls by remember { mutableStateOf(true) }
    var volume by remember { mutableStateOf(0.8f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Multimedia") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Video Preview Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Video preview content will be rendered here
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        AdvancedAnimations.FadeInOutAnimation(visible = showControls) {
            LinearProgressIndicator(
                progress = currentProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Record Button
            AdvancedAnimations.PulseAnimation { scale ->
                FloatingActionButton(
                    onClick = {
                        if (isRecording) onVideoStop() else onVideoRecord()
                    },
                    modifier = Modifier.scale(if (isRecording) scale else 1f)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.VideoCall,
                        contentDescription = if (isRecording) "Stop Recording" else "Start Recording"
                    )
                }
            }

            // Play/Pause Button
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Media Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Volume Control
            Slider(
                value = volume,
                onValueChange = { volume = it },
                modifier = Modifier.width(200.dp)
            )
            
            IconButton(onClick = { volume = if (volume > 0f) 0f else 0.8f }) {
                Icon(
                    imageVector = if (volume > 0f) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Volume"
                )
            }
        }

        // Recently Recorded Items
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentItems) { item ->
                MediaThumbnail(
                    item = item,
                    onClick = { /* Handle click */ }
                )
            }
        }
    }
}

@Composable
private fun MediaThumbnail(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Thumbnail image would be displayed here
            Text(
                text = item.name,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class MediaItem(
    val id: String,
    val name: String,
    val type: MediaType,
    val duration: Long,
    val thumbnailUrl: String?
)

enum class MediaType {
    VIDEO,
    AUDIO
}
