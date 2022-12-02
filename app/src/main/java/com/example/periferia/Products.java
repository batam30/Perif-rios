package com.example.periferia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Products extends AppCompatActivity {
    private Button btnProducts_back;
    private TextView txtProducts_Mayor, txtProducts_Descrip;
    private ImageView imgProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        btnProducts_back = (Button)  findViewById(R.id.btnProducts_back);
        txtProducts_Mayor = (TextView) findViewById(R.id.txtProducts_Mayor);
        txtProducts_Descrip = (TextView) findViewById(R.id.txtProducts_Descrip);
        imgProducts = (ImageView) findViewById(R.id.imgProducts);

        Intent intentIn = getIntent();
        txtProducts_Mayor.setText(intentIn.getStringExtra("title"));
        txtProducts_Descrip.setText(intentIn.getStringExtra("description"));

        int codeImage = intentIn.getIntExtra("imageCode", 0);
        imgProducts.setImageResource(codeImage);

        btnProducts_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Library.class);
                startActivity(intent);
            }
        });
    }
}