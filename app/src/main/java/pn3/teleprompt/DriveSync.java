package pn3.teleprompt;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.events.ChangeListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class DriveSync extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    final int RESOLVE_CONNECTION_REQUEST_CODE=1;

    ProgressBar progress;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        progress=(ProgressBar)findViewById(R.id.progress);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { "text/plain", "text/html" })
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(
                    intentSender, 2, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w("err", "Unable to send intent", e);
        }


    }






    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(data!=null) {
            DriveId driveId = data.getParcelableExtra(
                    OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            new RetrieveDriveFileContentsAsyncTask().execute(driveId);
        }
        finish();

    }

    class RetrieveDriveFileContentsAsyncTask extends AsyncTask<DriveId,Boolean,String>{

        @Override
        protected String doInBackground(DriveId... driveIds) {

            if(driveIds[0]!=null) {
                DriveFile df = driveIds[0].asDriveFile();
                DriveApi.DriveContentsResult res = df.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
                DriveContents con = res.getDriveContents();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String a;
                String result = new String();
                try {
                    while ((a = br.readLine()) != null) {
                        result += a;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return result;
            }
            else{
                return "nf";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("nf")){
                Toast.makeText(getBaseContext(),"Error Connecting",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                progress.setVisibility(View.GONE);
                Intent i = new Intent(DriveSync.this, Script.class);
                i.putExtra("title", "");
                i.putExtra("data", s);
                i.putExtra("mode", "new");
                startActivity(i);
            }


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

}

