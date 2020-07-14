package com.cold.androidq;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>{

    private Context context;
    List<Uri> imageList;
    int imageSize;

    public AlbumAdapter(Context contex, List<Uri> list, int size) {
        this.context = contex;
        this.imageList = list;
        this.imageSize = size;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_image_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = imageSize;
        params.height = imageSize;
        holder.imageView.setLayoutParams(params);
        Uri uri = imageList.get(position);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.album_loading_bg).override(imageSize, imageSize);
        Glide.with(context).load(uri).apply(options).into(holder.imageView);
    }

}
