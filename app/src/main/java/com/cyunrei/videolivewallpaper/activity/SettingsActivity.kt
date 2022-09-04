package com.cyunrei.videolivewallpaper;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import java.io.IOException;

public class Settings extends PreferenceActivity {
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        final SwitchPreference switchPreference1 = (SwitchPreference) findPreference(this.getResources().getString(R.string.preference_play_video_with_sound));
        final SwitchPreference switchPreference2 = (SwitchPreference) findPreference(this.getResources().getString(R.string.preference_hide_icon_from_launcher));
        switchPreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (switchPreference1.isChecked()) {
                    switchPreference1.setChecked(false);
                    VideoLiveWallpaper.muteMusic(Settings.this);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("rm " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("rm /data/data/com.cyunrei.videolivewallpaper/files/unmute");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    switchPreference1.setChecked(true);
                    VideoLiveWallpaper.unmuteMusic(Settings.this);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("touch " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("touch /data/data/com.cyunrei.videolivewallpaper/files/unmute");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        switchPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName(Settings.this, com.cyunrei.videolivewallpaper.MainActivity.class);

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (switchPreference2.isChecked()) {
                    packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    switchPreference2.setChecked(false);
                } else {
                    packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    switchPreference2.setChecked(true);
                }
                return false;
            }
        });
    }
}
