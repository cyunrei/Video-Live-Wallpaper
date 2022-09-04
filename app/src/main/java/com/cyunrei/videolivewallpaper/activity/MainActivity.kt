package com.cyunrei.videolivewallpaper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class MainActivity extends Activity {

    final String PREFERENCES = "com.cyunrei.videolivewallpaper_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getPermission();
        SharedPreferences updateReader = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String version_name = updateReader.getString(getString(R.string.version_name), " ");
        assert version_name != null;
        if (!version_name.equals(getString(R.string.version_name))) updateDialog();
        Button btn1 = findViewById(R.id.choose_video_file);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                chooseVideo();
            }
        });
        Button btn2 = findViewById(R.id.add_video_file_path);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                final EditText edit = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.add_path));
                builder.setView(edit);
                builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        String add_path = edit.getText().toString();
                        copyFile(new File(add_path), new File(getFilesDir().toPath() + "/file.mp4"));
                        VideoLiveWallpaper.setToWallPaper(MainActivity.this);
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        Button btn3 = findViewById(R.id.settings);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void updateDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle(getString(R.string.update_log) + "(" + getString(R.string.version_name) + ")");
        normalDialog.setMessage(getString(R.string.update_log_context));
        normalDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.version_name), getString(R.string.version_name));
        editor.apply();
        normalDialog.show();
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            assert uri != null;
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                copyFile(new File(Objects.requireNonNull(uri.getPath())), new File(getFilesDir() + "/file.mp4"));
                VideoLiveWallpaper.setToWallPaper(this);
                return;
            }
            copyFile(new File(getPath(this, uri)), new File(getFilesDir() + "/file.mp4"));
            VideoLiveWallpaper.setToWallPaper(this);
        }

    }

    public void copyFile(File fromFile, File toFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);
            fileChannelInput = fileInputStream.getChannel();
            fileChannelOutput = fileOutputStream.getChannel();
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileChannelInput != null)
                    fileChannelInput.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (fileChannelOutput != null)
                    fileChannelOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
