package com.sumatone.developers.letscook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by shalini on 16-01-2015.
 */
public class ModifyDialog extends Dialog implements View.OnClickListener {
    EditText ed;
    Button b;
    String pre="";
    private OnDoneButtonClickListener onDoneButtonClickListener;
    Context context;
    int reqCode=0;

    public ModifyDialog(Context c,String s,int position) {
        super(c);
        context=c;
        pre=s;
        reqCode=position;
    }
    public void setOnDoneButtonClickListener(OnDoneButtonClickListener listener){
        this.onDoneButtonClickListener=listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_dialog);
        /*WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.y = 0;

        this.getWindow().setAttributes(params);*/
        ed=(EditText)findViewById(R.id.modify_edit_text);
        b=(Button)findViewById(R.id.donebutton);
        b.setOnClickListener(this);
        ed.setText(pre);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.donebutton)
        {

           onDoneButtonClickListener.onDonebuttonClick(reqCode,ed.getText().toString());
            this.cancel();
        }
    }
    public interface OnDoneButtonClickListener{
        public void onDonebuttonClick(int position,String modifiedString);
    }
}
