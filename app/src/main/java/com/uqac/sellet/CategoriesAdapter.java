package com.uqac.sellet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private OnCategoryListener onCategoryListener;

    private List<Category> categories = new ArrayList<Category>();

    private Context context;

    public CategoriesAdapter(Context ctx, OnCategoryListener onCategoryListener) {
        this.context = ctx;
        this.onCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_row, parent, false);
        return new ViewHolder(view, onCategoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference storageReference = storage.getReference();

        holder.category.setText(categories.get(position).name);

        Glide.with(context)
                .load(storageReference.child(categories.get(position).iconLink))
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        if(!categories.isEmpty()) return categories.size();

        return 0;
    }

    public void updateCategories(List<Category> categories) {
        this.categories = categories;
        Log.d("ADAPTER", categories.toString());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnCategoryListener onCategoryListener;

        TextView category;
        ImageView icon, chevron;

        public ViewHolder(@NonNull View itemView, OnCategoryListener onCategoryListener) {
            super(itemView);
            category = itemView.findViewById(R.id.category_name);
            icon = itemView.findViewById(R.id.category_icon);
            chevron = itemView.findViewById(R.id.chevron);
            this.onCategoryListener = onCategoryListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
    }

    public interface OnCategoryListener {
        void onCategoryClick(int position);
    }
}
