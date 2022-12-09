package com.example.periferia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.periferia.Adapters.ProductAdapter;
import com.example.periferia.DB.DBHelper;
import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;

import java.util.ArrayList;

public class Library extends AppCompatActivity {
    private DBHelper dbHelper;
    private ProductService productService;
    private ListView listViewProducts;
    private ArrayList<Producto> productoArrayList;
    private ProductAdapter productAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        productoArrayList = new ArrayList<>();

        try {
            dbHelper = new DBHelper(this);
            /*byte[] img = "".getBytes();

            dbHelper.insertData("Producto1", "Description 1", "1000",img);
            dbHelper.insertData("Producto2", "Description 2", "2000",img);
            dbHelper.insertData("Producto3", "Description 3", "3000",img);
            dbHelper.insertData("Producto4", "Description 4", "4000",img);
            dbHelper.insertData("Producto5", "Description 5", "5000",img);
            dbHelper.insertData("Producto6", "Description 6", "6000",img);
            dbHelper.insertData("Producto7", "Description 7", "7000",img);
            dbHelper.insertData("Producto8", "Description 8", "8000",img);*/

            productService = new ProductService();
            Cursor cursor = dbHelper.getData();
            productoArrayList = productService.cursorToArray(cursor);
            Toast.makeText(this, "Insert OK", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Database", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        /*
        Producto producto1 = new Producto(R.drawable.monitor, "Producto 1", "Description 1", 1000 );
        Producto producto2 = new Producto(R.drawable.mouse, "Producto 2", "Description 2", 2000 );
        Producto producto3 = new Producto(R.drawable.keyboard, "Producto 3", "Description 3", 3000 );
        Producto producto4 = new Producto(R.drawable.monitor, "Producto 4", "Description 4", 4000 );
        Producto producto5 = new Producto(R.drawable.mouse, "Producto 5", "Description 5", 5000 );
        Producto producto6 = new Producto(R.drawable.keyboard, "Producto 6", "Description 6", 6000 );

        productoArrayList.add(producto1);
        productoArrayList.add(producto2);
        productoArrayList.add(producto3);
        productoArrayList.add(producto4);
        productoArrayList.add(producto5);
        productoArrayList.add(producto6);

        */

        listViewProducts = (ListView) findViewById(R.id.listLibrary);

        productAdapter = new ProductAdapter(this, productoArrayList);
        listViewProducts.setAdapter(productAdapter);

        /*
        btnProduct1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra( "title", txtProduct1.getText().toString());
                intent.putExtra( "description", "Description Product 1");
                intent.putExtra("imageCode", R.drawable.monitor);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAdd:
                Intent intent = new Intent(getApplicationContext(), ProdForm.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}