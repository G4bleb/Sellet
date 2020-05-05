package com.uqac.sellet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class AddFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddFragment";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Category> categories = new ArrayList<Category>();

    private int[] imageList = new int[] {
            R.drawable.logo, R.drawable.logo
    };
    Spinner statesSpinner,  categoriesSpinner;
    ArrayAdapter adapter1, adapter2;

    ArrayList<String> names = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_fragment, container, false);

        retrieveCategories();

        CarouselView carouselView = view.findViewById(R.id.carousel);
        carouselView.setPageCount(2);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(imageList[position]);
            }
        });

        categoriesSpinner = view.findViewById(R.id.categories);
        statesSpinner = view.findViewById(R.id.states);

        adapter1 = new ArrayAdapter<String>(getContext() , R.layout.spinner_layout, names);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        categoriesSpinner.setAdapter(adapter1);
        categoriesSpinner.setOnItemSelectedListener(this);

        adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.item_states, R.layout.spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        statesSpinner.setAdapter(adapter2);
        statesSpinner.setOnItemSelectedListener(this);

        return view;
    }

    public void getCategoryNames() {
        names.clear();

        for (int i = 0 ; i < categories.size() ; i++) {
            names.add(categories.get(i).name);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Notify the selected item text
        Toast.makeText(getContext(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                                getCategoryNames();
                                adapter1.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
