package com.android.hre

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.ImageMainAdapter
import com.android.hre.adapter.VaultMainFolderAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityVaultMainBinding
import com.android.hre.response.newvault.NewVaultDetiailsMainFolder
import retrofit2.Call
import retrofit2.Response


class VaultMainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVaultMainBinding
    var userid :String = ""
    private lateinit var vaultMainFolderAdapter: VaultMainFolderAdapter
    private lateinit var imageAdapter :ImageMainAdapter
    var allFolderNames :String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_vault_main)
        supportActionBar?.hide()
        binding = ActivityVaultMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        val receivIntent = intent

        if(receivIntent != null){
            allFolderNames = receivIntent.getStringExtra("allFolderNames")!!
        }

        if(TextUtils.isEmpty(allFolderNames)){
            allFolderNames = ""
            vaultInfoDatails()
        }else{
            val stringArray  = allFolderNames.split(",").map { it.trim() }
            if(stringArray.size == 1){
                vaultInfo1Datails(stringArray.get(0))
            }else if(stringArray.size == 2){
                vaultInfo2Datails(stringArray.get(0),stringArray.get(1))
            }else if(stringArray.size == 3){
                vaultInfo3Datails(stringArray.get(0),stringArray.get(1),stringArray.get(2))
            }else if(stringArray.size == 4){
                vaultInfo4Datails(stringArray.get(0),stringArray.get(1),stringArray.get(2),stringArray.get(3))
            }else if(stringArray.size == 5){
                vaultInfo5Datails(stringArray.get(0),stringArray.get(1),stringArray.get(2),stringArray.get(3),stringArray.get(4))
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun vaultInfoDatails() {
        RetrofitClient.instance.newVault(userid)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,allFolderNames)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }

    private fun vaultInfo1Datails(folderName:String) {
        RetrofitClient.instance.newValut1(userid,folderName)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,folderName)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }


    private fun vaultInfo2Datails(folderName1:String,folderName2:String) {
        RetrofitClient.instance.newValut2(userid,folderName1,folderName2)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {

                        val folderName = folderName1 + "," + folderName2
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,folderName)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }


    private fun vaultInfo3Datails(folderName1:String,folderName2:String,folderName3:String) {
        RetrofitClient.instance.newValut3(userid,folderName1,folderName2,folderName3)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {

                        val folderName = folderName1 + "," + folderName2 + "," +folderName3
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,folderName)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }


    private fun vaultInfo4Datails(folderName1:String,folderName2:String,folderName3:String,folderName4:String) {
        RetrofitClient.instance.newValut4(userid,folderName1,folderName2,folderName3,folderName4)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {

                        val folderName = folderName1 + "," + folderName2 + "," +folderName3 + "," +folderName4
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,folderName)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }

    private fun vaultInfo5Datails(folderName1:String,folderName2:String,folderName3:String,folderName4:String,folderName5:String) {
        RetrofitClient.instance.newValut5(userid,folderName1,folderName2,folderName3,folderName4,folderName5)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(this@VaultMainActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {

                        val folderName = folderName1 + "," + folderName2 + "," +folderName3 + "," +folderName4 + "," + folderName5
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders

                        val imageFiles = apiResponse.data.data


                        vaultMainFolderAdapter = VaultMainFolderAdapter(pettyCashBills,this@VaultMainActivity,folderName)

                        val mNoOfColumns = Utility.calculateNoOfColumns(applicationContext,180f)
                        binding.rvFolder.apply {
                            layoutManager = LinearLayoutManager(context)
                            layoutManager = GridLayoutManager(this@VaultMainActivity,mNoOfColumns)
                            adapter = vaultMainFolderAdapter
                        }

                        imageAdapter = ImageMainAdapter(imageFiles,this@VaultMainActivity)

                        binding.rvImagedoc.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = imageAdapter
                        }


                    }

                }

            })
    }




    fun newInstance(context: Context, allFolderNames: String): Intent {

        val intent = Intent(context, VaultMainActivity::class.java)
        intent.putExtra("allFolderNames", allFolderNames)
        return intent
    }

}