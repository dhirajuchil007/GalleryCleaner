package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.velocityappsdj.gallerycleaner.adapter.FolderListAdapter;
import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.models.FolderItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FolderListBottomSheet extends BottomSheetDialogFragment {
    public interface OnFolderSelected {
        void onSelected(int position, FolderItemModel folderItemModel);
    }

    private static final String TAG = "FolderListBottomSheet";
    private OnFolderSelected onFolderSelected;
    private Context mContext;
    private HashMap<String, FolderItemModel> foldersMap;
    private List<Data> allImages;
    private RecyclerView foldersList;
    private FolderListAdapter folderListAdapter;

    public FolderListBottomSheet(OnFolderSelected onFolderSelected,
                                 Context mContext, HashMap<String, FolderItemModel> foldersMap, List<Data> allImages) {
        this.onFolderSelected = onFolderSelected;
        this.mContext = mContext;
        this.foldersMap = foldersMap;
        this.allImages = allImages;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folder_list_bottomsheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foldersList = view.findViewById(R.id.recyclerFolderName);
        List<FolderItemModel> folderItemModels = new ArrayList<>(foldersMap.values());
        Collections.sort(folderItemModels, (a, b) -> a.getName().compareTo(b.getName()));
        folderItemModels.add(0, new FolderItemModel().setName(ApplicationConstants.ALL).setImagesData(allImages));
        Log.d(TAG, "onViewCreated: foldersmap" + foldersMap.size());


        folderListAdapter = new FolderListAdapter(new FolderListAdapter.OnItemSelected() {
            @Override
            public void onSelected(int position, FolderItemModel folderItemModel) {
                onFolderSelected.onSelected(position, folderItemModel);
                dismiss();
            }
        }, folderItemModels, mContext);
        foldersList.setLayoutManager(new LinearLayoutManager(mContext));
        foldersList.setAdapter(folderListAdapter);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume() called");
        super.onResume();
        if (folderListAdapter != null) {
            folderListAdapter.notifyDataSetChanged();
        }
    }
}
