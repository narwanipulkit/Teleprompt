package pn3.teleprompt;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v4.widget.CursorAdapter;


import com.getbase.floatingactionbutton.FloatingActionButton;

public class SelectScript extends AppCompatActivity implements View.OnClickListener {

    Cursor c;
    RecyclerView rv;
    Loader<Cursor> cursorLoader;

    @Override
    protected void onResume() {
        super.onResume();
        cursorLoader=getLoaderManager().restartLoader(Scripts.LOADER_ID,null, new android.app.LoaderManager.LoaderCallbacks<Cursor>(){
            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String url = "content://DataProvider/data";
                CursorLoader curLoader = new CursorLoader(getBaseContext(), Uri.parse(url), null, null, null, "_id");
                rv.getAdapter().notifyDataSetChanged();
                return curLoader;
            }

            @Override
            public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
                c=cursor;
                rv.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onLoaderReset(android.content.Loader<Cursor> loader) {

            }

        });
    }

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
        n.setOnClickListener(this);
        FloatingActionButton d=(FloatingActionButton)findViewById(R.id.drive);
        d.setOnClickListener(this);
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
                if(c==null)
                    return 0;
                return c.getCount();
            }
        });
        cursorLoader=getLoaderManager().initLoader(Scripts.LOADER_ID,null, new android.app.LoaderManager.LoaderCallbacks<Cursor>(){
            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String url = "content://DataProvider/data";
                CursorLoader curLoader = new CursorLoader(getBaseContext(), Uri.parse(url), null, null, null, "_id");
                rv.getAdapter().notifyDataSetChanged();
                return curLoader;
            }

            @Override
            public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
                c=cursor;
                rv.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onLoaderReset(android.content.Loader<Cursor> loader) {

            }

        });

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.newScript:
                i=new Intent(SelectScript.this,Script.class);
                startActivityForResult(i,3);
                break;
            case R.id.drive:
                i=new Intent(SelectScript.this,DriveSync.class);
                startActivity(i);
                break;
        }
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
