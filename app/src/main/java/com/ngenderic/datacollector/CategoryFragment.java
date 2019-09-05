package com.ngenderic.datacollector;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

                        EditText product_name;
                        Button btn;
    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

                     product_name =(EditText) view.findViewById(R.id.editText5);
                     btn=(Button) view.findViewById(R.id.login);
                     btn.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            String name=product_name.getText().toString();
//                             String method= "save";
//                             BackgroundTask backgroundTask= new BackgroundTask(view.getContext());
//                             backgroundTask.execute(name);
                             Log.i("INFO", name);
                             if (name.equals(""))
                             {
                                 Toast.makeText(getContext(),"Category Empty",Toast.LENGTH_LONG).show();
                             }
                             else{
                             sendingData(name);
                             }

                         }
                     });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }


    public void sendingData(final String category){
        String url = "http://192.168.43.251/product/index.php";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(getContext(),"successfully saved",Toast.LENGTH_LONG).show();
                        product_name.setText("");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category",category);


                return params;
            }
        }
                ;
        queue.add(postRequest);

    }

}
