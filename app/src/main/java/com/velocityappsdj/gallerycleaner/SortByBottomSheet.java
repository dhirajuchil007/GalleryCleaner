package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SortByBottomSheet extends BottomSheetDialogFragment {
    interface IOnSortSelected {
        void OnSelected(String pref);
    }

    private IOnSortSelected iOnSortSelected;
    private String pref;

    public SortByBottomSheet(IOnSortSelected iOnSortSelected, String pref) {
        this.iOnSortSelected = iOnSortSelected;
        this.pref = pref;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.sort_by_bottomsheet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.listSortPref);
        List<String> prefs = new ArrayList<>(ApplicationConstants.sort_order.keySet());
        if (pref.isEmpty())
            pref = "date";
        String preftext = ApplicationConstants.sort_order_pref_to_text.get(pref);

        for (String s : prefs) {
            if (s.equalsIgnoreCase(preftext))
                s += "  (Current selection)";
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, prefs);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iOnSortSelected.OnSelected(prefs.get(position));
                dismiss();
            }
        });
    }
}
