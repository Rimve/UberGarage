package com.neverim.ubergarage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class PreviewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<String> imagePaths = new ArrayList<>();
    private MapView mapView;
    private TextView name;
    private ExpandableHeightGrid cradView;
    private TextView price, likes, description, location;
    private CheckBox toolsCheck, liftCheck, miscCheck1, miscCheck2, miscCheck3, miscCheck4;
    private int index = -1;
    private Marker marker;
    private LatLng coords;
    private ArrayList<Bitmap> images;
    private ImageAdapter imageAdapter;
    private Button rentOut;
    private Timer timer = new Timer();
    private RelativeLayout previewLayout;
    private ProgressBar loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        name = findViewById(R.id.card_name);
        mapView = findViewById(R.id.preview_mapView);
        price = findViewById(R.id.preview_card_price_value);
        location = findViewById(R.id.preview_card_location_value);
        likes = findViewById(R.id.preview_favorite_view);
        toolsCheck = findViewById(R.id.preview_card_feature_tools);
        liftCheck = findViewById(R.id.preview_card_feature_lift);
        miscCheck1 = findViewById(R.id.preview_card_feature_ph1);
        miscCheck2 = findViewById(R.id.preview_card_feature_ph2);
        miscCheck3 = findViewById(R.id.preview_card_feature_ph3);
        miscCheck4 = findViewById(R.id.preview_card_feature_ph4);
        description = findViewById(R.id.preview_description);
        cradView = findViewById(R.id.preview_cardView);
        images = new ArrayList<>();
        rentOut = findViewById(R.id.preview_button_purchase);
        previewLayout = findViewById(R.id.preview_layout);
        loadingBar = findViewById(R.id.preview_loading_bar);

        initGoogleMap(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            index = extras.getInt("position");
            if (index != -1) {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").allowMainThreadQueries().build();
                OfferEntity offer = db.offerDao().getOfferById(index);

                String[] tempPaths;
                tempPaths = offer.imagePath.split(";");
                imagePaths = new ArrayList<>(Arrays.asList(tempPaths));
                String coordString = offer.coords;
                String[] splitCoords = coordString.split(";");
                mapView.setClipToOutline(true);
                price.setText(String.valueOf(offer.price));
                likes.setText(String.valueOf(offer.likes));
                location.setText(offer.location);
                toolsCheck.setChecked(offer.attribute1);
                liftCheck.setChecked(offer.attribute2);
                miscCheck1.setChecked(offer.attribute3);
                miscCheck2.setChecked(offer.attribute4);
                miscCheck3.setChecked(offer.attribute5);
                miscCheck4.setChecked(offer.attribute6);
                description.setText(offer.description);
                coords = new LatLng(Double.valueOf(splitCoords[0]), Double.valueOf(splitCoords[1]));
                if (offer.imageType == Constants.WITH_IMAGE) {
                    for (String path : imagePaths) {
                        File imgFile = new File(path);
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        images.add(selectedBitmap);
                    }
                }
                if (offer.imageType == Constants.NO_IMAGE) {
                    cradView.setVisibility(View.INVISIBLE);
                }
            }
        }

        imageAdapter = new ImageAdapter(this, images);
        cradView.setAdapter(imageAdapter);
        cradView.setExpanded(true);
        cradView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(PreviewActivity.this, LargeImageActivity.class);
                intent.putExtra("path", imagePaths.get(position)); // put image id in Intent
                startActivity(intent); // start Intent
            }
        });

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setMessage("Garage rented successfully!");
        final AlertDialog alert = dialog.create();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                    finish();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        rentOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviewActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Rent garage");
                builder.setMessage("Are you sure you want to rent this garage?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        previewLayout.setVisibility(View.GONE);
                        loadingBar.setVisibility(View.VISIBLE);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                previewLayout.setVisibility(View.INVISIBLE);
                                loadingBar.setVisibility(View.GONE);
                                alert.show();
                                handler.postDelayed(runnable, 2000);
                            }
                        }, 1000);
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
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            super.finish();
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
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
                                Toast.makeText(PreviewActivity.this, "You need to grant all permission to use this app features", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                map.addMarker(new MarkerOptions().position(coords));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 16.0f));
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
