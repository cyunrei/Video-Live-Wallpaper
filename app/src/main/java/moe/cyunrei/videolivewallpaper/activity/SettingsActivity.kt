@file:Suppress("DEPRECATION")

package moe.cyunrei.videolivewallpaper.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.preference.SwitchPreference
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import java.io.IOException

class SettingsActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        (findPreference(this.resources.getString(R.string.preference_play_video_with_sound)) as SwitchPreference).apply {
            this.onPreferenceChangeListener =
                OnPreferenceChangeListener { _, _ ->
                    if (this.isChecked) {
                        this.isChecked = false
                        VideoLiveWallpaperService.muteMusic(this@SettingsActivity)
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Runtime.getRuntime().exec("rm " + filesDir.toPath() + "/unmute")
                            } else {
                                Runtime.getRuntime()
                                    .exec("rm /data/data/moe.cyunrei.videolivewallpaper/files/unmute")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        this.isChecked = true
                        VideoLiveWallpaperService.unmuteMusic(this@SettingsActivity)
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Runtime.getRuntime().exec("touch " + filesDir.toPath() + "/unmute")
                            } else {
                                Runtime.getRuntime()
                                    .exec("touch /data/data/moe.cyunrei.videolivewallpaper/files/unmute")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    false
                }
        }
        (findPreference(this.resources.getString(R.string.preference_hide_icon_from_launcher)) as SwitchPreference).apply {
            val switchPreferenceThis = this
            val packageManager = packageManager
            val componentName = ComponentName(this@SettingsActivity, MainActivity::class.java)
            this.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
                if (switchPreferenceThis.isChecked) {
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                } else {
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
                switchPreferenceThis.isChecked = !switchPreferenceThis.isChecked
                false
            }
        }
    }
}