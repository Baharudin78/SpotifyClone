package com.baharudin.spotifyclone.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.baharudin.spotifyclone.exoplayer.callback.MusicPlaybackPreparer
import com.baharudin.spotifyclone.exoplayer.callback.MusicPlayerEventListener
import com.baharudin.spotifyclone.exoplayer.callback.MusicPlayerNotificationListener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject


private const val SERVICE_TAG = "MusicService"
@AndroidEntryPoint

class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSource : DefaultDataSourceFactory

    @Inject
    lateinit var exoplayer : SimpleExoPlayer

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var mediaSessonConnector: MediaSessionConnector

    var isForegroundService = false
    private var curPlayingSong : MediaMetadataCompat? = null

    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this,0,it,0)
        }
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
                this,
                mediaSession.sessionToken,
                MusicPlayerNotificationListener(this)
        ){

        }

        val musicPlayerPreparer = MusicPlaybackPreparer(firebaseMusicSource) {
            curPlayingSong = it
            if (it != null) {
                preparePlayer(
                        firebaseMusicSource.songs,
                        it,
                        true
                )
            }
        }

        mediaSessonConnector = MediaSessionConnector(mediaSession)
        mediaSessonConnector.setPlaybackPreparer(musicPlayerPreparer)
        mediaSessonConnector.setPlayer(exoplayer)
        exoplayer.addListener(MusicPlayerEventListener(this))
        musicNotificationManager.showNotification(exoplayer)
    }
    private fun preparePlayer(
            song : List<MediaMetadataCompat>,
            itemToPlay : MediaMetadataCompat,
            playNow : Boolean
    ){
        val curSongIndex = if (curPlayingSong == null) 0 else song.indexOf(itemToPlay)
        exoplayer.prepare(firebaseMusicSource.asMediaSource(dataSource))
        exoplayer.seekTo(curSongIndex,0L)
        exoplayer.playWhenReady = playNow
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onGetRoot(
            clientPackageName:
            String,
            clientUid: Int,
            rootHints: Bundle?
    ): BrowserRoot? {
        TODO()
    }

    override fun onLoadChildren(
            parentId: String,
            result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

    }
}