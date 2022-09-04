package moe.cyunrei.videolivewallpaper.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import moe.cyunrei.videolivewallpaper.utils.DocumentUtils.getPath


class MainActivity : Activity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        permissionCheck
        findViewById<Button?>(R.id.choose_video_file).apply {
            setOnClickListener { chooseVideo() }
        }

        findViewById<Button?>(R.id.add_video_file_path).apply {
            setOnClickListener {
                val edit = EditText(this@MainActivity)
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(getString(R.string.add_path))
                    setView(edit)
                    setPositiveButton(
                        getString(R.string.apply)
                    ) { _, _ ->
                        val videoFilePath: String = edit.text.toString()
                        this@MainActivity.openFileOutput(
                            "video_live_wallpaper_file_path",
                            Context.MODE_PRIVATE
                        ).use {
                            it.write(videoFilePath.toByteArray())
                        }
                        VideoLiveWallpaperService.setToWallPaper(this@MainActivity)
                    }
                    setNegativeButton(
                        getString(R.string.cancel)
                    ) { _, _ -> }
                    setCancelable(true)
                    create().apply {
                        setCanceledOnTouchOutside(true)
                        show()
                    }
                }
            }
        }

        findViewById<Button?>(R.id.settings).apply {
            setOnClickListener {
                Intent(this@MainActivity, SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private val permissionCheck: Unit
        get() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }

    private fun chooseVideo() {
        Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }.also { startActivityForResult(it, 1) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == RESULT_OK) {
            val uri: Uri = data.data!!
            this.openFileOutput(
                "video_live_wallpaper_file_path",
                Context.MODE_PRIVATE
            ).use {
                it.write(getPath(this, uri)!!.toByteArray())
            }
            VideoLiveWallpaperService.setToWallPaper(this)
        }
    }
}