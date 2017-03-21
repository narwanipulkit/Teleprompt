package pn3.teleprompt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Script extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText title,data;
    AppCompatButton cancel,save;
    int id;
    String inTitle="",inData="",mode="new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);

        Bundle b=getIntent().getExtras();
        if(b!=null){
            inTitle=b.getString("title");
            inData=b.getString("data");
            mode=b.getString("mode");
            id=b.getInt("id");
        }

        title=(TextInputEditText)findViewById(R.id.titl);
        data=(TextInputEditText)findViewById(R.id.data);
        data.setScroller(new Scroller(getBaseContext()));
        data.setVerticalScrollBarEnabled(true);
        data.setMovementMethod(new ScrollingMovementMethod());
        title.setText(inTitle);
        data.setText(inData);
        AppCompatButton cancel=(AppCompatButton)findViewById(R.id.cancel);
        AppCompatButton save=(AppCompatButton)findViewById(R.id.save);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);






    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                onBackPressed();
                break;
            case R.id.save:
                String t=title.getText().toString();
                String script=data.getText().toString();
                if(t!=null&&script!=null)
                    saveToDb(t,script);
        }

    }

    public void saveToDb(String t,String script){

        String url="content://DataProvider/data";
        ContentValues values=new ContentValues();
        values.put("title",t);
        values.put("data",script);
        Uri q=Uri.parse(url);
        if(mode.equals("edit")){
            values.put("_id",id);
            getContentResolver().update(q,values,"_id="+String.valueOf(id),null);
        }
        else {
            Uri u = getContentResolver().insert(q, values);
        }
        Intent i=new Intent();
        setResult(Activity.RESULT_OK,i);
        finish();
    }
}
