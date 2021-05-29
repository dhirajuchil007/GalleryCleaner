package com.velocityappsdj.gallerycleaner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.velocityappsdj.gallerycleaner.R;
import com.velocityappsdj.gallerycleaner.UserDataSingleTon;
import com.velocityappsdj.gallerycleaner.models.FolderItemModel;

import java.util.List;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {
    public interface OnItemSelected {
        void onSelected(int position, FolderItemModel folderItemModel);
    }

    private List<FolderItemModel> folders;
    private OnItemSelected onItemSelected;
    private Context mContext;

    public FolderListAdapter(OnItemSelected onItemSelected, List<FolderItemModel> folders, Context mContext) {
        this.onItemSelected = onItemSelected;
        this.folders = folders;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderListAdapter.FolderViewHolder holder, int position) {
        FolderItemModel folderItemModel = folders.get(position);
        holder.txtFolderName.setText(folderItemModel.getName());
        holder.txtItemsCount.setText(String.valueOf(folderItemModel.getImagesData().size()));
        if (UserDataSingleTon.getInstance().selectedFolder.equalsIgnoreCase(folderItemModel.getName())) {
            holder.imgEnabled.setVisibility(View.VISIBLE);
        } else {
            holder.imgEnabled.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> onItemSelected.onSelected(position, folderItemModel));

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private TextView txtFolderName, txtItemsCount;
        private ImageView imgEnabled;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFolderName = itemView.findViewById(R.id.txtFolderName);
            txtItemsCount = itemView.findViewById(R.id.txtItemsCount);
            imgEnabled = itemView.findViewById(R.id.imgEnabled);
        }
    }
}
