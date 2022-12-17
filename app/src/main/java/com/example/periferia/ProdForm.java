package com.example.periferia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.periferia.DB.DBBFireBase;
import com.example.periferia.DB.DBHelper;
import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ProdForm extends AppCompatActivity {
    private ProductService productService;
    private DBHelper dbHelper;
    private DBBFireBase dbbFireBase;
    private Button btnForm, btnFmGet, btnFmDelete, btnFmUpdate;
    private TextView txtFormName, txtFormDescription, txtFormPrice;
    private EditText editFormName, editFormDescription, editFormPrice, editFormId;
    private ImageView imgForm;
    ActivityResultLauncher<String> content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_form);


        btnForm = (Button) findViewById(R.id.btnForm);
        btnFmGet = (Button) findViewById(R.id.btnFmGet);
        btnFmDelete = (Button) findViewById(R.id.btnFmDelete);
        btnFmUpdate = (Button) findViewById(R.id.btnFmUpdate);
        editFormName = (EditText) findViewById(R.id.editFormName);
        editFormDescription = (EditText) findViewById(R.id.editFormDescription);
        editFormPrice = (EditText) findViewById(R.id.editFormPrice);
        editFormId = (EditText) findViewById(R.id.editFormId);
        imgForm = (ImageView) findViewById(R.id.imgForm);

        editFormPrice.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Si evento pasa
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Precionar Key
                    Toast.makeText(getApplicationContext(), "Enter", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });


        byte[] img = "".getBytes(StandardCharsets.UTF_8);
        try {
            productService = new ProductService();
            dbHelper = new DBHelper(this);
            dbbFireBase = new DBBFireBase();
        } catch (Exception e) {
            Log.e("DB", e.toString());

        }
        content = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result)
                    {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(result);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgForm.setImageBitmap(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
        );

        imgForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.launch("image/*");
            }
        });


        btnForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Producto producto = new Producto(
                            editFormName.getText().toString(),
                            editFormDescription.getText().toString(),
                            Integer.parseInt(editFormPrice.getText().toString()),
                           ""
                           //productService.imageViewToByte(imgForm)
                    );
                    //dbHelper.insertData(producto);
                    dbbFireBase.insertData(producto);
                }catch (Exception e){
                    Log.e("DB Insert", e.toString());
                }

                Intent intent = new Intent(getApplicationContext(), Library.class);
                startActivity(intent);
            }
        });
        /*
        btnFmGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editFormId.getText().toString().trim();
                if (id.compareTo("") != 0) {
                    ArrayList<Producto> list = productService.cursorToArray(dbHelper.getDataById(id));
                    list.add(dbbFireBase.getDataById(id));
                    if (list.size() != 0) {
                        Producto producto = list.get(0);

                        //imgForm.setImageBitmap(productService.byteToBitmap(producto.getImage()));
                        editFormName.setText(producto.getName());
                        editFormDescription.setText(producto.getDescription());
                        editFormPrice.setText(String.valueOf(producto.getPrice()));
                    } else {
                        Toast.makeText(getApplicationContext(), "No existe", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese id", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        btnFmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editFormId.getText().toString().trim();
                if (id.compareTo("") != 0) {
                    Log.d("DB", id);
                    dbHelper.deleteDataById(id);
                    clean();

                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese id a eliminar", Toast.LENGTH_SHORT).show();
                }
            }

        });
        btnFmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editFormId.getText().toString().trim();
                if (id.compareTo("") != 0) {
                    dbHelper.updateDataById(
                            id,
                            editFormName.getText().toString(),
                            editFormDescription.getText().toString(),
                            editFormPrice.getText().toString(),
                            productService.imageViewToByte(imgForm)
                    );
                    clean();
                }else {
                    Toast.makeText(getApplicationContext(), "Ingrese producto a Actualizar", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void clean(){
        editFormName.setText("");
        editFormDescription.setText("");
        editFormPrice.setText("");
        imgForm.setImageResource(R.drawable.addimage);
    }
}