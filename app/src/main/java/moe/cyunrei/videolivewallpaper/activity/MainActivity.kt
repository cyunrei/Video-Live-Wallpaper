package moe.cyunrei.videolivewallpaper.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import moe.cyunrei.videolivewallpaper.utils.FileUtils.copyFile
import java.io.File
import java.util.*

class MainActivity : Activity() {

    companion object {
        private const val PREFERENCES = "moe.cyunrei.videolivewallpaper_preferences"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        permissionCheck
        val updateReader: SharedPreferences =
            getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val versionName: String = updateReader.getString(getString(R.string.version_name), " ")!!
        if (versionName != getString(R.string.version_name)) updateDialog()

        findViewById<Button?>(R.id.choose_video_file).apply {
            setOnClickListener { chooseVideo() }
        }

        findViewById<Button?>(R.id.add_video_file_path).apply {
            setOnClickListener {
                val edit = EditText(this@MainActivity)
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle(getString(R.string.add_path))
                builder.setView(edit)
                builder.setPositiveButton(
                    getString(R.string.apply)
                ) { _, _ ->
                    val addPath: String = edit.text.toString()
                    copyFile(
                        File(addPath),
                        File(filesDir.toPath().toString() + "/file.mp4")
                    )
                    VideoLiveWallpaperService.setToWallPaper(this@MainActivity)
                }
                builder.setNegativeButton(
                    getString(R.string.cancel)
                ) { _, _ -> }
                builder.setCancelable(true)
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }
        }

        findViewById<Button?>(R.id.settings).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private val permissionCheck: Unit
        get() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
            }
        }

    private fun updateDialog() {
        val normalDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        normalDialog.setTitle(getString(R.string.update_log) + "(" + getString(R.string.version_name) + ")")
        normalDialog.setMessage(getString(R.string.update_log_context))
        normalDialog.setPositiveButton(
            getString(R.string.ok)
        ) { _, _ -> }
        val editor: SharedPreferences.Editor =
            getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putString(getString(R.string.version_name), getString(R.string.version_name))
        editor.apply()
        normalDialog.show()
    }

    private fun chooseVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == RESULT_OK) {
            val uri: Uri = data.data!!
            if ("file".equals(uri.scheme, ignoreCase = true)) {
                copyFile(
                    File(Objects.requireNonNull<String>(uri.path)),
                    File("$filesDir/file.mp4")
                )
                VideoLiveWallpaperService.setToWallPaper(this)
                return
            }
            copyFile(getPath(this, uri)?.let { File(it) }, File("$filesDir/file.mp4"))
            VideoLiveWallpaperService.setToWallPaper(this)
        }
    }
}