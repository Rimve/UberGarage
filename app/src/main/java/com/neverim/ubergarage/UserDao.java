package com.neverim.ubergarage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    /* UserEntity */

    @Query("SELECT * FROM userEntity WHERE userId = :id")
    UserEntity getUserById(int id);

    @Query("SELECT * FROM userEntity WHERE username = :username")
    UserEntity getUserByUsername(String username);

    @Query("SELECT * FROM userEntity")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM userEntity WHERE userId IN (:userIds)")
    List<UserEntity> getAllUsersByIds(int[] userIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    /* UserEntity End */
}
