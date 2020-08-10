package com.cyunrei.videolivewallpaper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class BootBroadCast extends BroadcastReceiver
{
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent service = new Intent(context, VideoLiveWallpaper.class);
		context.startService(service);
	}

}

