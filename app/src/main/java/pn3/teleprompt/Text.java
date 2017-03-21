package pn3.teleprompt;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class Text extends AppCompatActivity {

    String data;
    ScrollView sv;
    TextView dat;
    Button ss;
    Handler hand=new Handler();
    Runnable runner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        sv=(ScrollView)findViewById(R.id.scroll_container);
        dat=(TextView)findViewById(R.id.data);
        ss=(Button)findViewById(R.id.option);

        data=getIntent().getExtras().getString("data");
        dat.setText(data);
        ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ss.getText().toString().equals("Start")) {
                    ss.setText("Stop");
                    startScroll();
                }
                else{
                    ss.setText("Start");
                    stopScroll();
                }
            }
        });




    }

    public void startScroll(){

        runner=new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollBy(0,3);
                hand.postDelayed(runner,30);
            }
        };
        hand.postDelayed(runner,30);

    }
    public void stopScroll(){
        hand.removeCallbacks(runner);

    }
}
