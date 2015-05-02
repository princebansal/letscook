package com.sumatone.developers.letscook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shalini on 27-12-2014.
 */
public class ManageActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    TextView proceed,cancel;
    RadioGroup typegroup,actiongroup;
    String[] CHEFARRAY={"Name","Image Url"};
    String[] RECIPEARRAY={"Name","Image Url","Chef","Ingredients","Procedure"};
    LinearLayout listContainer;
    private int TYPE=1,ACTION=1;
    private String url="http://www.princebansal.comeze.com/letscook/getalldata.php";
    private String deleteUrl="http://www.princebansal.comeze.com/letscook/deletedata.php";
    Toolbar toolbar;
    JSONArray chefListArray,recipeListArray;
    ArrayList<String> chefList,recipeList;
    Spinner actionListSpinner;
    ArrayAdapter<String> actionListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_layout);
        init();
        formatActionBar();
        setData();
        loadData();

    }



    private void init() {
        proceed=(TextView)findViewById(R.id.actionbutton);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        cancel=(TextView)findViewById(R.id.cancelbutton);
        typegroup=(RadioGroup)findViewById(R.id.rg1);
        listContainer=(LinearLayout)findViewById(R.id.list_container);
        actiongroup=(RadioGroup)findViewById(R.id.rg3);
        actionListSpinner=(Spinner)findViewById(R.id.actionlist);
        actionListAdapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item);
    }

    private void formatActionBar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.manage);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.procolor)));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void setData() {
        proceed.setOnClickListener(this);
        cancel.setOnClickListener(this);
        typegroup.setOnCheckedChangeListener(this);
        actiongroup.setOnCheckedChangeListener(this);
        actionListAdapter.setDropDownViewResource(R.layout.simple_spinner_dopdown_item);
        actionListSpinner.setAdapter(actionListAdapter);
    }
    public void loadData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest jsonObjectRequest=new StringRequest(url,new Response.Listener<String>() {
            @Override
            public void onResponse(String vresponse) {
                progressDialog.hide();
                Log.i("alldata",vresponse.toString());
                try {
                    vresponse=vresponse.substring(0,vresponse.indexOf("<"));
                    vresponse=vresponse.trim();
                    JSONObject response=new JSONObject(vresponse);
                    recipeListArray=response.getJSONArray("recipes");
                    chefListArray=response.getJSONArray("chefs");
                    setAdapters();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.d("error",error.toString());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"alldatarequest");
    }

    private void setAdapters() {
        chefList=new ArrayList<>();
        recipeList=new ArrayList<>();
        try {
            for (int i = 0; i < chefListArray.length(); i++) {
                chefList.add(chefListArray.getJSONObject(i).getString("name"));
            }
            for (int i = 0; i < recipeListArray.length(); i++) {
                recipeList.add(recipeListArray.getJSONObject(i).getString("name"));
            }
            actionListAdapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item);
            actionListAdapter.setDropDownViewResource(R.layout.simple_spinner_dopdown_item);
            actionListAdapter.addAll(chefList);

            actionListSpinner.setAdapter(actionListAdapter);
            typegroup.check(R.id.chef);
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Cannot retrieve data",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smallmenu, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.actionbutton:
                if(ACTION!=3){
                    listContainer.setVisibility(LinearLayout.VISIBLE);
                    ManageFragment manageFragment=new ManageFragment(getFragArrayList(),ACTION,TYPE,chefList,chefListArray,recipeListArray,actionListSpinner.getSelectedItemPosition());
                    getSupportFragmentManager().beginTransaction().replace(R.id.list_container,manageFragment).commit();
                }
                else{
                    listContainer.setVisibility(LinearLayout.GONE);
                    deleteItem();
                }
                break;
            case R.id.cancelbutton:
                finish();
                break;

        }
    }

    private void deleteItem() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String temp=deleteUrl;
        String id="";
        try {
            id=TYPE==1?chefListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("id"):recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("id",id);
        temp=temp+"?type="+TYPE+"&&id="+id;

        StringRequest jsonObjectRequest=new StringRequest(temp,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.i("deleteresponse",response.toString());
                if(response.contains("successful")){
                    Toast.makeText(ManageActivity.this,"Succesfully Deleted",Toast.LENGTH_SHORT).show();
                    loadData();
                }
                else{
                    Toast.makeText(ManageActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.d("error",error.toString());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"deleterequest");
    }

    private ArrayList<ManageInfo> getFragArrayList() {
        Log.d("caoo","caoo");
        if (TYPE == 1) {
            ArrayList<ManageInfo> manageInfoa = new ArrayList<>();
            ManageInfo info = new ManageInfo();
            info.head = CHEFARRAY[0];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = chefListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);
            info = new ManageInfo();
            info.head = CHEFARRAY[1];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = chefListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);
            Log.d("managessrray",manageInfoa.toString());
            return manageInfoa;

        }
        else {
            ArrayList<ManageInfo> manageInfoa = new ArrayList<>();
            ManageInfo info = new ManageInfo();
            info.head =
                    RECIPEARRAY[0];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);

            info = new ManageInfo();
            info.head = RECIPEARRAY[1];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);

            info = new ManageInfo();
            info.head = RECIPEARRAY[2];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    String s="";
                    for(int i=0;i<chefListArray.length();i++){
                        if(chefListArray.getJSONObject(i).getString("id").equals(recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("chefId"))){
                            s=chefList.get(i);
                        }
                    }
                    info.text =s;
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);
            info = new ManageInfo();
            info.head = RECIPEARRAY[3];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("ingridients");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);
            info = new ManageInfo();
            info.head = RECIPEARRAY[4];
            if (ACTION == 1)
                info.text = "";
            else {
                try {
                    info.text = recipeListArray.getJSONObject(actionListSpinner.getSelectedItemPosition()).getString("procedure");
                } catch (JSONException e) {
                    e.printStackTrace();
                    info.text = "";
                }
            }
            manageInfoa.add(info);
            Log.d("managesrray",manageInfoa.toString());
            return manageInfoa;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }




    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (group.getId()){
            case R.id.rg1:
                switch (checkedId){
                    case R.id.chef:
                        TYPE=1;
                        actionListAdapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item,chefList);
                        actionListAdapter.setDropDownViewResource(R.layout.simple_spinner_dopdown_item);
                        actionListSpinner.setAdapter(actionListAdapter);
                        break;
                    case R.id.recipe:
                        TYPE=2;
                        actionListAdapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item,recipeList);
                        actionListAdapter.setDropDownViewResource(R.layout.simple_spinner_dopdown_item);
                        actionListSpinner.setAdapter(actionListAdapter);
                        break;
                }
                break;
            case R.id.rg3:
                switch (checkedId){
                    case R.id.add:
                        actionListSpinner.setVisibility(Spinner.GONE);
                        ACTION=1;
                        break;
                    case R.id.modify:
                        actionListSpinner.setVisibility(Spinner.VISIBLE);
                        ACTION=2;
                        break;
                    case R.id.delete:
                        actionListSpinner.setVisibility(Spinner.VISIBLE);
                        ACTION=3;
                        break;
                }
                break;

        }

    }



}
