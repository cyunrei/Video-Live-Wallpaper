package moe.cyunrei.videolivewallpaper.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

object FileUtils {
    fun copyFile(src: File?, dst: File?) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var fileChannelInput: FileChannel? = null
        var fileChannelOutput: FileChannel? = null
        try {
            fileInputStream = FileInputStream(src)
            fileOutputStream = FileOutputStream(dst)
            fileChannelInput = fileInputStream.channel
            fileChannelOutput = fileOutputStream.channel
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
                fileChannelInput?.close()
                fileOutputStream?.close()
                fileChannelOutput?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}