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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements CategoriesAdapter.OnCategoryListener {
    private static final String TAG = "SearchFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Category> categories = new ArrayList<Category>();
    private CategoriesAdapter categoriesAdapter;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        retrieveCategories();

        View view = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = view.findViewById(R.id.categories);

        categoriesAdapter = new CategoriesAdapter(getContext(), this);
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void retrieveCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                categories.add(new Category(document.getId(), document.getData().get("name").toString(), (String) document.getData().get("icon").toString()));
                                categoriesAdapter.updateCategories(categories);
                                categoriesAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // TODO: Implement search by categories
    @Override
    public void onCategoryClick(int position) {
        Toast.makeText(getContext(), "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
    }
}
