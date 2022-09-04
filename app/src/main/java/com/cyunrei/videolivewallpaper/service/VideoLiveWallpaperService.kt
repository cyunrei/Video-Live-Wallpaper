package com.cyunrei.videolivewallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;


public class VideoLiveWallpaper extends WallpaperService {

    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.cyunrei.livewallpaper";
    public static final String KEY_ACTION = "music";
    public static final boolean ACTION_MUSIC_UNMUTE = false;
    public static final boolean ACTION_MUSIC_MUTE = true;

    public static void muteMusic(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_MUSIC_MUTE);
        context.sendBroadcast(intent);
    }

    public static void unmuteMusic(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_MUSIC_UNMUTE);
        context.sendBroadcast(intent);
    }

    class VideoEngine extends Engine {
        private MediaPlayer mediaPlayer;
        private BroadcastReceiver broadcastReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter intentFilter = new IntentFilter(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean action = intent.getBooleanExtra(KEY_ACTION, false);
                    if (action) {
                        mediaPlayer.setVolume(0, 0);
                    } else {
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                }
            }, intentFilter);
        }

        @SuppressLint("SdCardPath")
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(holder.getSurface());
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getFilesDir() + "/file.mp4");
                mediaPlayer.setLooping(true);
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                mediaPlayer.prepare();
                mediaPlayer.start();
                File file = new File(getFilesDir() + "/unmute");
                if (file.exists()) mediaPlayer.setVolume(1.0f, 1.0f);
                else mediaPlayer.setVolume(0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mediaPlayer.start();
            } else {
                mediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mediaPlayer != null) mediaPlayer.release();
            unregisterReceiver(broadcastReceiver);
        }
    }

    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, VideoLiveWallpaper.class));
        context.startActivity(intent);
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
