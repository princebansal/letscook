package com.sumatone.developers.letscook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shalini on 24-02-2015.
 */
public class MainActivityDetailsFragment extends Fragment implements ProjectGalleryAdapter.ClickListener, ProjectGalleryAdapter.CheckedListener {
    private RecyclerView recyclerView;
    private ProjectGalleryAdapter adapter;
    static ImageLoader imageLoader;
    private SharedPreferences pref;
    ImageView recent_image;
    private String url="http://www.princebansal.comeze.com/letscook/markfavourite.php";
    String[] url1;
    List<MainInfo> list;
    JSONArray dataArray;
    String TAG="";
    public MainActivityDetailsFragment() {
    }

    @SuppressLint("ValidFragment")
    public MainActivityDetailsFragment(JSONArray array,String s) {
       dataArray=array;
        TAG=s;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.recommended_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_recommended_recycler);
        adapter = new ProjectGalleryAdapter(getActivity(),getProjectData(),TAG);
        adapter.setClickListener(this);
        adapter.setCheckedListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        imageLoader = AppController.getInstance().getImageLoader();
        recyclerView.setAdapter(adapter);
        pref= PreferenceManager.getDefaultSharedPreferences(getActivity());
        return rootView;
    }

    public List<MainInfo> getProjectData(){
        list=new ArrayList<>();
        MainInfo info=null;
        for(int i=0;i<dataArray.length();i++){
           JSONObject chef= null;
           if(TAG.equals("main")||TAG.equals("search")){
               try {
                   chef = dataArray.getJSONObject(i);
                   info = new MainInfo();
                   info.des = chef.getString("name");
                   info.url = chef.getString("url");
                   info.id = chef.getInt("id");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
            else{
               try {
                   chef = dataArray.getJSONObject(i);
                   info = new MainInfo();
                   if(chef.get("favourite").equals("true")){
                       info.des = chef.getString("name");
                       info.url = chef.getString("url");
                       info.id = chef.getInt("id");
                   }
                   else
                       continue;
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
           list.add(info);
       }


        return list;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }




    @Override
    public void onRecyclerItemClick(View v, int position) {


            Intent i = new Intent(getActivity(), RecipeActivity.class);
            Bundle b = new Bundle();
            Log.d("chefId", String.valueOf(list.get(position).id));
            b.putInt("chefid", list.get(position).id);
            i.putExtra("chefinfo", b);
            getActivity().startActivity(i);

    }

    private void setFavourite(final int id, final String action) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        url = url + "?chefId=" + id+"&&user="+pref.getString("type","guest")+"&&action="+action;
        StringRequest jsonObjectRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (progressDialog.isShowing())
                    progressDialog.hide();
                Log.i("resdata", response.toString());
                response = response.substring(0, response.indexOf("<"));
                response = response.trim();
                JSONObject object=null;
                try {
                    object=new JSONObject(response);
                Toast.makeText(getActivity(),object.getString("status"),Toast.LENGTH_SHORT).show();
                updateDetails(object.getString("status"),object.getString("data"));} catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog.isShowing())
                    progressDialog.hide();
                Log.d("Error", error.toString());
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "datarequest");
    }

    private void updateDetails(String status, String data) {
        if(!status.equals("You have no favourites")){
            SharedPreferences.Editor editor= pref.edit();
            editor.putString("fav",data);
            editor.commit();
        }
    }

    @Override
    public void onRecyclerChecked(View v, int position, boolean check) {
        Log.d("position", String.valueOf(position));
        if(v.getId()==R.id.fav_toggle){
            Log.d("checked", String.valueOf(check));
            if(check==true) {
                try {
                    if(dataArray.getJSONObject(position).getString("favourite").equals("true")){
                        Toast.makeText(getActivity(),"Already favourite",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        setFavourite(list.get(position).id, "true");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else try {
                if(dataArray.getJSONObject(position).getString("favourite").equals("false")){
                    Toast.makeText(getActivity(),"This is not favourite",Toast.LENGTH_SHORT).show();
                }
                else {
                    setFavourite(list.get(position).id,"false");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
