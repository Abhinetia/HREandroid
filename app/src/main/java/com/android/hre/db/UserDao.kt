package com.android.hre.db

import androidx.room.*


@Dao
interface UserDao {

    @Query("SELECT * FROM userinfo ORDER BY id DESC")
    fun getAllUserInfo(): List<UserEntity>?

    @Insert
    fun insertUser(user: UserEntity?)

}
