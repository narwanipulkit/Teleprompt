package pn3.teleprompt;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;


public class Scripts extends Fragment {


    String to;
    RecyclerView r;
    @Override
    public void onResume() {
        super.onResume();
        r.getAdapter().notifyDataSetChanged();


    }


    public Scripts() {
        // Required empty public constructor
    }

    public static Scripts newInstance() {
        Scripts fragment = new Scripts();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    Cursor c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v=inflater.inflate(R.layout.fragment_scripts,container,false);
        r=(RecyclerView)v.findViewById(R.id.recycler);
        final RecyclerView rv=(RecyclerView)v.findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        ItemTouchHelper.SimpleCallback touchCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.LEFT){
                    MyViewHolder view=(MyViewHolder)viewHolder;
                    Log.e("Callback:","Swipe Detected");
                    String url="content://DataProvider/data";
                    final int id=viewHolder.getPosition();
                    final String title=view.title.getText().toString();
                    final String data=view.prev.getText().toString();
                    c.moveToPosition(viewHolder.getPosition());
                    Log.e("id:", String.valueOf(view.id));
                    getActivity().getContentResolver().delete(Uri.parse(url),"_id =="+view.id,null);
                    rv.getAdapter().notifyItemRemoved(viewHolder.getPosition());
                    Snackbar.make(v,"Text Removed",Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ContentValues cv=new ContentValues();
                            cv.put("_id",id);
                            cv.put("title",title);
                            cv.put("data",data);
                            String contentUrl="content://DataProvider/data";
                            getActivity().getContentResolver().insert(Uri.parse(contentUrl),cv);
                            rv.getAdapter().notifyItemInserted(id);

                        }
                    }).show();


                }
            }
        };
        ItemTouchHelper ith=new ItemTouchHelper(touchCallback);
        ith.attachToRecyclerView(rv);
        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scripts,parent,false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyViewHolder mv= (MyViewHolder)holder;
                c.moveToPosition(position);
                mv.id=c.getInt(0);
                mv.title.setText(c.getString(1));
                mv.prev.setText(c.getString(2));

            }

            @Override
            public int getItemCount() {
                String u="content://DataProvider";
                String projection[]={"(_id","title","data"};
                Uri reqUri=Uri.parse(u);
                c=getActivity().managedQuery(reqUri,null,null,null,"title");
                if(c==null)
                    return 0;
                return c.getCount();
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, prev;
        public int id;


        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.list_title);
            prev = (TextView) view.findViewById(R.id.list_prev);

        }

        @Override
        public void onClick(View view) {
            c.moveToPosition(getPosition());
            Intent i=new Intent(getActivity(),Script.class);
            i.putExtra("title",c.getString(1));
            i.putExtra("data",c.getString(2));
            i.putExtra("mode","edit");
            i.putExtra("id",c.getInt(0));
            startActivityForResult(i,2);


        }



    }




}
