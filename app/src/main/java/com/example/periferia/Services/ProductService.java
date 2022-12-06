package com.example.periferia.Services;

import android.database.Cursor;

import com.example.periferia.Entities.Producto;
import com.example.periferia.R;

import java.util.ArrayList;

public class ProductServices {
    public ArrayList<Producto> cursorToArray(Cursor cursor){
        ArrayList<Producto> list = new ArrayList<>();
        if(cursor.getCount() == 0){
            return list;
        }else{
            while (cursor.moveToNext()){
                Producto producto = new Producto(
                        R.drawable.monitor,
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3))
                );
            }
        }
        return list;
    }
}
