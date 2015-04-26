package com.sumatone.developers.letscook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectGalleryAdapter extends RecyclerView.Adapter<ProjectGalleryAdapter.ProjectGalleryHolder> {
    private final List<MainInfo> data;
    private LayoutInflater inflater;
    private Context c;
    private ClickListener clickListener;
    private CheckedListener checkedListener;
    private ImageLoader imageLoader;
    private SharedPreferences pref;
    private ProjectGalleryAdapter adapter;
    private String TAG="";

    public ProjectGalleryAdapter(Context context, List<MainInfo> list,String t) {
        TAG=t;
        data = list;
        c = context;
        pref= PreferenceManager.getDefaultSharedPreferences(c);
        imageLoader=AppController.getInstance().getImageLoader();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ProjectGalleryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_fragment_recycler, parent, false);
        ProjectGalleryHolder projectViewHolder = new ProjectGalleryHolder(view);
        return projectViewHolder;
    }

    public void setClickListener(ClickListener cl) {
        this.clickListener = cl;
    }
    public void setCheckedListener(CheckedListener cl) {
        this.checkedListener = cl;
    }


    @Override
    public void onBindViewHolder(ProjectGalleryHolder holder, int position) {
        //if(position==0)
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        //else
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        MainInfo info = data.get(position);
       if(data==null){
           Log.d("datanull","datanull");
       }
        imageLoader.get(info.url, ImageLoader.getImageListener(holder.image, R.drawable.loading_bg, R.drawable.error));
        holder.des.setText(info.des);
        if(TAG.equals("main")&&(!pref.getString("type","guest").equals("guest"))){
            holder.favToggle.setVisibility(ToggleButton.VISIBLE);
            String favo=pref.getString("fav","none");
            if(!(favo.equals("none")||favo.equals("")||favo==null)){
                String[] arr=favo.split(",");
                Arrays.sort(arr);
                if(!(Arrays.binarySearch(arr,String.valueOf(info.id))<0)){
                    holder.favToggle.setChecked(true);
                }
            }
        }
        else{
            holder.favToggle.setVisibility(ToggleButton.GONE);
        }

    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    class ProjectGalleryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView des;
        ImageView image;
        RelativeLayout layout;
        ToggleButton favToggle;

        public ProjectGalleryHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.row_image);
            des = (TextView) itemView.findViewById(R.id.row_text);
            favToggle=(ToggleButton)itemView.findViewById(R.id.fav_toggle);
            favToggle.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //delete(getPosition());
            //  Toast.makeText(c,"Item clicked"+getPosition(),Toast.LENGTH_SHORT).show();
            if (clickListener != null) {
                clickListener.onRecyclerItemClick(v, getPosition());
            }
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checkedListener!= null) {
                checkedListener.onRecyclerChecked(buttonView, getPosition(),isChecked);
            }

        }
    }
    public interface ClickListener{
        public void onRecyclerItemClick(View v,int position);
    }
    public interface CheckedListener{
        public void onRecyclerChecked(View v,int position,boolean check);
    }
}