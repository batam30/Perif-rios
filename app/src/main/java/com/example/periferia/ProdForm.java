package com.example.periferia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
    private TextView prodFormWarning;
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

        btnForm = (Button) findViewById(R.id.btnForm);
        editFormName = (EditText) findViewById(R.id.editFormName);
        editFormDescription = (EditText) findViewById(R.id.editFormDescription);
        editFormPrice = (EditText) findViewById(R.id.editFormPrice);

        imgForm = (ImageView) findViewById(R.id.imgForm);
        textLatitudForm = (TextView) findViewById(R.id.textLatitudForm);
        textLongitudForm = (TextView) findViewById(R.id.textLongitudForm);
        prodFormWarning = (TextView) findViewById(R.id.prodFormWarning);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intentIN = getIntent();
        Boolean edit = intentIN.getBooleanExtra("edit", false);


        map = (MapView) findViewById(R.id.mapForm);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //productService = new ProductService();

        map.setBuiltInZoomControls(true);
        mapController = (MapController) map.getController();
        GeoPoint colombia = new GeoPoint(4.570868, -74.297333);
        mapController.setCenter(colombia);
        mapController.setZoom(12);
        map.setMultiTouchControls(true);

        productService = new ProductService();
        if (edit) {
            btnForm.setText("Actualizar");
            prodFormWarning.setText("¡¡¡¡Por favor antes de actualizar, ingrese una imagen nueva, para que el programa funcione de manera correcta!!!!");
            editFormName.setText(intentIN.getStringExtra("name"));
            editFormDescription.setText(intentIN.getStringExtra("description"));
            editFormPrice.setText(String.valueOf(intentIN.getIntExtra("price", 0)));
            productService.insertUriToImageView(intentIN.getStringExtra("image"), imgForm, ProdForm.this);
            textLatitudForm.setText(String.valueOf(intentIN.getDoubleExtra("latitud", 0.0)));
            textLongitudForm.setText(String.valueOf(intentIN.getDoubleExtra("longitud", 0.0)));
            GeoPoint geoPoint = new GeoPoint(intentIN.getDoubleExtra("latitud", 0.0), intentIN.getDoubleExtra("longitud", 0.0));
            Marker marker = new Marker(map);
            marker.setPosition(geoPoint);
            map.getOverlays().add(marker);

        }



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
                        Uri uri = result;
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
                            Double.parseDouble(textLongitudForm.getText().toString().trim())
                    );

                    if (edit) {
                        producto.setId(intentIN.getStringExtra("id"));
                        dbbFireBase.updateData(producto);

                    } else {
                        //dbHelper.insertData(producto);
                        dbbFireBase.insertData(producto);
                    }
                } catch (Exception e) {
                    Log.e("DB Insert", e.toString());
                }

                Intent intent = new Intent(getApplicationContext(), Library.class);
                startActivity(intent);
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