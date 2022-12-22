package com.example.periferia;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.periferia.Adapters.ProductAdapter;
import com.example.periferia.DB.DBBFireBase;
import com.example.periferia.DB.DBHelper;
import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;

import java.util.ArrayList;

public class Library extends AppCompatActivity {
    private DBHelper dbHelper;
    private DBBFireBase dbbFireBase;
    private ProductService productService;
    private ListView listLibrary;
    private ArrayList<Producto> productoArrayList;
    private ProductAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        productoArrayList = new ArrayList<>();

        try {
            //dbHelper = new DBHelper(this);
            dbbFireBase = new DBBFireBase();

            productService = new ProductService();
            Cursor cursor = dbHelper.getData();
            productoArrayList = productService.cursorToArray(cursor);
            /*if(productoArrayList.size() == 0){
                dbFirebase.syncData(dbHelper);
            }*/
            //Toast.makeText(this, "Insert OK", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Database", e.toString());
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


        listLibrary = (ListView) findViewById(R.id.listLibrary);

        productAdapter = new ProductAdapter(this, productoArrayList);
        listLibrary.setAdapter(productAdapter);

        dbbFireBase.getData(productAdapter, productoArrayList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.itemAdd:
                intent = new Intent(getApplicationContext(), ProdForm.class);
                startActivity(intent);
                return true;
            case R.id.actioMap:
                intent = new Intent(getApplicationContext(), Maps.class);
                ArrayList<String> latitudes = new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();

                for(int i=0; i<productoArrayList.size(); i++){
                    latitudes.add(String.valueOf(productoArrayList.get(i).getLatitud()));
                    longitudes.add(String.valueOf(productoArrayList.get(i).getLongitud()));
                }
                intent.putStringArrayListExtra("latitudes", latitudes);
                intent.putStringArrayListExtra("longitudes", longitudes);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}