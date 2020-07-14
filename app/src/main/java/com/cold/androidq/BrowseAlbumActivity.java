package com.cold.androidq;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BrowseAlbumActivity extends AppCompatActivity {

    List<Uri> imageList = new ArrayList<>();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_album);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int columns = 3;
                int imageSize = recyclerView.getWidth() / columns;
                AlbumAdapter adapter = new AlbumAdapter(BrowseAlbumActivity.this, imageList, imageSize);
                GridLayoutManager layoutManager = new GridLayoutManager(BrowseAlbumActivity.this, columns);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                loadImages(adapter);
                return false;
            }
            //            @Override
//            boolean onPreDraw() {

//            }
        });
    }

    private void loadImages(final AlbumAdapter adapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageList.add(uri);
                    }
                    cursor.close();
                }
                BrowseAlbumActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }



}
