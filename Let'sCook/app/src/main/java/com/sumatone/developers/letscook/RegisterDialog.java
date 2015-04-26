package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by shalini on 01-02-2015.
 */
public class RegisterDialog extends Dialog implements View.OnClickListener {
    private Context c;
    private EditText regUser,regPass,regConPass;
    private Button regButton;
    private String url,nametext1,passtext1,conpasstext1;
    private InputMethodManager imm;

    public RegisterDialog(Context context) {
        super(context);
        c=context;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dialog);
        regUser=(EditText)findViewById(R.id.reg_user);
        regPass=(EditText)findViewById(R.id.reg_pas);
        regConPass=(EditText)findViewById(R.id.reg_con_pas);
        imm=(InputMethodManager)c.getSystemService(Activity.INPUT_METHOD_SERVICE);
        regButton=(Button)findViewById(R.id.reg_but);
        regButton.setOnClickListener(this);
        url="http://www.princebansal.comeze.com/letscook/register.php";
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.reg_but:
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                getData();
              break;
        }
    }

    private void getData() {

        nametext1=regUser.getText().toString();
        Log.d("username",nametext1);
        passtext1=regPass.getText().toString();
        conpasstext1=regConPass.getText().toString();
        validate();
    }

    private void validate() {
        if(passtext1.length()==0||nametext1.length()==0||conpasstext1.length()==0)
        {
            Toast.makeText(c,"Please enter all fields",Toast.LENGTH_SHORT).show();
        }
        else if(passtext1.length()<6)
        {
            Toast.makeText(c,"Password must be 6 characters long",Toast.LENGTH_LONG).show();
            regPass.setText("");
            regConPass.setText("");
        }
        else if(!passtext1.equals(conpasstext1))
        {
            Toast.makeText(c,"Password mismatch",Toast.LENGTH_LONG).show();
        }
        else
        {
            if(checkConnection()) {
                register();
            }
            else{
                Toast.makeText(c,"No internet connection",Toast.LENGTH_LONG).show();
            }
        }

    }

    private void register() {

        Log.d("usernamepass",nametext1);
        final ProgressDialog progressDialog=new ProgressDialog(c);
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        url=url+"?username="+nametext1+"&&password="+passtext1;
        StringRequest jsonObjectRequest= new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                url="http://www.princebansal.comeze.com/letscook/register.php";
                Log.d("resdata", response.toString());
                if (progressDialog.isShowing())
                    progressDialog.hide();
                response=response.substring(0,response.indexOf("<"));
                response=response.trim();
                processData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                url="http://www.princebansal.comeze.com/letscook/register.php";
                if (progressDialog.isShowing())
                    progressDialog.hide();
                Log.d("Error", error.toString());
                Toast.makeText(c,"Some Error",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "datarequest");
    }

    private void processData(String response) {
        switch (response){
            case "successful":
                Toast.makeText(c,"Succesfully Registered",Toast.LENGTH_SHORT).show();
                this.hide();
                break;
            case "exist":
                Toast.makeText(c,"Username Already Exist",Toast.LENGTH_SHORT).show();
                regPass.setText("");
                regConPass.setText("");
                regUser.setText("");
                break;
            case "error":
                Toast.makeText(c,"Some Error",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(c,"Unknown Error",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isAvailable() && wifi.isConnected()) {
            return true;
        } else if (mobile.isAvailable() && mobile.isConnected()) {
        /*
         * check.setImageResource(R.drawable.tick);
         * check.setVisibility(View.VISIBLE); noInterntet.setText("");
         */
            return true;
        } else {
            return false;
        }
    }
}
