package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;


import com.example.android.inventoryapp.data.InventContract;
import com.example.android.inventoryapp.data.InventDBHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int INVENT_LOADER = 0;
    InventCursorAdapter mCursorAdapter;
    private String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.empty_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, detailActivity.class);
                startActivity(intent);
            }
        });


        ListView inventListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);

        inventListView.setEmptyView(emptyView);
        mCursorAdapter = new InventCursorAdapter(this, null);
        inventListView.setAdapter(mCursorAdapter);
        inventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                Intent intent = new Intent(MainActivity.this, detailActivity.class);
                Uri currentInventUri = ContentUris.withAppendedId(InventContract.InventEntry.CONTENT_URI, id);
                intent.setData(currentInventUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INVENT_LOADER, null, this);
    }

    private void insert() {
        ContentValues values = new ContentValues();
        values.put(InventContract.InventEntry.COLUMN_NAME, "ORANGE");
        values.put(InventContract.InventEntry.COLUMN_PRICE, "2");
        values.put(InventContract.InventEntry.COLUMN_QUANTITY, 3);
        values.put(InventContract.InventEntry.COLUMN_EMAIL, "xyz@gmail.com");
        values.put(InventContract.InventEntry.COLUMN_IMAGE, "http://www.mphorticulture.gov.in/images/oranges-vitamin-c-lg.jpg");
        Uri newUri = getContentResolver().insert(InventContract.InventEntry.CONTENT_URI, values);

    }

    private void deleteAll() {

        int rowsDeleted = getContentResolver().delete(InventContract.InventEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + "rows deleted");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_data:
                insert();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_data:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventContract.InventEntry._ID,
                InventContract.InventEntry.COLUMN_NAME,
                InventContract.InventEntry.COLUMN_QUANTITY,
                InventContract.InventEntry.COLUMN_PRICE,
                InventContract.InventEntry.COLUMN_EMAIL,
                InventContract.InventEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this, InventContract.InventEntry.CONTENT_URI, projection, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
