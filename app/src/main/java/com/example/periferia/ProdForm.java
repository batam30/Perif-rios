package com.example.periferia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.nio.charset.StandardCharsets;

public class ProdForm extends AppCompatActivity {
    private ProductService productService;
    private DBHelper dbHelper;
    private DBBFireBase dbbFireBase;
    private Button btnForm;
    private TextView txtFormName, txtFormDescription, txtFormPrice;
    private EditText editFormName, editFormDescription, editFormPrice;
    private ImageView imgForm;
    private TextView textLatitudForm, textLongitudForm;
    private MapView map;
    private MapController mapController;
    private StorageReference storageReference;
    private String urlImage;

    ActivityResultLauncher<String> content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_form);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        productService = new ProductService();


        btnForm = (Button) findViewById(R.id.btnForm);
        editFormName = (EditText) findViewById(R.id.editFormName);
        editFormDescription = (EditText) findViewById(R.id.editFormDescription);
        editFormPrice = (EditText) findViewById(R.id.editFormPrice);

        imgForm = (ImageView) findViewById(R.id.imgForm);
        textLatitudForm = (TextView) findViewById(R.id.textLatitudForm);
        textLongitudForm = (TextView) findViewById(R.id.textLongitudForm);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intentIN = getIntent();
        Boolean edit = intentIN.getBooleanExtra("edit", false);


        map = (MapView) findViewById(R.id.mapForm);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        mapController = (MapController) map.getController();
        GeoPoint colombia = new GeoPoint(4.570868, -74.297333);
        mapController.setCenter(colombia);
        mapController.setZoom(12);
        map.setMultiTouchControls(true);
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                textLatitudForm.setText(String.valueOf(p.getLatitude()));
                textLongitudForm.setText(String.valueOf(p.getLongitude()));
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);

        if(edit){
            btnForm.setText("Actualizar");
            productService.insertUriToImageView(intentIN.getStringExtra("image"), imgForm, this);
            editFormName.setText(intentIN.getStringExtra("name"));
            editFormDescription.setText(intentIN.getStringExtra("description"));
            editFormPrice.setText(String.valueOf(intentIN.getIntExtra("price", 0)));
            textLatitudForm.setText(String.valueOf(intentIN.getDoubleExtra("latitud", 0.0)));
            textLongitudForm.setText(String.valueOf(intentIN.getDoubleExtra("longitud", 0.0)));
            GeoPoint p = new GeoPoint(intentIN.getDoubleExtra("latitud", 0.0), intentIN.getDoubleExtra("longitud", 0.0));
            Marker marker = new Marker(map);
            marker.setPosition(p);
            map.getOverlays().add(marker);

        }

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
                    public void onActivityResult(Uri result) {
                        Uri uri= result;
                        StorageReference filePath = storageReference.child("image").child(uri.getLastPathSegment());
                        filePath.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Imagen Cargada", Toast.LENGTH_SHORT).show();
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;
                                            urlImage = downloadUrl.toString();
                                            productService.insertUriToImageView(urlImage, imgForm, ProdForm.this);
                                        }
                                    });
                                }
                            });
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
                            urlImage,
                            Double.parseDouble(textLatitudForm.getText().toString().trim()),
                            //productService.imageViewToByte(imgFormProduct)
                            Double.parseDouble(textLongitudForm.getText().toString().trim())
                    );

                    if (edit){
                        producto.setId(intentIN.getStringExtra("id"));
                        dbbFireBase.updateData(producto);

                    }else{
                        //dbHelper.insertData(producto);
                        dbbFireBase.insertData(producto);
                    }
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

        /*btnFmDelete.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }

    public void clean(){
        editFormName.setText("");
        editFormDescription.setText("");
        editFormPrice.setText("");
        imgForm.setImageResource(R.drawable.addimage);
    }
}