package com.example.periferia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;

public class Products extends AppCompatActivity {
    //private DBHelper dbHelper;
    private ProductService productService;
    private Button btnProducts_back;
    private TextView txtProducts_Name, txtProducts_Descrip, txtProducts_Price;
    private ImageView imgProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        btnProducts_back = (Button) findViewById(R.id.btnProducts_back);
        txtProducts_Name = (TextView) findViewById(R.id.txtProducts_Name);
        txtProducts_Price = (TextView) findViewById(R.id.txtProducts_Price);
        txtProducts_Descrip = (TextView) findViewById(R.id.txtProducts_Descrip);
        imgProducts = (ImageView) findViewById(R.id.imgProducts);
        //dbHelper = new DBHelper(this);
        productService = new ProductService();

        Intent intentIn = getIntent();
        Producto producto = new Producto(
                Integer.parseInt(intentIn.getStringExtra("price"))
        );




        txtProducts_Name.setText(intentIn.getStringExtra("name"));
        txtProducts_Descrip.setText(intentIn.getStringExtra("description"));
        txtProducts_Price.setText(String.valueOf(producto.getPrice()));
        //txtProducts_Price.setText(String.valueOf(intentIn.getIntExtra("price", 0)));
        productService.insertUriToImageView(intentIn.getStringExtra("image"), imgProducts, Products.this);

        btnProducts_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Library.class);
                startActivity(intent);
            }
        });
    }
}