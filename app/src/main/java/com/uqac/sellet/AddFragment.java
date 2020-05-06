package com.uqac.sellet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.uqac.sellet.entities.PictureLoader;
import com.uqac.sellet.entities.Product;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddFragment";
    public static final int PICK_PRODUCT_PICTURE = 1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Category> categories = new ArrayList<Category>();

//    private int[] imageList = new int[] {
//            R.drawable.logo, R.drawable.logo
//    };

    private PictureLoader pl = new PictureLoader();
    private CarouselView carouselView;

    Spinner statesSpinner,  categoriesSpinner;
    ArrayAdapter adapter1, adapter2;

    ArrayList<String> names = new ArrayList<String>();

    Product p;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_fragment, container, false);

        p = new Product(getContext());

        retrieveCategories();

        carouselView = view.findViewById(R.id.carousel);
        carouselView.setPageCount(0);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageURI(p.picturesArray.get(position));
            }
        });

        view.findViewById(R.id.addImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture(v);
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

        view.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishProduct(v);
            }
        });

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
        // TODO : CHANGE PRODUCT CATEGORY AND CONDITION
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
                                categories.add(new Category(getContext(), document.getId(), document.getData().get("name").toString(), (String) document.getData().get("icon").toString()));
                                getCategoryNames();
                                adapter1.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addPicture(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, PICK_PRODUCT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_PRODUCT_PICTURE) {
            Log.d(TAG, data.getData().toString());
            p.picturesArray.add(data.getData());
            refreshCarouselView();
        }
    }

    private void refreshCarouselView(){
        carouselView.setPageCount(p.picturesArray.size());
    }

    private void publishProduct(View v){
        p.name = ((EditText)getView().findViewById(R.id.title_edit)).getText().toString();
        p.desc = ((EditText)getView().findViewById(R.id.desc_edit)).getText().toString();
        p.price = Double.parseDouble(((EditText)getView().findViewById(R.id.price_edit)).getText().toString());
        p.publish();
    }
}
