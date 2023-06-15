package com.android.hre

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.api.Convertion
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityLoginBinding
import com.android.hre.storage.SharedPrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Type


class LoginActivity :AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor


    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences=getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()


        binding.btnlogin.setOnClickListener {
            loginUpUser()
        }


    }


    private fun loginUpUser() {
        val email =  binding.etEmployeeId.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if(email.isEmpty()){
            binding.etEmployeeId.error = "Email required"
            binding.etEmployeeId.requestFocus()
            return
        }


        if(password.isEmpty()){
            binding.etPassword.error = "Password required"
            binding.etPassword.requestFocus()
            return
        }
        RetrofitClient.instance.userLogin(email, password)
            .enqueue(object: retrofit2.Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.v("Sucess",response.body().toString())
                    var JsonObject1 = JSONObject()
                    val result = response.body()
                    val gson = Gson()
                    val JsonObject3 = gson.toJsonTree(result).asJsonObject
                  //  var JsonObject = Convertion().convertStringToJSON(response.toString())
                    var JsonObject = JSONObject(JsonObject3.toString())

//                    val stringStringMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
//                    val map: Map<String, String> = gson.fromJson(json, stringStringMap)



                    val status =JsonObject.getString("status").toString()

                    Log.v("TAG","Status is $status")


                    if (JsonObject.getString("status")  == "TRUE" ){
                        var JsonArray = JsonObject.getJSONArray("data")
                        for (i in 0  until JsonArray.length()){
                            JsonObject1 = JsonArray.getJSONObject(i)
                        }
                        Log.v("TAG","$JsonObject1")
                        editor.putBoolean(Constants.ISLOGGEDIN,true)
                        editor.putString("user_id", JsonObject1.getString("user_id"))
                        editor.putString("employee_id", JsonObject1.getString("employee_id"))
                        editor.putString("username", JsonObject1.getString("username"))
                        editor.putString("role", JsonObject1.getString("role"))
                        editor.putString("role_name",JsonObject1.getString("role_name"))

                        editor.apply()
                        editor.commit()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (JsonObject.getString("status")  == "FALSE"){
                        showAlertDialogOkAndCloseAfter(JsonObject.getString("message"))
                    }




/*
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            if (userResponse.status == 1 ) {
                                val userData = userResponse.data.toString()


                                Log.v("TAG","$userData")


                                //  SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.data!!)  // response.body()?.data!!
//                                Log.v("Shared", response.body()?.data!!.toString())
//                                val intent = Intent(applicationContext, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
                            } else {
                                // Handle invalid credentials or empty data
                                Log.v("TAG","$userResponse")

                                Toast.makeText(applicationContext, userResponse.message, Toast.LENGTH_LONG).show()
                                showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                            }
                        } else {
                            showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                            // Handle null response body
                            Toast.makeText(applicationContext, "Error: Invalid Response", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Handle HTTP error
                        Toast.makeText(applicationContext, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
*/



                }
            })


    }
    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }


    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
//            setResult(Activity.RESULT_OK)
             }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


}