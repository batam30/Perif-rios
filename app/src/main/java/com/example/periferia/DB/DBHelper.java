package com.example.periferia.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.periferia.Entities.Producto;
import com.example.periferia.Services.ProductService;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase;
    private ProductService productService;

    public DBHelper(Context context){
        super(context, "DBPerifericos", null,  1);
        sqLiteDatabase = this.getWritableDatabase();
        productService = new ProductService();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE PRODUCTS("+
                "id TEXT PRIMARY KEY," +
                "name VARCHAR," +
                "description TEXT," +
                "price VARCHAR," +
                "image TEXT," +
                "deleted TEXT," +
                "createdAt TEXT,"+
                "updatedAt TEXT"+
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS PRODUCTS");
    }

    //METODOS CRUD
    public void insertData(Producto producto){
        String sql = "INSERT INTO PRODUCTS VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, producto.getId());
        statement.bindString(2, producto.getName());
        statement.bindString(3, producto.getDescription());
        statement.bindString(4, String.valueOf(producto.getPrice()));
        statement.bindString(5, producto.getImage());
        statement.bindString(6, String.valueOf(producto.isDeleted()));
        statement.bindString(7, productService.dateToString(producto.getCreatedAt()));
        statement.bindString(8, productService.dateToString(producto.getUpdatedAt()));
        statement.executeInsert();
    }

    public Cursor getData(){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM PRODUCTS", null);
        return cursor;
    }

    public Cursor getDataById(String id){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM PRODUCTS WHERE id = "+id, null);
        return cursor;
    }

    public void deleteDataById(String id){
        sqLiteDatabase.execSQL("DELETE FROM PRODUCTS WHERE id = " + id);
    }

    public void updateDataById(String id, String name, String description, String price, byte[] image){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("price", price);
        contentValues.put("image", image);
        sqLiteDatabase.update("PRODUCTS",contentValues,"id = ?",new String[]{id});
    }

}