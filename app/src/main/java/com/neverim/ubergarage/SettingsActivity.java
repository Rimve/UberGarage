package com.neverim.ubergarage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class SettingsActivity extends AppCompatActivity {
    private Switch att1, att2, att3, att4, att5, att6;
    private Button filterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        att1 = findViewById(R.id.switch_tools);
        att2 = findViewById(R.id.switch_lift);
        att3 = findViewById(R.id.switch_hole);
        att4 = findViewById(R.id.switch_ac);
        att5 = findViewById(R.id.switch_security);
        att6 = findViewById(R.id.switch_tire_mount);
        filterBtn = findViewById(R.id.filter_button);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").allowMainThreadQueries().build();
                List<OfferEntity> offers = db.offerDao().getFilteredOffers(att1.isChecked(), att2.isChecked(),
                        att3.isChecked(), att4.isChecked(), att5.isChecked(), att6.isChecked());

                if (offers.size() > 0) {
                    // get the reference of RecyclerView
                    RecyclerView recyclerView = findViewById(R.id.settings_recyclerView);
                    // set a LinearLayoutManager with default vertical orientaion
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
                    // call the constructor of CustomAdapter to send the reference and data to Adapter
                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(SettingsActivity.this, offers, Constants.OFFER_RECYCLER);
                    recyclerView.setAdapter(recyclerAdapter); // set the Adapter to RecyclerView
                    recyclerView.setVisibility(View.VISIBLE);
                    filterBtn.setVisibility(View.GONE);
                    att1.setVisibility(View.GONE);
                    att2.setVisibility(View.GONE);
                    att3.setVisibility(View.GONE);
                    att4.setVisibility(View.GONE);
                    att5.setVisibility(View.GONE);
                    att6.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getBaseContext(), "There are currently no offers that meet criteria", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
