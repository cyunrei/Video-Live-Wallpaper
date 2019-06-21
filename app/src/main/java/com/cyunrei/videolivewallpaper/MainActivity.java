package com.cyunrei.videolivewallpaper;

import android.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.cyunrei.videolivewallpaper.*;
import java.io.*;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		getPermission();

		
		SharedPreferences updateReader = getSharedPreferences("dialog", MODE_PRIVATE);

		String Updatealue = updateReader.getString("update", "");

		if (!Updatealue.equals("1")) {
			updateDialog();
		}

		Button btn1 = (Button) findViewById(R.id.btn1);

		btn1.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					chooseVideo();
				}
			});


		Button btn2 = (Button) findViewById(R.id.btn2);

		btn2.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					final EditText edit = new EditText(MainActivity.this);

					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

					builder.setTitle(getString(R.string.add_path));
					builder.setView(edit);
					builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								dialogSetup();
								
								String add_path = edit.getText().toString();

								try
								{
									Runtime.getRuntime().exec("cp " + add_path + " ../data/data/com.cyunrei.videolivewallpaper/file.mp4");

								}
								catch (IOException e)
								{}


							}
						});
					builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{

							}
						});
					builder.setCancelable(true);

					AlertDialog dialog = builder.create();

					dialog.setCanceledOnTouchOutside(true);
					dialog.show();

				}
			});


		Button btn3 = (Button) findViewById(R.id.btn3);

		btn3.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

					builder.setTitle(getString(R.string.about));
					builder.setMessage((getString(R.string.context)));
					builder.setPositiveButton(getString(R.string.contact), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								Intent data = new Intent(Intent.ACTION_SENDTO); 
								data.setData(Uri.parse("mailto:cyunrei@gmail.com")); 
								startActivity(data);
							}
						});


					builder.setNeutralButton(getString(R.string.update), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								Intent uri = new Intent();
								uri.setAction(Intent.ACTION_VIEW);
								uri.setData(Uri.parse("https://github.com/Cyunrei/Video-Live-Wallpaper/releases/"));
								startActivity(uri);
								
							}

						});


					builder.setCancelable(true);

					AlertDialog dialog = builder.create();

					dialog.setCanceledOnTouchOutside(true);
					dialog.show();

				}
			});


    }

	public void startLiveWallpaperPreView(String packageName, String classFullName) {
        ComponentName componentName = new ComponentName(packageName, classFullName);
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT < 16) {
            intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        } else {
            intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", componentName);
        }
        startActivityForResult(intent, 0);
    }
	
	
	private void chooseVideo()
	{
		Intent intent = new Intent();
		intent.setType("video/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);
	}

	private void dialogSetup()
	{

		VideoLiveWallpaper.setToWallPaper(this);
	}

	private void getPermission()
	{

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
	}

	private void setup()
	{
		
		VideoLiveWallpaper.setToWallPaper(this);
		
		try
		{
			Runtime.getRuntime().exec("cp " + path + " ../data/data/com.cyunrei.videolivewallpaper/file.mp4");
		}
		catch (IOException e)
		{}

	}

	private void updateDialog()
	{

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);

		normalDialog.setTitle(getString(R.string.updatelog));
		normalDialog.setMessage(getString(R.string.updatelogcontext));
		normalDialog.setPositiveButton(getString(R.string.iknow), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{

				}
			});

		SharedPreferences.Editor editor = getSharedPreferences("dialog", MODE_PRIVATE).edit();
		editor.putString("update", "1");
		editor.commit();

		normalDialog.show();
	}

    String path;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        if (resultCode == Activity.RESULT_OK)
		{
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme()))
			{

                path = uri.getPath();

				setup();

                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
			{

                path = getPath(this, uri);

                setup();
            }
			else
			{

                path = getRealPathFromURI(uri);

                setup();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri)
	{
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst())
		{
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri)
	{

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
		{

            if (isExternalStorageDocument(uri))
			{
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
				{
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }

            else if (isDownloadsDocument(uri))
			{

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
					Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            else if (isMediaDocument(uri))
			{
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
				{
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
				else if ("video".equals(type))
				{
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
				else if ("audio".equals(type))
				{
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        else if ("content".equalsIgnoreCase(uri.getScheme()))
		{
            return getDataColumn(context, uri, null, null);
        }

        else if ("file".equalsIgnoreCase(uri.getScheme()))
		{
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs)
	{

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
		{
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
														null);
            if (cursor != null && cursor.moveToFirst())
			{
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
		finally
		{
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri)
	{
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri)
	{
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri)
	{
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}

