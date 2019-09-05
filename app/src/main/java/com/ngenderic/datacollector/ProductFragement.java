package com.ngenderic.datacollector;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragement extends Fragment {

              Button camera;
              EditText productname;
              Spinner spinner;
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;
    private  ArrayList<CatItem> category;
    private static final int PERMISSIONCODE=100;

    public ProductFragement() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadCategory();


        camera=(Button) view.findViewById(R.id.camera);
        productname=(EditText) view.findViewById(R.id.fname);
        spinner=(Spinner) view.findViewById(R.id.spinner1);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   CatItem m= (CatItem) spinner.getSelectedItem();
                    if (productname.getText().toString().equals("")){
                        Toast.makeText(getContext(),"Productname Empty",Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA )== PackageManager.PERMISSION_DENIED ||
                            ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_DENIED   ){
                        String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,PERMISSIONCODE);

                    }
                    else {
                        //permissiongranted

                        opencamera();
                    }
                }
                else {
                    //systemos<23
                    opencamera();
                }

            }
        });





    }

    private void opencamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getFileUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
        startActivityForResult(i, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONCODE:{
                if (grantResults.length> 0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    opencamera();
                }
                else {
                    Toast.makeText(getContext(),"Access Denied",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getFileUri() {

        image_name = productname.getText().toString()+String.valueOf(Math.random()*10000 + 1)+".jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name
        );

        file_uri = Uri.fromFile(file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            new Encode_image().execute();
        }
    }

    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }
    private void makeRequest() {
        final String pname=productname.getText().toString();
        CatItem categoryid=(CatItem) spinner.getSelectedItem();
        final String catid=categoryid.getCountryName();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.251/product/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);
                map.put("pname",pname);
                map.put("catid",catid);
                Log.i("INFOrmation",pname);
                Log.i("INFOrmation",catid);

                return map;
            }
        };
        requestQueue.add(request);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_fragement, container, false);
 }

    private static final String JSON_URL = "http://192.168.43.251/product/index.php";
    private void loadCategory() {
        category=new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    //getting the whole json object from the response
                    JSONObject obj = new JSONObject(response);
                    Log.d("Data ", response.toString());

                    JSONArray contactArray = obj.getJSONArray("cat_id");

                    //now looping through all the elements of the json array

                    for (int i = 0; i < contactArray.length(); i++) {
                        //getting the json object of the particular index inside the array
                        JSONObject contactObject = contactArray.getJSONObject(i);
                                String cat=contactObject.getString("id")+"."+contactObject.getString("name");
                        Log.d("hhhhhhhhh", cat);
                               category.add(new CatItem(cat,contactObject.getString("id")));
                    }
                    CatAdapter adapter = new CatAdapter(getContext(),category);

                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            CatItem clickedItem = (CatItem) parent.getItemAtPosition(position);
                            String clickedCountryName = clickedItem.getCountryName();
                            //Toast.makeText(getContext(), clickedCountryName + " selected", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occur
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error :",error.toString());
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

}
