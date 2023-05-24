package com.android.hre.storage

import android.content.Context
import com.android.hre.Constants
import com.android.hre.response.Data

class SharedPrefManager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("id", -1) != -1
        }

    val user: Data
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
            return Data(
                sharedPreferences.getString("user_id", "1").toString(),
                sharedPreferences.getString("employee_id", null).toString(),
                sharedPreferences.getString("username", null).toString(),
                sharedPreferences.getString("role", null).toString(),
                sharedPreferences.getString("role_name",null).toString()
            )
        }

    fun checkUserNameMatchers(userName : String): Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        if(sharedPreferences.getString("username","").equals(userName)){
            return true
        }
        return false
    }


    fun getUserName(): String {
        val sharedPreferences = mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString("username","").toString()
    }

    fun saveUser(user: Data) {

        val sharedPreferences = mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.ISLOGGEDIN,true)
        editor.putString("user_id", user.user_id)
        editor.putString("employee_id", user.employee_id)
        editor.putString("username", user.username)
        editor.putString("role", user.role)
        editor.putString("role_name",user.role_name)

        editor.apply()
        editor.commit()

    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }
}