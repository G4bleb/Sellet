package com.uqac.sellet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uqac.sellet.entities.OnReadyListener;
import com.uqac.sellet.entities.Product;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment implements ProductsAdapter.OnProductListener {
    private static final String TAG = "HomeFragment";

    private ArrayList<Product> products;

    private RecyclerView recyclerView;
    private ProductsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = view.findViewById(R.id.products_recycler);

        getProducts();

        mAdapter = new ProductsAdapter(getContext(), this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void getProducts(){
        new Product(getContext()).getAllProducts().setOnReadyListener(new OnReadyListener<ArrayList<Product>>() {
            @Override
            public void onReady(ArrayList<Product> allpr) {
                products = allpr;
                Log.d(TAG, "onReady: "+allpr.toString());
                mAdapter.updateProducts(allpr);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onProductClick(int position) {
        Intent myIntent = new Intent(getContext(), ProductDetailsActivity.class);
        myIntent.putExtra("productId", products.get(position).id); //Optional parameters
        startActivity(myIntent);
    }
}
