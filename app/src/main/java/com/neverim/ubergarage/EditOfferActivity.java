package com.neverim.ubergarage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.room.Room;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditOfferActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<String> imagePaths = new ArrayList<>();
    private Button editOfferButton;
    private ExpandableHeightGrid editCardView;
    private MapView mapView;
    private EditText price, descriptionText, locationText;
    private CheckBox toolsCheck, liftCheck, miscCheck1, miscCheck2, miscCheck3, miscCheck4;
    private int index = -1;
    private OfferEntity offerEditable;
    private GoogleMap googleMap;
    private Marker marker;
    private LatLng coords;
    private ImageAdapter imageAdapter;
    private ArrayList<Bitmap> images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").allowMainThreadQueries().build();

        editCardView = findViewById(R.id.edit_cardView);
        mapView = findViewById(R.id.edit_mapView);
        price = findViewById(R.id.edit_card_price_value);
        descriptionText = findViewById(R.id.edit_description);
        locationText = findViewById(R.id.edit_card_location_value);
        toolsCheck = findViewById(R.id.edit_card_feature_tools);
        liftCheck = findViewById(R.id.edit_card_feature_lift);
        miscCheck1 = findViewById(R.id.edit_card_feature_ph1);
        miscCheck2 = findViewById(R.id.edit_card_feature_ph2);
        miscCheck3 = findViewById(R.id.edit_card_feature_ph3);
        miscCheck4 = findViewById(R.id.edit_card_feature_ph4);
        editOfferButton = findViewById(R.id.edit_button_add);
        images = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_plus_sign);
        images.add(icon);
        if (extras != null) {
            index = extras.getInt("position");
            if (index != -1) {
                offerEditable = db.offerDao().getOfferById(index);
                String[] tempPaths;
                tempPaths = offerEditable.imagePath.split(";");
                imagePaths = new ArrayList<>(Arrays.asList(tempPaths));

                if (offerEditable.imageType == Constants.WITH_IMAGE) {
                    for (String path : imagePaths) {
                        File imgFile = new File(path);
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        images.add(selectedBitmap);
                    }
                }
                if (offerEditable.imageType == Constants.NO_IMAGE) {
                    // Remove the map picture that the user does not see
                    imagePaths.clear();
                }
                String coordString = offerEditable.coords;
                String[] splitCoords = coordString.split(";");
                mapView.setClipToOutline(true);
                price.setText(String.valueOf(offerEditable.price));
                locationText.setText(offerEditable.location);
                toolsCheck.setChecked(offerEditable.attribute1);
                liftCheck.setChecked(offerEditable.attribute2);
                miscCheck1.setChecked(offerEditable.attribute3);
                miscCheck2.setChecked(offerEditable.attribute4);
                miscCheck3.setChecked(offerEditable.attribute5);
                miscCheck4.setChecked(offerEditable.attribute6);
                descriptionText.setText(offerEditable.description);
                coords = new LatLng(Double.valueOf(splitCoords[0]), Double.valueOf(splitCoords[1]));
            }
        }

        initGoogleMap(savedInstanceState);

        imageAdapter = new ImageAdapter(this, images);

        editCardView.setAdapter(imageAdapter);
        editCardView.setExpanded(true);
        editCardView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    if (images.size() <= 8) {
                        pickImage();
                        offerEditable.imageType = Constants.WITH_IMAGE;
                    }
                    else {
                        Toast.makeText(EditOfferActivity.this, "You have reached the maximum amount of images", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        editCardView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditOfferActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Remove image");
                    builder.setMessage("Are you sure you want to remove this image?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove cradView
                            images.remove(position);
                            imagePaths.remove(position - 1);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });

        editOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a price has been entered
                if (price.getText().length() <= 0) {
                    Toast.makeText(getBaseContext(), "Invalid price", Toast.LENGTH_LONG).show();
                }
                else {
                    double offerPrice = Double.parseDouble(price.getText().toString());
                    // Check if the price is valid. Show error message if not
                    if (offerPrice <= 0) {
                        Toast.makeText(getBaseContext(), "Price has to be higher than 0.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Check if we have a valid editCardView path
                        if (marker != null) {
                            final String tempLocation = locationText.getText().toString();
                            if (tempLocation.length() >= 3) {
                                final double tempPrice = offerPrice;
                                final String tmpCoords = marker.getPosition().latitude + ";" + marker.getPosition().longitude;
                                offerEditable.imagePath = "";
                                if (imagePaths.isEmpty()) {
                                    offerEditable.imageType = Constants.NO_IMAGE;
                                    final File imageFile = getOutputMediaFile(Constants.NO_IMAGE);
                                    imagePaths.add(imageFile.getAbsolutePath());
                                    takeMapSnap(imageFile);
                                } else {
                                    offerEditable.imageType = Constants.WITH_IMAGE;
                                }
                                for (String s : imagePaths) {
                                    offerEditable.imagePath += s + ";";
                                }
                                new Thread(new Runnable() {
                                    public void run() {
                                        db.offerDao().updateOfferById(offerEditable.offerId, offerEditable.imagePath, offerEditable.imageType, toolsCheck.isChecked(),
                                                liftCheck.isChecked(), miscCheck1.isChecked(), miscCheck2.isChecked(), miscCheck3.isChecked(), miscCheck4.isChecked(),
                                                tmpCoords, tempPrice, tempLocation, descriptionText.getText().toString(), offerEditable.likes, Constants.getUserId());
                                    }
                                }).start();
                                Toast.makeText(getBaseContext(), "Your offer has been edited!", Toast.LENGTH_SHORT).show();
                                Intent intentSearch = new Intent(EditOfferActivity.this, ProfileActivity.class);
                                startActivity(intentSearch);
                            } else {
                                Toast.makeText(getBaseContext(), "Enter location", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Mark the rough garage location on the map.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getBaseContext(), "An error has occurred", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    final Uri imageUri = data.getData();
                    if (imageUri != null) {
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        final File imageFile = getOutputMediaFile(Constants.WITH_IMAGE);
                        final BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;
                        options.inJustDecodeBounds = false;
                        options.inTempStorage = new byte[16 * 1024];

                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImage, 650, 900, false);
                        resizedBitmap = Utils.rotateBitmap(resizedBitmap, 90);
                        imagePaths.add(imageFile.getAbsolutePath());
                        storeImage(resizedBitmap, imageFile);
                        images.add(resizedBitmap);
                        imageAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(getBaseContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void pickImage() {
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.PICK_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_REQUEST_CODE);
        }
    }

    private void storeImage(final Bitmap image, final File location) {
        new Thread(new Runnable() {
            public void run() {
                if (location == null) {
                    Log.d("SAVEIMG", "Error creating media file, check storage permissions: ");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(location);
                    image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d("SAVEIMG", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("SAVEIMG", "Error accessing file: " + e.getMessage());
                }
            }
        }).start();
    }

    public File getOutputMediaFile(int imgState) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String template = "yyyyMMdd_HHmmssSSS";
        Locale locale = Locale.getDefault();
        String timeStamp = new SimpleDateFormat(template, locale).format(new Date());
        File mediaFile;
        String mImageName = null;
        if (imgState == Constants.WITH_IMAGE) {
            mImageName = "OF_" + timeStamp + ".jpg";
        }
        if (imgState == Constants.NO_IMAGE) {
            mImageName = "MAP_" + timeStamp + ".jpg";
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void takeMapSnap(final File imageFile) {
        // Create an editCardView of the map if user did not add any images to his offer
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    storeImage(snapshot, imageFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        googleMap.setOnMapLoadedCallback( new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.snapshot(callback);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        if (map != null) {
            Dexter.withActivity(this)
                    .withPermissions(ACCESS_FINE_LOCATION, INTERNET, ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if(!report.areAllPermissionsGranted()){
                                Toast.makeText(EditOfferActivity.this, "You need to grant all permission to use this app features", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                googleMap = map;
                                marker = map.addMarker(new MarkerOptions().position(coords));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 16.0f));
                                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng point) {
                                        map.clear();
                                        marker = map.addMarker(new MarkerOptions().position(point));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        }
                    })
                    .check();
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
}
