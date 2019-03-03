package com.cyunrei.videolivewallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class BootBroadCast extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{

		Intent service = new Intent(context, VideoLiveWallpaper.class);
		context.startService(service);

	}

}

