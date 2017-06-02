package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventContract;

import java.io.ByteArrayOutputStream;


public class detailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENT_LOADER = 0;
    private Uri mCurentUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityText;
    private EditText mEmailtext;
    private ImageView mimageView;
    private Button imageU;
    private boolean imagecap = false;
    private byte[] imagebyte = null;
    static final int Request_image = 1;
    private boolean mInventHasChanged = false;
    private Button increase;
    private Button decrease;
    private int Quantity_integer;
    private View.OnTouchListener mtouchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mInventHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurentUri = intent.getData();
        if (mCurentUri == null) {
            setTitle("ADD A ITEM");
            invalidateOptionsMenu();
        } else {
            setTitle("edit item");
            getLoaderManager().initLoader(EXISTING_INVENT_LOADER, null, this);
        }
        mNameEditText = (EditText) findViewById(R.id.name_edit);
        mEmailtext = (EditText) findViewById(R.id.email_edit);
        mPriceEditText = (EditText) findViewById(R.id.price_edit);
        mQuantityText = (EditText) findViewById(R.id.quantity_text);
        increase = (Button) findViewById(R.id.increase);
        decrease = (Button) findViewById(R.id.decrease);
        imageU = (Button) findViewById(R.id.imageU);
        mimageView = (ImageView) findViewById(R.id.image_detail);

        mNameEditText.setOnTouchListener(mtouchListner);
        mEmailtext.setOnTouchListener(mtouchListner);
        mPriceEditText.setOnTouchListener(mtouchListner);
        mQuantityText.setOnTouchListener(mtouchListner);
        mQuantityText.setText("0");

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quantity_integer = Integer.parseInt(mQuantityText.getText().toString());
                if (Quantity_integer > 0) {
                    Quantity_integer--;
                    mQuantityText.setText(Integer.toString(Quantity_integer));
                    mInventHasChanged = true;
                }
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Quantity_integer = Integer.parseInt(mQuantityText.getText().toString());

                Quantity_integer++;
                mQuantityText.setText(Integer.toString(Quantity_integer));
                mInventHasChanged = true;

            }
        });

        imageU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takepic.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takepic, Request_image);
                        imagecap=true;
                    }
                } else {
                    ActivityCompat.requestPermissions(detailActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            123);
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Request_image && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mimageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (imageBitmap != null) {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                imagebyte = stream.toByteArray();
                Log.i("my message", String.valueOf(imagebyte));
                imagecap = true;
            }
        }
    }

    private void saveInvent() {

        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mNameEditText.setError("required");
        }
        if (TextUtils.isEmpty(mEmailtext.getText().toString())) {
            mEmailtext.setError("required");
        }
        if (TextUtils.isEmpty(mQuantityText.getText().toString())) {
            mQuantityText.setError("required");
        }


        if (!TextUtils.isEmpty(mNameEditText.getText().toString()) && !TextUtils.isEmpty(mPriceEditText.getText().toString()) &&
                !TextUtils.isEmpty(mEmailtext.getText().toString())) {
            String name = mNameEditText.getText().toString().trim();
            int price = 0;
            if (!TextUtils.isEmpty(mPriceEditText.getText().toString().trim()))
                price = Integer.parseInt(mPriceEditText.getText().toString().trim());
            String email = mEmailtext.getText().toString();
            int quantity = Integer.parseInt(mQuantityText.getText().toString().trim());
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email)) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put(InventContract.InventEntry.COLUMN_NAME, name);
            values.put(InventContract.InventEntry.COLUMN_PRICE, price);
            values.put(InventContract.InventEntry.COLUMN_QUANTITY, quantity);
            values.put(InventContract.InventEntry.COLUMN_EMAIL, email);
            if(imagecap) {
                values.put(InventContract.InventEntry.COLUMN_IMAGE, imagebyte);
            }
//            else
//            {
//                values.put(InventContract.InventEntry.COLUMN_IMAGE,0);
//
//            }

            if (mCurentUri == null) {
                Uri uri = getContentResolver().insert(Uri.withAppendedPath(InventContract.InventEntry.BASE_CONTENT_URI, InventContract.InventEntry.PATH_INVENT), values);
                if (uri != null) {
                    Toast.makeText(this, "ITEM ADDED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error adding", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rows = getContentResolver().update(mCurentUri, values, null, null);
                if (rows == 0)
                    Toast.makeText(this, "NO ROW UPDATED", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "ITEM updated", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(detailActivity.this, MainActivity.class);
            startActivity(intent);
        }else
        {
            Toast.makeText(this, "ADD IMAGE", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInvent();
                finish();
                return true;
            case R.id.order:
                showOrderConfirm();
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
            case android.R.id.home:
                if (!mInventHasChanged) {
                    NavUtils.navigateUpFromSameTask(detailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardbutton =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(detailActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardbutton);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (!mInventHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventContract.InventEntry.COLUMN_NAME,
                InventContract.InventEntry.COLUMN_PRICE,
                InventContract.InventEntry.COLUMN_QUANTITY,
                InventContract.InventEntry.COLUMN_EMAIL,
                InventContract.InventEntry.COLUMN_IMAGE
        };
        return new CursorLoader(this, mCurentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_NAME);
            int priceIndex = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_PRICE);
            int emailIndex = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_EMAIL);
            int quantityIndex = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_QUANTITY);
            int imageIndex = cursor.getColumnIndex(InventContract.InventEntry.COLUMN_IMAGE);


            imagebyte = cursor.getBlob(imageIndex);
            String name = cursor.getString(nameIndex);
            String price = cursor.getString(priceIndex);
            int quantity = cursor.getInt(quantityIndex);
            String email = cursor.getString(emailIndex);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);

            mimageView.setImageBitmap(bitmap);
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mEmailtext.setText(email);
            mQuantityText.setText(Integer.toString(quantity));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mEmailtext.setText("");
        mPriceEditText.setText("");


    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButton
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes");
        builder.setPositiveButton("Discard", discardButton);
        builder.setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void showOrderConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("YOU CAN PLACE ORDER NOW");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("E-mail", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text");
                intent.setData(Uri.parse("mailto:" + mEmailtext.getText().toString().trim()));
                intent.putExtra(Intent.EXTRA_SUBJECT, "new order");
                String message = "send the order" +
                        mNameEditText.getText().toString().trim();
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete this item?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "error delete",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}