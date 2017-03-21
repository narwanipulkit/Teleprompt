package pn3.teleprompt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class New extends Fragment implements View.OnClickListener {

    View vid,txt;
    FrameLayout container;


//    OnFragmentInteractionListener mListener;

    public New() {
        // Required empty public constructor
    }

    public static New newInstance() {
        New fragment = new New();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_new, container, false);
        vid=v.findViewById(R.id.video_card);
        txt=v.findViewById(R.id.text_card);
        container=(FrameLayout)v.findViewById(R.id.container);
        vid.setOnClickListener(this);
        txt.setOnClickListener(this);
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /**
        if (context instanceof OnFragmentInteractionListener) {
             mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
         **/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.video_card:
                i=new Intent(getActivity(),SelectScript.class);
                i.putExtra("from","New");
                i.putExtra("to","Video");
                startActivity(i);
                break;
            case R.id.text_card:
                i=new Intent(getActivity(),SelectScript.class);
                i.putExtra("from","New");
                i.putExtra("to","Text");
                startActivity(i);
                break;
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

     */
}
