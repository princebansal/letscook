package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shalini on 24-12-2014.
 */
public class StartScreenActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {
    private EditText uname;
    private EditText pass;
    private Button signin,registerButton;
    private TextView guest;
    private ProgressDialog pDialog;
    private String url,nametext,passtext;
    List<NameValuePair> details;
    InputMethodManager imm;
    SharedPreferences pref;
    String fav="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initpref();
        setContentView(R.layout.startscreen);
        init();
        setData();
    }

    private void initpref() {
        String s=pref.getString("type","guest");
        checkpref(s);
    }

    private void checkpref(String s) {
        if(s.equals("guest"))
            return;
        else
        {
           tomain();
        }
    }
    private void updatepref(String s) {
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("type",s);
        editor.commit();
    }

    private void setData() {
        signin.setOnClickListener(this);
        guest.setOnClickListener(this);
        pass.setOnEditorActionListener(this);
        registerButton.setOnClickListener(this);
    }

    private void init() {
        uname= (EditText)findViewById(R.id.userfield);
        pass=(EditText)findViewById(R.id.pasfield);
        signin=(Button)findViewById(R.id.signinbut);
        registerButton=(Button)findViewById(R.id.register);
        guest=(TextView)findViewById(R.id.guesttext);
        uname.requestFocus();
        details= new ArrayList<NameValuePair>();
        url="http://www.princebansal.comeze.com/letscook/authenticate.php";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signinbut:
                getdata();
                break;
            case R.id.guesttext:
                updatepref("guest");
                tomain();
                break;
            case R.id.register:
                RegisterDialog registerDialog=new RegisterDialog(this);
                registerDialog.setTitle("Register");
                registerDialog.show();
                break;
        }
    }



    private void tomain() {
        Intent tomain=new Intent(this,MainActivity.class);
        startActivity(tomain);
        finish();
    }


    private void getdata() {
        nametext=uname.getText().toString();
        passtext=pass.getText().toString();
        validate();
    }

    private void validate() {
        if(passtext.length()==0||nametext.length()==0)
        {
            Toast.makeText(this,"Both fields are required",Toast.LENGTH_LONG).show();
        }
        else if(passtext.length()<6)
        {
            Toast.makeText(this,"Password must be 6 characters long",Toast.LENGTH_LONG).show();
            pass.setText("");
        }
        else
        {
            if(checkConnection()) {
                details.add(new BasicNameValuePair("username", nametext));
                details.add(new BasicNameValuePair("password", passtext));
                new GetEvents().execute();
            }
            else{
                Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean checkConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) this
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
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            imm=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            signin.performClick();//match this behavior to your 'Send' (or Confirm) button
            return true;
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tomain=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        startActivity(tomain);
    }

    private class GetEvents extends AsyncTask<Void, Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StartScreenActivity.this);
            pDialog.setMessage("Signing In");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected String doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String valresponse = sh.makeServiceCall(url, ServiceHandler.POST,details);
            valresponse=valresponse.substring(0,valresponse.indexOf("<"));
            valresponse=valresponse.trim();
            Log.d("res",valresponse);
            return valresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(result.equals("incorrect")||result.equals("")||result.equals(null))
            {
                Toast.makeText(StartScreenActivity.this,"Incorrect username or password",Toast.LENGTH_LONG).show();
                pass.setText("");
                uname.setText("");
                if(imm.isActive())
                imm.hideSoftInputFromWindow(pass.getWindowToken(),0);
            }
            else
            {
                JSONObject det=null;
                String user="guest";
                try {
                    det=new JSONObject(result);
                    user=det.getString("username");
                    fav=det.getString("favourites");
                    updatepref(fav,true);
                    Log.d("ssastring",fav);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updatepref(user);
                tomain();
            }
        }

    }

    private void updatepref(String fav, boolean b) {
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("fav",fav);
        editor.commit();
    }

}
