package com.uqac.sellet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uqac.sellet.entities.Product;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private OnProductListener onProductListener;

    private List<Product> products = new ArrayList<>();

    private Context context;

    public ProductsAdapter(Context ctx, OnProductListener onProductListener) {
        this.context = ctx;
        this.onProductListener = onProductListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_row, parent, false);
        return new ViewHolder(view, onProductListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference storageReference = storage.getReference();

        holder.productName.setText(products.get(position).name);

        if(!products.get(position).picturesLinks.isEmpty()){
            Glide.with(context)
                    .load(storageReference.child(products.get(position).picturesLinks.get(0)))
                    .into(holder.picture);
        }
    }

    @Override
    public int getItemCount() {
        if(!products.isEmpty()) return products.size();
        return 0;
    }

    public void updateProducts(List<Product> products) {
        this.products = products;
//        Log.d("ADAPTER", products.toString());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnProductListener onProductListener;

        TextView productName;
        ImageView picture;

        public ViewHolder(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            picture = itemView.findViewById(R.id.product_picture);
            this.onProductListener = onProductListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProductListener.onProductClick(getAdapterPosition());
        }
    }

    public interface OnProductListener {
        void onProductClick(int position);
    }
}
