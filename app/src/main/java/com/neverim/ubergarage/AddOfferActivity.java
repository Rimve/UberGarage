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
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
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

public class AddOfferActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ArrayList<String> imagePaths = new ArrayList<>();
    private Button addOfferButton;
    private ExpandableHeightGrid offerCardView;
    private MapView mapView;
    private EditText priceText, descriptionText, locationText;
    private CheckBox toolsCheck, liftCheck, miscCheck1, miscCheck2, miscCheck3, miscCheck4;
    private Marker marker;
    private GoogleMap googleMap;
    private ArrayList<Bitmap> images;
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").build();

        offerCardView = findViewById(R.id.offer_cardView);
        mapView = findViewById(R.id.offer_mapView);
        mapView.setClipToOutline(true);
        offerCardView.setClipToOutline(true);
        priceText = findViewById(R.id.offer_card_price_value);
        locationText = findViewById(R.id.offer_card_location_value);
        descriptionText = findViewById(R.id.offer_description);
        descriptionText.setClipToOutline(true);
        toolsCheck = findViewById(R.id.offer_card_feature_tools);
        liftCheck = findViewById(R.id.offer_card_feature_lift);
        miscCheck1 = findViewById(R.id.offer_card_feature_ph1);
        miscCheck2 = findViewById(R.id.offer_card_feature_ph2);
        miscCheck3 = findViewById(R.id.offer_card_feature_ph3);
        miscCheck4 = findViewById(R.id.offer_card_feature_ph4);
        addOfferButton = findViewById(R.id.offer_button_add);
        images = new ArrayList<>();

        initGoogleMap(savedInstanceState);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_plus_sign);
        images.add(icon);
        imageAdapter = new ImageAdapter(this, images);
        offerCardView.setAdapter(imageAdapter);
        offerCardView.setExpanded(true);
        offerCardView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    if (images.size() <= 8) {
                        pickImage();
                    }
                    else {
                        Toast.makeText(AddOfferActivity.this, "You have reached the maximum amount of images", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        offerCardView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddOfferActivity.this);
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

        addOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if a price has been entered
                if (priceText.getText().length() <= 0) {
                    Toast.makeText(getBaseContext(), "Invalid price", Toast.LENGTH_LONG).show();
                }
                else {
                    double offerPrice = Double.parseDouble(priceText.getText().toString());
                    // Check if the price is valid. Show error message if not
                    if (offerPrice <= 0) {
                        Toast.makeText(getBaseContext(), "Price has to be higher than 0", Toast.LENGTH_LONG).show();
                    }
                    if (offerPrice >= 99999) {
                        Toast.makeText(getBaseContext(), "Price is too high", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Check if we have a valid cradView path
                        if (marker != null) {
                            final String tempLocation = locationText.getText().toString();
                            if (tempLocation.length() >= 3) {
                                final OfferEntity offer = new OfferEntity();
                                offer.imagePath = "";
                                if (imagePaths.isEmpty()) {
                                    offer.imageType = Constants.NO_IMAGE;
                                    final File imageFile = getOutputMediaFile(Constants.NO_IMAGE);
                                    imagePaths.add(imageFile.getAbsolutePath());
                                    takeMapSnap(imageFile);
                                } else {
                                    offer.imageType = Constants.WITH_IMAGE;
                                }
                                for (String s : imagePaths) {
                                    offer.imagePath += s + ";";
                                }
                                offer.likes = 0;
                                offer.attribute1 = toolsCheck.isChecked();
                                offer.attribute2 = liftCheck.isChecked();
                                offer.attribute3 = miscCheck1.isChecked();
                                offer.attribute4 = miscCheck2.isChecked();
                                offer.attribute5 = miscCheck3.isChecked();
                                offer.attribute6 = miscCheck4.isChecked();
                                offer.coords = marker.getPosition().latitude + ";" + marker.getPosition().longitude;
                                offer.description = descriptionText.getText().toString();
                                offer.price = offerPrice;
                                offer.ownerId = Constants.getUserId();
                                offer.location = locationText.getText().toString();
                                new Thread(new Runnable() {
                                    public void run() {
                                        db.offerDao().insertAllOffers(offer);
                                    }
                                }).start();
                                Toast.makeText(getBaseContext(), "Your offer has been added!", Toast.LENGTH_SHORT).show();
                                Intent intentSearch = new Intent(AddOfferActivity.this, SearchActivity.class);
                                startActivity(intentSearch);
                                finish();
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
                        //resizedBitmap = Utils.rotateBitmap(resizedBitmap, 90);
                        imagePaths.add(imageFile.getAbsolutePath());
                        storeImage(resizedBitmap, imageFile);
                        images.add(resizedBitmap);
                        imageAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(getBaseContext(), "An error has occurred, select another picture", Toast.LENGTH_LONG).show();
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
        // Create an cradView of the map if user did not add any images to his offer
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    storeImage(snapshot, imageFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        googleMap.snapshot(callback);
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
                                Toast.makeText(AddOfferActivity.this, "You need to grant all permission to use this app features", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                googleMap = map;
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.1694, 23.8813), 16.0f));
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
