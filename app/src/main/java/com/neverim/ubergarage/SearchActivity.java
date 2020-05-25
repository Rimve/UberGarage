package com.neverim.ubergarage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").allowMainThreadQueries().build();

        List<OfferEntity> offers = db.offerDao().getAllOffers();

        if (offers.size() > 0) {
            // get the reference of RecyclerView
            RecyclerView recyclerView = findViewById(R.id.search_recyclerView);
            // set a LinearLayoutManager with default vertical orientaion
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
            // call the constructor of CustomAdapter to send the reference and data to Adapter
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(SearchActivity.this, offers, Constants.OFFER_RECYCLER);
            recyclerView.setAdapter(recyclerAdapter); // set the Adapter to RecyclerView
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentSettings = new Intent(SearchActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_user:
                Intent intentProfile = new Intent(SearchActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                return true;

            case R.id.action_add_offer:
                Intent intentNewOffer = new Intent(SearchActivity.this, AddOfferActivity.class);
                startActivity(intentNewOffer);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
