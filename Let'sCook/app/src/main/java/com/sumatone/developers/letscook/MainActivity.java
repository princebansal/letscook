package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends ActionBarActivity implements MaterialTabListener, View.OnClickListener {

    private SharedPreferences pref;
    private MaterialTabHost mtab;
    private Toolbar toolbar;
    private int NUM_PAGES=2;
    private Button searchButton;
    private EditText searchBar;
    private SliderAdapter adapter;
    private String url="http://www.princebansal.comeze.com/letscook/getdata.php";
    private ViewPager pager;
    private JSONArray chefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.procolor)));
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String s=pref.getString("type","guest");
        searchButton.setOnClickListener(this);
        mtab.setAccentColor(getResources().getColor(android.R.color.white));
        mtab.setPrimaryColor(getResources().getColor(R.color.procolor));
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mtab.setSelectedNavigationItem(position);
            }
        });
        if(s.equals("guest")) {
            NUM_PAGES=1;
            getData("guest");
        }
        else {
            NUM_PAGES=2;
            getData(s);
        }
        adapter=new SliderAdapter(getSupportFragmentManager());
        for(int i=0;i<adapter.getCount();i++)
        {
            mtab.addTab(
                    mtab.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }


    }

    private void getData(final String s) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCancelable(false);
        Log.i("favv",pref.getString("fav","none"));
        String fav=pref.getString("fav","none");
        url=url+"?fav="+fav;
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resdata", response.toString());
                if (progressDialog.isShowing())
                    progressDialog.hide();
                try {
                    JSONObject object=response;
                    chefs= object.getJSONArray("chefs");
                    adapter = new SliderAdapter(getSupportFragmentManager());
                    pager.setAdapter(adapter);
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
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                    Map<String, String> map = new HashMap<>();
                    Log.i("makhujfghfhrthrft", pref.getString("fav", "none").toString());
                    map.put("fav", pref.getString("fav","none"));
                    return map;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "datarequest");
    }

    private void init() {
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        mtab=(MaterialTabHost)findViewById(R.id.materialTabHost);
        pager= (ViewPager) findViewById(R.id.contactpager1);
        chefs=null;
        searchBar=(EditText)findViewById(R.id.searchbar);
        searchButton=(Button)findViewById(R.id.searchbutton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smallmenu1, menu);
        String s=pref.getString("type","guest");
        if(s.equals("guest"))
            menu.getItem(1).setTitle("Login");
        if(s.equals("ziyad")){
            menu.getItem(0).setVisible(true);
        }
        else
        {
            menu.getItem(0
            ).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.sm_action_logout) {
            updatepref("guest");
            tostart(this);
            return true;
        }
        if (id == R.id.settings) {
            startActivity(new Intent(this,ManageActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    public void tostart(Activity ac) {
        Intent tostart=new Intent(ac.getApplicationContext(),StartScreenActivity.class);
        startActivity(tostart);
        finish();
    }
    public void updatepref(String s) {
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("type", s);
        editor.commit();
    }
    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.searchbutton){
            String s=searchBar.getText().toString();
            if(s==null||s==""){
                Toast.makeText(this,"Cannot be empty",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(this,SearchActivity.class);
                intent.putExtra("search_string",s);
                Log.d("msstring",s);
                startActivity(intent);
            }

        }
    }


    private class SliderAdapter extends FragmentStatePagerAdapter {
        String[] tabs;
        public SliderAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
            tabs=getResources().getStringArray(R.array.tab_names);
        }
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new MainActivityDetailsFragment(chefs,"main");

                case 1:
                    return new MainActivityDetailsFragment(chefs,"fav");
            }
            return null;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override

        public int getCount() {
            return NUM_PAGES;
        }
    }
}
