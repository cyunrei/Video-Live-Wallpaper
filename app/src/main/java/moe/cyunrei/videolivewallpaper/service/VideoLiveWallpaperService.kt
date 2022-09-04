package moe.cyunrei.videolivewallpaper.service

import android.app.WallpaperManager
import android.content.*
import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.io.File
import java.io.IOException

class VideoLiveWallpaperService : WallpaperService() {
    internal inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null
        private var broadcastReceiver: BroadcastReceiver? = null
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.getBooleanExtra(KEY_ACTION, false)
                    if (action) {
                        mediaPlayer!!.setVolume(0f, 0f)
                    } else {
                        mediaPlayer!!.setVolume(1.0f, 1.0f)
                    }
                }
            }.also { broadcastReceiver = it }, intentFilter)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setSurface(holder.surface)
            try {
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource("$filesDir/file.mp4")
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                val file = File("$filesDir/unmute")
                if (file.exists()) mediaPlayer!!.setVolume(1.0f, 1.0f) else mediaPlayer!!.setVolume(
                    0f,
                    0f
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                mediaPlayer!!.start()
            } else {
                mediaPlayer!!.pause()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        override fun onDestroy() {
            super.onDestroy()
            if (mediaPlayer != null) mediaPlayer!!.release()
            unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    companion object {
        const val VIDEO_PARAMS_CONTROL_ACTION = "moe.cyunrei.livewallpaper"
        const val KEY_ACTION = "music"
        private const val ACTION_MUSIC_UNMUTE = false
        private const val ACTION_MUSIC_MUTE = true
        fun muteMusic(context: Context) {
            val intent = Intent(VIDEO_PARAMS_CONTROL_ACTION)
            intent.putExtra(KEY_ACTION, ACTION_MUSIC_MUTE)
            context.sendBroadcast(intent)
        }

        fun unmuteMusic(context: Context) {
            val intent = Intent(VIDEO_PARAMS_CONTROL_ACTION)
            intent.putExtra(KEY_ACTION, ACTION_MUSIC_UNMUTE)
            context.sendBroadcast(intent)
        }

        fun setToWallPaper(context: Context) {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, VideoLiveWallpaperService::class.java)
            )
            context.startActivity(intent)
            try {
                WallpaperManager.getInstance(context).clear()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}