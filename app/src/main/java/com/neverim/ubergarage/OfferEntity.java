package com.neverim.ubergarage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OfferEntity {
    @PrimaryKey(autoGenerate = true)
    public int offerId;

    @ColumnInfo(name = "image_path")
    public String imagePath;

    @ColumnInfo(name = "image_type")
    public int imageType;

    @ColumnInfo(name = "attribute1")
    public boolean attribute1;

    @ColumnInfo(name = "attribute2")
    public boolean attribute2;

    @ColumnInfo(name = "attribute3")
    public boolean attribute3;

    @ColumnInfo(name = "attribute4")
    public boolean attribute4;

    @ColumnInfo(name = "attribute5")
    public boolean attribute5;

    @ColumnInfo(name = "attribute6")
    public boolean attribute6;

    @ColumnInfo(name = "coords")
    public String coords;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "likes")
    public int likes;

    @ColumnInfo(name = "ownerId")
    public int ownerId;
}
