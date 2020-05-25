package com.neverim.ubergarage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface OfferDao {
    /* OfferEntity */

    @Query("SELECT * FROM offerEntity")
    List<OfferEntity> getAllOffers();

    @Query("SELECT * FROM offerEntity WHERE offerId = :offerId")
    OfferEntity getOfferById(int offerId);

    @Query("UPDATE offerEntity SET image_path = :imagePath, image_type = :imageType, attribute1 = :att1, attribute2 = :att2," +
            "attribute3 = :att3, attribute4 = :att4, attribute5 = :att5, attribute6 = :att6," +
            "coords = :coords, price = :price, location = :location, description = :desc, likes = :likes," +
            " ownerId = :ownerId WHERE offerId = :offerId")
    void updateOfferById(int offerId, String imagePath, int imageType, boolean att1, boolean att2,
                         boolean att3, boolean att4, boolean att5, boolean att6,
                         String coords, double price, String location, String desc, int likes, int ownerId);

    @Query("SELECT * FROM offerEntity WHERE ownerId = :userId")
    List<OfferEntity> getUserOffers(int userId);

    @Query("SELECT * FROM offerEntity WHERE attribute1 = :att1 AND attribute2 = :att2" +
            " AND attribute3 = :att3 AND attribute4 = :att4 AND attribute5 = :att5 AND attribute6 = :att6")
    List<OfferEntity> getFilteredOffers(boolean att1, boolean att2, boolean att3, boolean att4,
                                        boolean att5, boolean att6);

    @Query("SELECT * FROM offerEntity WHERE offerId IN (:offerIds)")
    List<OfferEntity> getAllOffersByIds(int[] offerIds);

    @Query("DELETE FROM offerEntity WHERE offerId = :offerId")
    void deleteOfferById(int offerId);

    @Insert
    void insertAllOffers(OfferEntity... offers);

    @Delete
    void deleteOffer(OfferEntity offer);

    /* OfferEntity End */
}
