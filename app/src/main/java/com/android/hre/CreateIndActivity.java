package com.android.hre;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.hre.response.Indent;
import com.android.hre.response.grn.GrnList;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.http.POST;


public class CreateIndActivity extends AppCompatActivity {

    AppCompatButton btncreateintend;
    AppCompatButton btn_materials;
    String userid = "";
    TextView tvcategoryMaterial;
    TextView tvmaterialnameselection;
    TextView tvbrnacdnamescateg;
    TextView tv_qtysize;
    TextView tvdescrtipondisp;
    String materialCategory  = "";
    String materialName = "";
    String materialBraand = "";
    String materialSize  = "";
    String materialdescription = "";
    RelativeLayout rv_cosepcn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  getActionBar().hide();

        //getActionBar().hide();

        setContentView(R.layout.activity_create_intend);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("user_id", "");



        btncreateintend = findViewById(R.id.btncreateintend);
        btn_materials = findViewById(R.id.btn_materials);
        tvbrnacdnamescateg = findViewById(R.id.tvbrnacdnamescateg);
        tvdescrtipondisp = findViewById(R.id.tvdescrtipondisp);
        tv_qtysize = findViewById(R.id.tv_qtysize);
        tvmaterialnameselection = findViewById(R.id.tvmaterialnameselection);
        tvcategoryMaterial = findViewById(R.id.tvcategoryMaterial);
        rv_cosepcn = findViewById(R.id.rv_cosepcn);

        HashMap<String, Indent> indentHashMap = new HashMap<>();
        List<Indent> list = new ArrayList<>();

        for (HashMap.Entry<String, Indent> entry : indentHashMap.entrySet()) {
            String key = entry.getKey();
            Indent value = entry.getValue();

            // ...
        }

        //GrnList.IndentDetail = new GrnList.IndentDetail()

        btncreateintend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeTheJSONObjectToSave();
            }
        });

        btn_materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), SearchMaterialIndentActivity.class);
                mStartForResult.launch(intent);
            }
        });

    }

    private void makeTheJSONObjectToSave() {
        JSONObject mainJSONObject = new JSONObject();
        try {


            mainJSONObject.put("user_id", userid);//loggedInOffice.getOfficeId());
            mainJSONObject.put("pcn", "PCN001");

            JSONArray doxConsignmentJSONArray = new JSONArray();
//            for(int i = 0; i < doxConsignmentJSONArray.length(); i++) {
//                JSONObject doxConsignmentJSONObject = new JSONObject();
//                doxConsignmentJSONObject.put("material_id",materialCategory);
//                doxConsignmentJSONObject.put("description", materialdescription);
//                doxConsignmentJSONObject.put("quantity", materialSize);
//                doxConsignmentJSONArray.put(doxConsignmentJSONObject);
//            }

            JSONObject doxConsignmentJSONObject = new JSONObject();
            doxConsignmentJSONObject.put("material_id",materialCategory);
            doxConsignmentJSONObject.put("description", materialdescription);
            doxConsignmentJSONObject.put("quantity", materialSize);
            doxConsignmentJSONArray.put(doxConsignmentJSONObject);

            mainJSONObject.put("indents", doxConsignmentJSONArray);

            saveTheDoxConsignmentsInServer(mainJSONObject);
        } catch (Exception e) {
            e.printStackTrace();


        }


    }

    private void saveTheDoxConsignmentsInServer(final JSONObject doxConsignmentJSONObject) {

        Log.d("Constants.LOG", doxConsignmentJSONObject.toString());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String url = "https://hre.netiapps.com/api/create-indent";



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, doxConsignmentJSONObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LOG", response.toString());
                progressDialog.dismiss();
                try {
                   int status = response.getInt("status");
//                    if(status == 0) {
//                         fi
//                    } else {
//
//
//                    }
                    showAlertDialogOkAndClose("Data Saved Succesfully");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOG", error.toString());
                progressDialog.dismiss();




            }
        });

        DTDCVolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void showAlertDialogOkAndClose(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();

                        materialCategory = intent.getStringExtra("MaterialID").toString();
                        materialName = intent.getStringExtra("MaterialName").toString();
                        materialBraand = intent.getStringExtra("MaterialBrand").toString();
                        materialSize = intent.getStringExtra("MaterialSize").toString();
                        materialdescription = intent.getStringExtra("MaterialDescrption").toString();
                        rv_cosepcn.setVisibility(VISIBLE);

                        tvcategoryMaterial.setText(materialCategory);
                        tvmaterialnameselection.setText(materialName);
                        tvbrnacdnamescateg.setText(materialBraand);
                        tv_qtysize.setText(materialSize);
                        tvdescrtipondisp.setText(materialdescription);

                    }
                }
            });
}
