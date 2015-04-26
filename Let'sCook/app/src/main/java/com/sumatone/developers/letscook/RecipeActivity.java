package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by shalini on 24-02-2015.
 */
public class RecipeActivity extends ActionBarActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private JSONArray recipesList=null;
    private List<RecipeInfo> list;
    private RecipeAdapter adapter;
    private Toolbar toolbar;
    private int chefId=0;
    private String url="http://www.princebansal.comeze.com/letscook/getrecipesdata.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe_layout);
        toolbar=(Toolbar)findViewById(R.id.app_bar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recipes");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.procolor)));
        list=null;
        chefId=getIntent().getBundleExtra("chefinfo").getInt("chefid",1);
        loadData();
        recyclerView = (RecyclerView) findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadData() {
        url=url+"?chefId="+chefId;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    progressDialog.hide();
                Log.i("reciperes",response.toString());
                try {
                    recipesList=response.getJSONArray("recipes");
                    setAdapterRecycler();
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
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("chefid", String.valueOf(chefId));
                return map;
            }
        };

    AppController.getInstance().addToRequestQueue(jsonObjectRequest,"reciperequest");
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
    private void setAdapterRecycler() {
        list=new ArrayList<>();
        RecipeInfo info = null;
        try {
            for (int i = 0; i < recipesList.length(); i++) {
                JSONObject object = recipesList.getJSONObject(i);
                //info.url = object.getString("imageurl");
                info=new RecipeInfo();
                info.name = object.getString("name");
                info.id = object.getInt("id");
                info.chefId = object.getInt("chefId");
                info.url=object.getString("url");
                info.ingridients=object.getString("ingridients");
                info.procedure=object.getString("procedure");
                list.add(info);
            }
        } catch (Exception e) {
                e.printStackTrace();
            Log.d("error","error");
        }
        adapter=new RecipeAdapter(this,list,"recipe");
        recyclerView.setAdapter(adapter);
    }



    public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
        private final List<RecipeInfo> data;
        private LayoutInflater inflater;
        private Context c;
        private ImageLoader imageLoader;
        private RecipeAdapter adapter1;
        private String TAG1="";

        public RecipeAdapter(Context context, List<RecipeInfo> list, String t) {
            TAG1=t;
            data = list;
            c = context;
            imageLoader=AppController.getInstance().getImageLoader();
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.main_fragment_recycler, parent, false);
            RecipeHolder recipeHolder = new RecipeHolder(view);
            return recipeHolder;
        }


        @Override
        public void onBindViewHolder(RecipeHolder holder, int position) {
            //if(position==0)
            //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
            //else
            //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            RecipeInfo info = data.get(position);
            if(data==null){
                Log.d("datanull", "datanull");
            }
            imageLoader.get(info.url, ImageLoader.getImageListener(holder.image, R.drawable.loading_bg, R.drawable.error));
            holder.des.setText(info.name);

        }

        @Override
        public int getItemCount() {

            return data.size();
        }

        class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView des;
            ImageView image;
            RelativeLayout layout;

            public RecipeHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.row_image);
                des = (TextView) itemView.findViewById(R.id.row_text);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //delete(getPosition());
                //  Toast.makeText(c,"Item clicked"+getPosition(),Toast.LENGTH_SHORT).show();

                Intent i=new Intent(RecipeActivity.this, FullRecipeActivity.class);
                Bundle b=new Bundle();
                RecipeInfo info=data.get(getPosition());
                String[] s={info.name, info.ingridients,info.procedure,info.url,String.valueOf(info.id),String.valueOf(info.chefId)};
                i.putExtra("recipeinfo",s);
                startActivity(i);
            }


        }


    }
}
