package com.example.periferia.DB;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.periferia.Adapters.ProductAdapter;
import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBBFireBase {
    private FirebaseFirestore db;
    private ProductService productService;

    public DBBFireBase(){
        this.db = FirebaseFirestore.getInstance();
        this.productService = new ProductService();
        //this.db.terminate();
    }
    public void insertData(Producto prod){
        // Create a new user with a first and last name
        Map<String, Object> producto = new HashMap<>();
        producto.put("id", prod.getId());
        producto.put("name", prod.getName());
        producto.put("description", prod.getDescription());
        producto.put("price", prod.getPrice());
        producto.put("image", prod.getImage());
        producto.put("deleted", prod.isDeleted());
        producto.put("createdAt", prod.getCreatedAt());
        producto.put("updatedAt", prod.getUpdatedAt());
        producto.put("latitud", prod.getLatitud());
        producto.put("longitud", prod.getLongitud());

// Add a new document with a generated ID
        db.collection("products")
            .add(producto)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
        //db.terminate();
    }
    public void getData(ProductAdapter productAdapter, ArrayList<Producto> list){
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Producto producto = null;
                                if(!Boolean.valueOf(document.getData().get("deleted").toString())){
                                    producto = new Producto(
                                            document.getData().get("id").toString(),
                                            document.getData().get("name").toString(),
                                            document.getData().get("description").toString(),
                                            Integer.parseInt(document.getData().get("price").toString()),
                                            document.getData().get("image").toString(),
                                            Boolean.valueOf(document.getData().get("deleted").toString()),
                                            productService.stringToDate(document.getData().get("createdAt").toString()),
                                            productService.stringToDate(document.getData().get("updatedAt").toString()),
                                            Double.parseDouble(document.getData().get("latitud").toString()),
                                            Double.parseDouble(document.getData().get("longitud").toString())
                                    );
                                    list.add(producto);
                                }
                            }
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void updateData(Producto producto){
        db.collection("products").whereEqualTo("id", producto.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                documentSnapshot.getReference().update(
                                        "name", producto.getName(),
                                        "description", producto.getDescription(),
                                        "price", producto.getPrice(),
                                        "image", producto.getImage(),
                                        "latitud", producto.getLatitud(),
                                        "longitud", producto.getLongitud()
                                );
                            }
                        }
                    }
                });
    }
    public void deleteData(String id){
        db.collection("products").whereEqualTo("id",id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                documentSnapshot.getReference().delete();
                            }
                        }
                    }
                });
    }

}
