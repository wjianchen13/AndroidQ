package com.cold.androidq;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;

    private Button btnBrowseAlbum;
    private Button btnAddImageToAlbum;
    private Button btnDownloadFile;
    private Button btnPickFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        btnBrowseAlbum = findViewById(R.id.browseAlbum);
        btnAddImageToAlbum = findViewById(R.id.addImageToAlbum);
        btnDownloadFile = findViewById(R.id.downloadFile);
        btnPickFile = findViewById(R.id.pickFile);

        List<String> permissionsToRequire = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequire.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] str1=new String[permissionsToRequire.size()];
        for(int i=0;i<permissionsToRequire.size();i++) {
            str1[i]=permissionsToRequire.get(i);
        }
        if(str1.length > 0)
            ActivityCompat.requestPermissions(this, str1, 0);
        btnBrowseAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileActivity.this, BrowseAlbumActivity.class);
                startActivity(intent);
            }
        });
        btnAddImageToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
                String displayName = System.currentTimeMillis() + ".jpg";
                String mimeType = "image/jpeg";
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                addBitmapToAlbum(bitmap, displayName, mimeType, compressFormat);
            }
        });

        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileUrl = "http://guolin.tech/android.txt";
                String fileName = "android.txt";
                downloadFile(fileUrl, fileName);
            }
        });
        btnPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFileAndCopyUriToExternalFilesDir();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You must allow all the permissions.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void addBitmapToAlbum(Bitmap bitmap, String displayName, String mimeType, Bitmap.CompressFormat compressFormat) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            values.put(MediaStore.MediaColumns.DATA, Environment.getExternalStorageDirectory().getPath() +  "/" + Environment.DIRECTORY_DCIM + displayName);
        }
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(compressFormat, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(this, "Add bitmap to album succeeded.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(final String fileUrl, final String fileName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this, "You must use device running Android 10 or higher", Toast.LENGTH_SHORT).show();
            return ;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(fileUrl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                        if (uri != null) {
                            OutputStream outputStream = getContentResolver().openOutputStream(uri);
                            if (outputStream != null) {
                                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                                byte[] buffer = new byte[1024];
                                int bytes = bis.read(buffer);
                                while (bytes >= 0) {
                                    bos.write(buffer, 0, bytes);
                                    bos.flush();
                                    bytes = bis.read(buffer);
                                }
                                bos.close();
                                FileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FileActivity.this, fileName + " is in Download directory now.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        bis.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void pickFileAndCopyUriToExternalFilesDir() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String fileName = getFileNameByUri(uri);
                    copyUriToExternalFilesDir(uri, fileName);
                }
            }
        }
    }

    private String getFileNameByUri(Uri uri) {
        String fileName = String.valueOf(System.currentTimeMillis());
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            cursor.close();
        }
        return fileName;
    }

    private void  copyUriToExternalFilesDir(final Uri uri, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    File tempDir = getExternalFilesDir("temp");
                    if (inputStream != null && tempDir != null) {
                        File file = new File("$tempDir/$fileName");
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        byte[] byteArray = new byte[1024];
                        int bytes = bis.read(byteArray);
                        while (bytes > 0) {
                            bos.write(byteArray, 0, bytes);
                            bos.flush();
                            bytes = bis.read(byteArray);
                        }
                        bos.close();
                        fos.close();
                        FileActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FileActivity.this, "Copy file into $tempDir succeeded.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


}
