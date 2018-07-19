package com.cyunrei.videolivewallpaper;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends PreferenceActivity
{
	PreferenceManager pathManager;
	EditTextPreference pathPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_activity);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

		try
		{
			Runtime.getRuntime().exec("mkdir -p /storage/emulated/0/Android/data/com.cyunrei.videolivewallpaper/files/");
		}
		catch (IOException e)
		{}

    }

	public boolean onCreateOptionsMenu(Menu menu)
	{  
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, getString(R.string.apply));
		return true;
	}

	@Override  
    public boolean onOptionsItemSelected(MenuItem item)
	{  
        switch (item.getItemId())
		{  
			case Menu.FIRST + 1:  
				VideoLiveWallpaper.setToWallPaper(this);
				path();
				break;  
		}return true;
	}
	private void path()
	{

		pathManager = getPreferenceManager();
		pathPreference = (EditTextPreference) pathManager.findPreference("path");
		String path = pathPreference.getText();

		try
		{
			Runtime.getRuntime().exec("cp " + path + " ../storage/emulated/0/Android/data/com.cyunrei.videolivewallpaper/files/file.mp4");
		}
		catch (IOException e)
		{}
	}
}
