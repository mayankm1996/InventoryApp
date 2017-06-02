package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import static android.R.attr.name;
import static com.example.android.inventoryapp.data.InventContract.InventEntry.CONTENT_LIST_TYPE;

/**
 * Created by Mayank on 05-03-2017.
 */

public class InventProvider extends ContentProvider {

    public static final String LOG_TAG = InventProvider.class.getSimpleName();
    private static final int INVENTS = 100;
    private static final int INVENTS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventContract.InventEntry.CONTENT_AUTHORITY, InventContract.InventEntry.PATH_INVENT, INVENTS);
        sUriMatcher.addURI(InventContract.InventEntry.CONTENT_AUTHORITY, InventContract.InventEntry.PATH_INVENT + "/#", INVENTS_ID);

    }

    private InventDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                cursor = database.query(InventContract.InventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTS_ID:
                selection = InventContract.InventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                cursor = database.query(InventContract.InventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("CANNOT QUERY NOT KNOWN" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;


    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                return InventContract.InventEntry.CONTENT_LIST_TYPE;
            case INVENTS_ID:
                return InventContract.InventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                return insertInvent(uri, contentValues);
            default:
                throw new IllegalArgumentException("INSERTION NOT SUPPORTED HERE " + uri);
        }
    }

    private Uri insertInvent(Uri uri, ContentValues values) {
        String name = values.getAsString(InventContract.InventEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("product requires a name");
        }
        String price = values.getAsString(InventContract.InventEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("PRICE SHOULD BE ENTERED ");
        }
        Integer quantity = values.getAsInteger(InventContract.InventEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("enter correct quantity");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventContract.InventEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "FAILED TO INSERT ROW " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS:
                rowsDeleted = database.delete(InventContract.InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTS_ID:
                selection = InventContract.InventEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = database.delete(InventContract.InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("DELETION NOT SUPPORTED" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTS: {
                return updateInvent(uri, contentValues, selection, selectionArgs);
            }
            case INVENTS_ID:
                selection = InventContract.InventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))
                };
                return updateInvent(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UPDATE IS NOT SUPPORTED FOR" + uri);
        }
    }

    private int updateInvent(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventContract.InventEntry.COLUMN_NAME)) {
            String name = values.getAsString(InventContract.InventEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("product requires a name");
            }
        }

        if (values.containsKey(InventContract.InventEntry.COLUMN_PRICE)) {
            String price = values.getAsString(InventContract.InventEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("PRICE SHOULD BE ENTERED ");
            }
        }
        if (values.containsKey(InventContract.InventEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventContract.InventEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("enter correct quantity");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventContract.InventEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
