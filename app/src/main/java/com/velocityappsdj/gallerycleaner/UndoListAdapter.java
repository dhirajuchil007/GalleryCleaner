package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.velocityappsdj.gallerycleaner.db.Data;

import java.util.List;

public class UndoListAdapter extends RecyclerView.Adapter<UndoListAdapter.ImageViewHolder> {
    public interface OnImageClicked {
        void OnTap(Data imageData);
    }

    private List<Data> imageDataList;
    private OnImageClicked onImageClicked;
    private Context context;

    public UndoListAdapter(List<Data> imageDataList, Context context, OnImageClicked onImageClicked) {
        this.imageDataList = imageDataList;
        this.onImageClicked = onImageClicked;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UndoListAdapter.ImageViewHolder holder, int position) {
        Glide.with(context).load(imageDataList.get(position).getImagePath()).centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //todo add failed state
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> onImageClicked.OnTap(imageDataList.get(position)));

    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
        }
    }
}
