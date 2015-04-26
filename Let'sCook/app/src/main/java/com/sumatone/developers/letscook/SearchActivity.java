package com.sumatone.developers.letscook;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shalini on 25-04-2015.
 */
public class SearchActivity extends ActionBarActivity {
    private String searchString="";
    private JSONArray chefs;
    private TextView errorText;
    private FrameLayout fragmentContainer;
    private String url="http://www.princebansal.comeze.com/letscook/searchchef.php";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        errorText=(TextView)findViewById(R.id.noresults);
        fragmentContainer=(FrameLayout)findViewById(R.id.fragment_frame);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.procolor)));
        searchString=getIntent().getStringExtra("search_string");
        searchData();
    }

    private void searchData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        searchString=searchString.replace(" ","%20");
        url=url+"?search="+searchString;
        StringRequest jsonObjectRequest= new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("resdata", response.toString());
                response=response.substring(0,response.indexOf("<"));
                response=response.trim();
                if (progressDialog.isShowing())
                    progressDialog.hide();
                errorText.setVisibility(TextView.GONE);
                try {
                    JSONObject object=new JSONObject(response);
                    if(object.getString("status").equals("ok")) {
                        chefs = object.getJSONArray("chefs");
                        setData(chefs);
                    }
                    else{
                        errorText.setVisibility(TextView.VISIBLE);
                        errorText.setText("No results found");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog.isShowing())
                    progressDialog.hide();
                Log.d("Error", error.toString());
                errorText.setVisibility(TextView.VISIBLE);
                errorText.setText("Some problem in connection");
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "datarequest");
    }

    private void setData(JSONArray chefs) {
        MainActivityDetailsFragment fragment=new MainActivityDetailsFragment(chefs,"search");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_frame, fragment)
                .commit();
    }
}
