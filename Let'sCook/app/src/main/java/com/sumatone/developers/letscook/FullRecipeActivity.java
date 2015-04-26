package com.sumatone.developers.letscook;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shalini on 21-04-2015.
 */
public class FullRecipeActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView rName,rIng,rProc;
    private ImageView rImg;
    private Toolbar toolbar;
    private ImageLoader imageLoader;
    private int recipeId=0;
    private String[] recipeData;
    private String url="http://www.princebansal.comeze.com/getrecipe.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_recipe_layout);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Recipe");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.procolor)));
        rName=(TextView)findViewById(R.id.rname);
        rProc=(TextView)findViewById(R.id.rproc);
        imageLoader=AppController.getInstance().getImageLoader();
        rIng=(TextView)findViewById(R.id.ring);
        rImg=(ImageView)findViewById(R.id.rimage);
        recipeData=getIntent().getStringArrayExtra("recipeinfo");
        display();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
                finish();
            default:
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.home:
                finish();
                break;
            default:
                break;

        }
    }
    private void display() {
        rName.setText(recipeData[0]);
        rIng.setText(recipeData[1]);
        rProc.setText(recipeData[2]);
        imageLoader.get(recipeData[3], ImageLoader.getImageListener(rImg, R.drawable.loading_bg, R.drawable.error));

    }
}
