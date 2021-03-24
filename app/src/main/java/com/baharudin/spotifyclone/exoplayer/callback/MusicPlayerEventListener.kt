package com.baharudin.spotifyclone.exoplayer.callback

import android.widget.Toast
import com.baharudin.spotifyclone.exoplayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
        private val musixService : MusicService
) : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady) {
            musixService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Toast.makeText(musixService, "Ada masalah di jaringan", Toast.LENGTH_SHORT).show()
    }
}