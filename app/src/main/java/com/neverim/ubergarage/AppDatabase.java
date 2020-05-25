package com.neverim.ubergarage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {OfferEntity.class, UserEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OfferDao offerDao();
    public abstract UserDao userDao();
}
