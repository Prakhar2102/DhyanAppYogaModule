package com.shubham.detect_pose.ui.theme

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@Composable
fun VideoPlayer(videoResId: Int){
    val context = LocalContext.current
    val player = remember { SimpleExoPlayer.Builder(context).build() }
    val playerView = remember { PlayerView(context) }
    val mediaItem = remember(videoResId) {
        MediaItem.fromUri("android.resource://${context.packageName}/$videoResId")
    }
    val playWhenReady by rememberSaveable { mutableStateOf(true) }

    player.setMediaItem(mediaItem)
    playerView.player = player
//    playerView.useController = false
    player.repeatMode = SimpleExoPlayer.REPEAT_MODE_ONE
    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
        launch {
            delay(1.minutes) // Wait for 1 minute
            player.seekTo(0) // Rewind the video to the beginning
            player.play()
        }
    }
    DisposableEffect(player) {
        onDispose {
            player.release() // Release the resources used by the player
        }
    }

    AndroidView(factory = { playerView })
}

