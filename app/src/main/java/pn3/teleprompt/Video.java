package pn3.teleprompt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurgle.camerakit.Camera1;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Video extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /**
             mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
             | View.SYSTEM_UI_FLAG_FULLSCREEN
             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
             | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
             | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
             **/

        }
    };
    private View mControlsView;

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();

    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    CameraView camera;
    ScrollView sv;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Bundle mBundle = getIntent().getExtras();
        String data = mBundle.getString("data");
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        TextView d = (TextView) findViewById(R.id.data);
        sv = (ScrollView) findViewById(R.id.scroll_data);
        d.setText(data);
        camera = (CameraView) findViewById(R.id.camera);
        camera.start();
        camera.setFocus(CameraKit.Constants.FOCUS_TAP);
        camera.setFacing(CameraKit.Constants.FACING_FRONT);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        final Button b = (Button) findViewById(R.id.record);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (b.getText().toString().equals("R")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        b.setBackground(getDrawable(R.drawable.square));
                    }
                    b.setText("S");
                    camera.startRecordingVideo();
                    startScroll();
                } else {
                    b.setText("R");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        b.setBackground(getDrawable(R.drawable.round_button));
                    }
                    camera.setCameraListener(new CameraListener() {
                        @Override
                        public void onVideoTaken(File video) {
                            super.onVideoTaken(video);
                            Log.e("Video", "Taken");
                            if (isExternalStorageWritable()) {
                                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "teleprompt");
                                if (!dir.mkdirs()) {
                                    Log.e("Video" + video.getUsableSpace(), "Directory Not Created");
                                }
                                Date d = new Date();
                                String date=d.toString();
                                String filename="video" + String.valueOf(d.getDate()) + String.valueOf(d.getTime())+ ".mp4";
                                video.renameTo(new File(dir, filename));



                                saveDataAndPlace(filename,date);




                            }
                            else{
                                Snackbar.make(findViewById(R.id.vid_layout),"Error Writing The Video",Snackbar.LENGTH_LONG).show();
                            }

                        }
                    });
                    stopScroll();
                    camera.stopRecordingVideo();

                }




            }
        });
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    final Handler h=new Handler();
    Runnable runner=new Runnable() {
        @Override
        public void run() {
            sv.smoothScrollBy(0,3);
            h.postDelayed(this,30);
        }
    };

    public void saveDataAndPlace(final String filename, final String date){
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getParent(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                PlaceLikelihood max=likelyPlaces.get(0);
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if(placeLikelihood.getLikelihood()>max.getLikelihood()){
                        max=placeLikelihood;
                    }
                }
                Log.e("Place:", String.valueOf(max.getPlace().getName()));
                ContentValues cv=new ContentValues();
                cv.put("title",filename);
                cv.put("place",String.valueOf(max.getPlace().getName()));
                cv.put("date",date);
                String url="content://DataProvider/video";
                getContentResolver().insert(Uri.parse(url),cv);
                likelyPlaces.release();
            }
        });

    }

    public void stopScroll(){
        h.removeCallbacks(runner);

    }


    public void startScroll(){

        h.postDelayed(runner,30);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
