package pn3.teleprompt;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class Stored extends Fragment {

    RecyclerView rv;
    Loader<Cursor> cursorLoader;

    Cursor c;
    static final int LOADER_ID=3;
    TextView empty;
    public Stored() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        cursorLoader=getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        String url = "content://DataProvider/video";
                        CursorLoader curLoader = new CursorLoader(getContext(), Uri.parse(url), null, null, null, "_id");
                        rv.getAdapter().notifyDataSetChanged();
                        return curLoader;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        c=data;
                        rv.getAdapter().notifyDataSetChanged();

                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                }

        );

    }

    public static Stored newInstance() {
        Stored fragment = new Stored();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_stored,container,false);
        empty=(TextView)v.findViewById(R.id.empty_view);
        rv=(RecyclerView)v.findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);


        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stored,parent,false);
                return new MyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyViewHolder mv= (MyViewHolder)holder;
                File dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"teleprompt");
                mv.f=new File(dir,dir.list()[mv.getPosition()]);
                Bitmap b= ThumbnailUtils.createVideoThumbnail(mv.f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                mv.thumbnail.setImageBitmap(b);
                mv.title.setText(dir.list()[position]);
                if(getItemCount()==0){
                    mv.empty.setVisibility(View.VISIBLE);
                }
                String url="content://DataProvider/video";
                if(position>=0) {
                    c = getActivity().getContentResolver().query(Uri.parse(url),null,"title=?",new String[]{dir.list()[position]},null);
                    if(c.getCount()>0) {
                        c.moveToFirst();
                        mv.place.setText(c.getString(2));
                        mv.date.setText(c.getString(3));
                    }
                }



            }

            @Override
            public int getItemCount() {
                File dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"teleprompt");
                if(dir.list()!=null) {
                    if (dir.list().length == 0) {
                        empty.setVisibility(View.VISIBLE);

                    }
                    else{
                        empty.setVisibility(View.GONE);

                    }
                    return dir.list().length;
                }
                else {
                    empty.setVisibility(View.VISIBLE);
                    return 0;
                }
            }
        });

        cursorLoader=getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        String url = "content://DataProvider/video";
                        CursorLoader curLoader = new CursorLoader(getContext(), Uri.parse(url), null, null, null, "_id");
                        rv.getAdapter().notifyDataSetChanged();
                        return curLoader;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        c=data;
                        rv.getAdapter().notifyDataSetChanged();

                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                }

        );


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
        public TextView title,place,date,empty;
        public ImageView thumbnail;
        public File f;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);



            title = (TextView) view.findViewById(R.id.list_title);
            place=(TextView)view.findViewById(R.id.place);
            date=(TextView)view.findViewById(R.id.date);
            empty=(TextView)view.findViewById(R.id.empty_view);
            thumbnail=(ImageView)view.findViewById(R.id.thumb_img);


        }

        @Override
        public void onClick(View view) {



            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension("mp4");
            newIntent.setDataAndType(Uri.fromFile(f),mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                getContext().startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }


        }
    }


}
