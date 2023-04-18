package com.android.hre

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityLoginBinding
import com.android.hre.response.LoginResponse
import com.android.hre.storage.SharedPrefManager
import retrofit2.Call
import retrofit2.Response

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
            .enqueue(object: retrofit2.Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.v("Sucess",response.body().toString())
                    val status = response.body()?.status
                    if (status?.equals(1)!!){
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                        Log.v("Sucess",status.toString())

                         SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.data!!)
                        Log.v("Shared", response.body()?.data!!.toString())
                            val intent = Intent(applicationContext, MainActivity::class.java)
                           // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                    }else{
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()

                    }

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


}