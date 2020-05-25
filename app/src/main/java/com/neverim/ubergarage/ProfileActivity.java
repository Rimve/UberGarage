package com.neverim.ubergarage;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class ProfileActivity extends AppCompatActivity {
    private TextView username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.profile_username);

        AppDatabase offerDao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "offerDao").allowMainThreadQueries().build();
        AppDatabase userDao = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "userDao").allowMainThreadQueries().build();

        int userId = Constants.getUserId();
        List<OfferEntity> offers = offerDao.offerDao().getUserOffers(userId);
        UserEntity user = userDao.userDao().getUserById(userId);

        username.setText(user.username);

        if (offers.size() > 0) {
            // get the reference of RecyclerView
            RecyclerView recyclerView = findViewById(R.id.profile_recyclerView);
            // set a LinearLayoutManager with default vertical orientaion
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
            // call the constructor of CustomAdapter to send the reference and data to Adapter
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(ProfileActivity.this, offers, Constants.PROFILE_RECYCLER);
            recyclerView.setAdapter(recyclerAdapter); // set the Adapter to RecyclerView
        }
    }
}
