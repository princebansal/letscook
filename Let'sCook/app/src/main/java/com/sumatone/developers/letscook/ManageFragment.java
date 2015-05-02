package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shalini on 01-05-2015.
 */
public class ManageFragment extends Fragment implements View.OnClickListener {
    private ArrayList<ManageInfo> list;
    private RecyclerView recyclerView;
    private ManageAdapter adapter;
    JSONArray rarray,carray;
    private Button submit;
    private ArrayList<String> array;
    private int ACTION=1,TYPE=1;
    private int position=0;
    private String updateUrl="http://www.princebansal.comeze.com/letscook/updatedata.php";

    public ManageFragment(ArrayList<ManageInfo> a,int action,int type,ArrayList<String> arr,JSONArray carr,JSONArray rarr,int pos) {
        array=arr;
        list=a;
        carray=carr;
        rarray=rarr;
        ACTION=action;
        TYPE=type;
        position=pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.manage_fragment,container,false);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.manage_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(list==null){
            Log.d("null","lisy");
        }
        adapter=new ManageAdapter(getActivity(),list,"fragment");
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        submit=(Button)view.findViewById(R.id.submit_button);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.submit_button:
                submitData();
                break;
        }
    }

    private void submitData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String temp=updateUrl;
        String posi="";
        Log.d("type", String.valueOf(TYPE));
            if(TYPE==1){
                try {
                    posi=carray.getJSONObject(position).getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                temp=temp+"?type="+String.valueOf(TYPE)+"&&action="+String.valueOf(ACTION)+"&&name="+adapter.getText(0).replace(" ", "%20")+"&&id="+posi+"&&url="+adapter.getText(1).replace(" ", "%20");
            }
            else{
                try {
                    posi=rarray.getJSONObject(position).getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                temp=temp+"?type="+String.valueOf(TYPE)+"&&action="+String.valueOf(ACTION)+"&&id="+posi+"&&name="+adapter.getText(0).replace(" ","%20")+"&&url="+adapter.getText(1).replace(" ","%20")+"&&chefId="+adapter.getText(2).replace(" ","%20")+"&&ingredients="+adapter.getText(3).replace(" ","%20")+"&&procedure="+adapter.getText(4).replace(" ","%20");
            }



        StringRequest jsonObjectRequest=new StringRequest(temp,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.i("updateresponse",response.toString());
                if(response.contains("successful")){
                    Toast.makeText(getActivity(), "Succesfully updated", Toast.LENGTH_SHORT).show();
                    Activity activity=getActivity();
                    if(activity instanceof ManageActivity){
                        ((ManageActivity)activity).loadData();
                    }
                }
                else{
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.d("error",error.toString());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"updaterequest");
    }

    public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ManageHolder> {
        private final List<ManageInfo> data;
        private LayoutInflater inflater;
        private Context c;
        private ManageAdapter adapter1;
        private String TAG1="";

        public ManageAdapter(Context context, List<ManageInfo> list, String t) {
            TAG1=t;
            data = list;
            c = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ManageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.manage_recycler, parent, false);
            ManageHolder recipeHolder = new ManageHolder(view);
            return recipeHolder;
        }
        public String getText(int i){
            return data.get(i).text;
        }

        @Override
        public void onBindViewHolder(ManageHolder holder, int position) {
            //if(position==0)
            //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
            //else
            //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            ManageInfo info = data.get(position);
            if(data==null){
                Log.d("datanull", "datanull");
            }
            else{
                holder.head.setText(info.head);
                holder.des.setText(info.text);
            }

        }

        @Override
        public int getItemCount() {

            return data.size();
        }
        public void updateItem(String s,int i){
            data.get(i).text=s;
        }


        class ManageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ModifyDialog.OnDoneButtonClickListener {

            TextView head,des;


            public ManageHolder(View itemView) {
                super(itemView);
                des = (TextView) itemView.findViewById(R.id.desc);
                head = (TextView) itemView.findViewById(R.id.head);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //delete(getPosition());
                //  Toast.makeText(c,"Item clicked"+getPosition(),Toast.LENGTH_SHORT).show();
                if(TYPE==1||(TYPE==2&&getPosition()!=2)) {
                    ModifyDialog modifyDialog = new ModifyDialog(getActivity(), list.get(getPosition()).text, getPosition());
                    modifyDialog.setTitle("Enter details");
                    modifyDialog.setOnDoneButtonClickListener(this);
                    modifyDialog.show();
                }
                else{
                    String[] a={""};
                    ListDialog modifyDialog = new ListDialog(getActivity(),array.toArray(a));
                    modifyDialog.setTitle("Enter details");
                    modifyDialog.setOnDoneButtonClickListener(new ModifyDialog.OnDoneButtonClickListener() {
                        @Override
                        public void onDonebuttonClick(int position, String modifiedString) {
                            adapter.updateItem(array.get(Integer.parseInt(modifiedString)),position);
                            recyclerView.setAdapter(adapter);
                            recyclerView.invalidate();
                        }
                    });
                    modifyDialog.show();
                }
            }


            @Override
            public void onDonebuttonClick(int position, String modifiedString) {
                adapter.updateItem(modifiedString,position);
                recyclerView.setAdapter(adapter);
                recyclerView.invalidate();
            }
        }


    }


}

