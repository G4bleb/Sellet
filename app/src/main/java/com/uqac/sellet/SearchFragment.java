package com.uqac.sellet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uqac.sellet.entities.OnReadyListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements CategoriesAdapter.OnCategoryListener {
    private static final String TAG = "SearchFragment";

    private ArrayList<Category> categories = new ArrayList<Category>();
    private CategoriesAdapter categoriesAdapter;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = view.findViewById(R.id.categories);

        retrieveCategories();

        categoriesAdapter = new CategoriesAdapter(getContext(), this);
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void retrieveCategories() {
        new Category(getContext()).getAllCategories().setOnReadyListener(new OnReadyListener<ArrayList<Category>>() {
            @Override
            public void onReady(ArrayList<Category> allCts) {
                categories = allCts;
                Log.d(TAG, "onReady: "+allCts.toString());
                categoriesAdapter.updateCategories(allCts);
                categoriesAdapter.notifyDataSetChanged();
            }
        });
    }

    // TODO: Implement search by categories
    @Override
    public void onCategoryClick(int position) {
        Toast.makeText(getContext(), "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
    }
}
