package pn3.teleprompt;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class SelectScript extends AppCompatActivity {

    Cursor c;
    RecyclerView rv;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        rv.getAdapter().notifyDataSetChanged();
    }
    String to="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle bun=getIntent().getExtras();
        to=bun.getString("to");
        FloatingActionButton n=(FloatingActionButton)findViewById(R.id.newScript);
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectScript.this,Script.class);
                startActivityForResult(i,3);
            }
        });
        rv=(RecyclerView)findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scripts,parent,false);
                return new SelectScript.MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SelectScript.MyViewHolder mv= (SelectScript.MyViewHolder)holder;
                c.moveToPosition(position);
                mv.title.setText(c.getString(1));
                mv.prev.setText(c.getString(2));

            }

            @Override
            public int getItemCount() {
                String u="content://DataProvider";
                String projection[]={"(_id","title","data"};
                Uri reqUri=Uri.parse(u);
                c=managedQuery(reqUri,null,null,null,"title");
                if(c==null)
                    return 0;
                return c.getCount();
            }
        });

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, prev;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.list_title);
            prev = (TextView) view.findViewById(R.id.list_prev);

        }

        @Override
        public void onClick(View view) {
            c.moveToPosition(getPosition());
            Intent i;
            if(to.equals("Text")){
                i=new Intent(SelectScript.this,Text.class);
            }
            else {
                i = new Intent(SelectScript.this, Video.class);
            }
            i.putExtra("data",c.getString(2));
            startActivity(i);
            finish();


        }



    }
}
