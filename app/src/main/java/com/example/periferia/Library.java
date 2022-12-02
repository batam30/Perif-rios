package com.example.periferia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Library extends AppCompatActivity {
    private Button btnProduct1, btnProduct2, btnProduct3;
    private TextView txtProduct1, txtProduct2, txtProduct3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        txtProduct1 = (TextView) findViewById(R.id.txtProduct1);
        txtProduct2 = (TextView) findViewById(R.id.txtProduct2);
        txtProduct3 = (TextView) findViewById(R.id.txtProduct3);

        btnProduct1 = (Button) findViewById(R.id.btnProduct1);
        btnProduct2 = (Button) findViewById(R.id.btnProduct2);
        btnProduct3 = (Button) findViewById(R.id.btnProduct3);

        btnProduct1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra( "title", txtProduct1.getText().toString());
                intent.putExtra( "description", "Description Product 1");
                intent.putExtra("imageCode", R.drawable.monitor);
                startActivity(intent);
            }
        });

        btnProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra( "title", txtProduct2.getText().toString());
                intent.putExtra("imageCode", R.drawable.mouse);
                intent.putExtra( "description", "Description Product 2");
                startActivity(intent);
            }
        });

        btnProduct3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra( "title", txtProduct3.getText().toString());
                intent.putExtra("imageCode", R.drawable.keyboard);
                intent.putExtra( "description", "Description Product 3");
                startActivity(intent);
            }
        });
    }
}