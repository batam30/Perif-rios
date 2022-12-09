package com.example.periferia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.periferia.Entities.Producto;
import com.example.periferia.Products;
import com.example.periferia.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Producto> arrayProducts;


    public ProductAdapter(Context context, ArrayList<Producto> arrayProducts) {
        this.context = context;
        this.arrayProducts = arrayProducts;
    }

    @Override
    public int getCount() {
        return this.arrayProducts.size();
    }

    @Override
    public Object getItem(int i) {
        return this.arrayProducts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView,
                        ViewGroup viewGroup) {

        View view = convertView;
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view = layoutInflater.inflate(R.layout.product_template, null);


        ImageView imageTemplate = (ImageView) view.findViewById(R.id.imageTemplate);
        TextView textNameTemplate = (TextView) view.findViewById(R.id.textNameTemplate);
        TextView textDescriptionTemplate = (TextView) view.findViewById(R.id.textDescriptionTemplate);
        TextView textPriceTemplate = (TextView) view.findViewById(R.id.textPriceTemplate);

        Producto producto = arrayProducts.get(i);
        byte[] image = producto.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        imageTemplate.setImageBitmap(bitmap);
        textNameTemplate.setText(producto.getName());
        textDescriptionTemplate.setText(producto.getDescription());
        int Col = producto.getPrice() * 4800;
        int Usd = producto.getPrice();
        String prices = "Pesos: " +Col+ "   USD: "+Usd;
        textPriceTemplate.setText(prices);

        imageTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), Products.class);
                intent.putExtra( "id", String.valueOf(producto.getId()));
                context.startActivity(intent);
            }
        });
        return view;
    }
}

