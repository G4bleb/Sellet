package com.uqac.sellet;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Category {
    private static final String TAG = "Product";

    String id;
    String name;
    String iconLink;

    public Category(String id, String name, String iconLink){
        this.id = id;
        this.name = name;
        this.iconLink = iconLink;
    }

    Map<String, Object> toMap(){
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("icon", iconLink);
        return category;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+id+", "+name+", "+iconLink+"}";
    }

}
