package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by shalini on 16-01-2015.
 */
public class ListDialog extends Dialog implements View.OnClickListener {
    Context context;
    int reqCode=0;
    Spinner spinner;
    ModifyDialog.OnDoneButtonClickListener onDoneButtonClickListener;
    ArrayAdapter<String> adapter;
    String[] list;
    private Button button;
    public ListDialog(Context c,String[] s) {
        super(c);
        context=c;
        list=s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_dialog);
        button=(Button)findViewById(R.id.done_button);
        button.setOnClickListener(this);
        spinner=(Spinner)findViewById(R.id.chefList);
        adapter=new ArrayAdapter<String>(context,R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dopdown_item);
        spinner.setAdapter(adapter);

        /*WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.y = 0;

        this.getWindow().setAttributes(params);*/

    }
    public void setOnDoneButtonClickListener(ModifyDialog.OnDoneButtonClickListener listener){
        this.onDoneButtonClickListener=listener;
    }

    @Override
    public void onClick(View v) {
        onDoneButtonClickListener.onDonebuttonClick(2,String.valueOf(spinner.getSelectedItemPosition()));
        this.cancel();
    }
}
