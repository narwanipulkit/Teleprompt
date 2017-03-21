package pn3.teleprompt;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class Stored extends Fragment {

    RecyclerView rv;

    public Stored() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        rv.getAdapter().notifyDataSetChanged();

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
                mv.title.setText(dir.list()[position]);
            }

            @Override
            public int getItemCount() {
                File dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"teleprompt");

                return dir.list().length;
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
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.list_title);


        }

        @Override
        public void onClick(View view) {

            File dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"teleprompt");
            File f=new File(dir,dir.list()[getPosition()]);


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
