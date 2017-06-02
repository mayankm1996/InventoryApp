package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventContract;


/**
 * Created by Mayank on 09-03-2017.
 */

public class InventCursorAdapter extends CursorAdapter {


    public InventCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        int id = cursor.getColumnIndex(InventContract.InventEntry._ID);
        int name = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_NAME);
        int price = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_PRICE);
        final int quantity = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_QUANTITY);
        int image1 = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_IMAGE);

        final long id1 = cursor.getLong(id);
        String name_list = cursor.getString(name);
        int price_list = cursor.getInt(price);
        int quantity_list = cursor.getInt(quantity);
        byte[] image = cursor.getBlob(image1);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        TextView name_view = (TextView) view.findViewById(R.id.list_name);
        TextView price_view = (TextView) view.findViewById(R.id.list_price);
        final TextView quantity_view = (TextView) view.findViewById(R.id.list_quantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.inventory_img);

        name_view.setText(name_list);
        price_view.setText(Integer.toString(price_list));
        quantity_view.setText(Integer.toString(quantity_list));

        imageView.setImageBitmap(bitmap);
        ImageView img = (ImageView) view.findViewById(R.id.button_sale);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity_new = Integer.parseInt(quantity_view.getText().toString());
                if (quantity_new > 0) {
                    quantity_new--;
                    quantity_view.setText(Integer.toString(quantity_new));
                    ContentValues values = new ContentValues();
                    values.put(InventContract.InventEntry.COLUMN_QUANTITY, quantity_new);
                    Uri uri = ContentUris.withAppendedId(InventContract.InventEntry.CONTENT_URI, id1);
                    context.getContentResolver().update(uri, values, null, null);
                }
            }
        });
    }
}
