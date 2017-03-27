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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;


public class Scripts extends Fragment  {


    String to;
    int first=0;
    static final int LOADER_ID=1;
    RecyclerView r;
    Loader<Cursor> cursorLoader;

    @Override
    public void onResume() {
        super.onResume();

        if(first==1) {
            Log.e("OnRecieve","first==1");
            cursorLoader = getLoaderManager().restartLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String url = "content://DataProvider/data";
                    CursorLoader curLoader = new CursorLoader(getContext(), Uri.parse(url), null, null, null, "_id");
                    r.getAdapter().notifyDataSetChanged();
                    return curLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    c=data;
                    r.getAdapter().notifyDataSetChanged();

                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            });
        }




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



        String[] columns={"data","title","data"};
        int[] views={R.id.list_title,R.id.list_prev};
        cursorLoader=getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String url="content://DataProvider/data";
                first=1;
                CursorLoader curLoader=new CursorLoader(getContext(),Uri.parse(url),null,null,null,"_id");
                return curLoader;

            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                c=data;
                r.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
        SimpleCursorAdapter simpleCursorAdapter=new SimpleCursorAdapter(getContext(),R.layout.fragment_scripts,null,columns,views,0);





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
                    cursorLoader=getLoaderManager().restartLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                        @Override
                        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                            String url = "content://DataProvider/data";
                            CursorLoader curLoader = new CursorLoader(getContext(), Uri.parse(url), null, null, null, "_id");
                            r.getAdapter().notifyDataSetChanged();
                            return curLoader;
                        }

                        @Override
                        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                            c=data;
                            r.getAdapter().notifyDataSetChanged();

                        }

                        @Override
                        public void onLoaderReset(Loader<Cursor> loader) {

                        }
                    });


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
                if(getItemCount()==0){
                    mv.empty.setVisibility(View.VISIBLE);
                }
                c.moveToPosition(position);
                mv.id=c.getInt(0);
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
        public TextView title, prev,empty;
        public int id;


        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.list_title);
            prev = (TextView) view.findViewById(R.id.list_prev);
            empty=(TextView)view.findViewById(R.id.empty_view);

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
