package com.example.periferia.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.periferia.DB.DBBFireBase;
import com.example.periferia.Entities.Producto;
import com.example.periferia.Library;
import com.example.periferia.ProdForm;
import com.example.periferia.Products;
import com.example.periferia.R;
import com.example.periferia.Services.ProductService;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Producto> arrayProducts;
    ProductService productService;
    //private static final DecimalFormat df = new DecimalFormat("0.00");


    public ProductAdapter(Context context, ArrayList<Producto> arrayProducts) {
        this.context = context;
        this.arrayProducts = arrayProducts;
        this.productService = new ProductService();
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view = layoutInflater.inflate(R.layout.product_template, null);


        ImageView imageTemplate = (ImageView) view.findViewById(R.id.imageTemplate);
        TextView textNameTemplate = (TextView) view.findViewById(R.id.textNameTemplate);
        TextView textDescriptionTemplate = (TextView) view.findViewById(R.id.textDescriptionTemplate);
        TextView textPriceTemplate = (TextView) view.findViewById(R.id.textPriceTemplate);
        Button btnTemplateEdit = (Button) view.findViewById(R.id.btnTemplateEdit);
        Button btnTemplateDelete = (Button) view.findViewById(R.id.btnTemplateDelete);

        Producto producto = arrayProducts.get(i);
        /*byte[] image = producto.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageTemplate.setImageBitmap(bitmap);*/

        textNameTemplate.setText(producto.getName());
        textDescriptionTemplate.setText(producto.getDescription());
        int Col = producto.getPrice();
        double UsdCal = producto.getPrice() * 0.00021 ;
        int Usd = (int) Math.round(UsdCal);
        //String price = "Pesos: " +Col+ "   USD: "+ df.format(Usd);
        String prices = "Pesos: " +Col+ "   USD: "+ Usd;
        textPriceTemplate.setText(prices);

        productService.insertUriToImageView(producto.getImage(), imageTemplate, context);


        imageTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), Products.class);
                intent.putExtra("id", String.valueOf(producto.getId()));
                intent.putExtra("name", String.valueOf(producto.getName()));
                intent.putExtra("description", String.valueOf(producto.getDescription()));
                intent.putExtra("price", String.valueOf(producto.getPrice()));
                intent.putExtra("image", String.valueOf(producto.getImage()));
                context.startActivity(intent);
            }
        });
        btnTemplateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ProdForm.class);
                intent.putExtra("edit", true);
                intent.putExtra("id", producto.getId());
                intent.putExtra("name", producto.getName());
                intent.putExtra("description", producto.getDescription());
                intent.putExtra("price", producto.getPrice());
                intent.putExtra("image", producto.getImage());
                intent.putExtra("latitud", producto.getLatitud());
                intent.putExtra("longitud", producto.getLongitud());

                context.startActivity(intent);
            }
        });

        btnTemplateDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Estas seguro que deseas eliminar este producto?")
                        .setTitle("Confirmación")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DBBFireBase dbFirebase = new DBBFireBase();
                                dbFirebase.deleteData(producto.getId());
                                Intent intent = new Intent(context.getApplicationContext(), Library.class);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        return view;
    }
}

