package diplohack.callswede;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sinch.android.rtc.SinchError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import diplohack.callswede.view.OpenDataUrl;

public class AvailableActivity extends BaseActivity implements SnicksnackService.StartFailedListener {

    String status = "0";
    String userId;

    private final String TAG = AvailableActivity.class.getName();
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available);
        activity = this;
        //findViewById(R.id.testbutton).setOnClickListener(testClickListener);

    }

    @Override
    protected void onServiceConnected(){
        getSnicsnackServiceInterface().setStartListener(this);

        final Switch availabilityButton = (Switch) findViewById(R.id.toggleButton);
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        Boolean available = sharedPref.getBoolean("Available", false);
        userId = sharedPref.getString(SnicksnackService.USER_ID, "");
        if(available) {
            availabilityButton.setChecked(available);
            checkOrRequestUserPermission(android.Manifest.permission.RECORD_AUDIO, 1);
            checkOrRequestUserPermission(android.Manifest.permission.MODIFY_AUDIO_SETTINGS, 2);

            if(!getSnicsnackServiceInterface().isStarted() && !userId.isEmpty()) {
                getSnicsnackServiceInterface().startClient(userId);
            }
        }

        findViewById(R.id.reregisterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(availabilityButton.isChecked()) {
                    new AlertDialog.Builder(AvailableActivity.this)
                            .setTitle("Error")
                            .setMessage("Cannot Re-Register while you are available for call")
                            .setIcon(R.drawable.ic_info_outline_red_800_24dp)
                            .setPositiveButton(android.R.string.ok, null).show();

                } else {
                    Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(intent);
                }
            }
        });
        availabilityButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                status = "0";
                if (isChecked) {
                    status = "1";
                }

                SharedPreferences.Editor editor = sharedPref.edit();
                if(status.equals("1")) {
                    editor.putBoolean("Available", true);
                    checkOrRequestUserPermission(android.Manifest.permission.RECORD_AUDIO, 1);
                    if(!getSnicsnackServiceInterface().isStarted() && !userId.isEmpty()) {
                        getSnicsnackServiceInterface().startClient(userId);
                    }
                } else {
                    editor.putBoolean("Available", false);
                    if(getSnicsnackServiceInterface().isStarted()) {
                        getSnicsnackServiceInterface().stopClient();
                    }
                }
                new UpdateStateTask().execute(status);
                editor.commit();

            }
        });
    }

    @Override
    public void onStartFailed(SinchError error){
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted(){

    }


    private class UpdateStateTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AvailableActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Changing state");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params){
            URL url = null;
            try {
                url = new URL(getPushNotificatonUrl(params[0]));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestMethod("POST");
                conn.connect();
                Log.v("State changed: ", conn.getResponseMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        private String getPushNotificatonUrl(String state){

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http");
            uriBuilder.authority(getString(R.string.remote_server));

            uriBuilder.appendPath(getString(R.string.push_notification_path));
            uriBuilder.appendQueryParameter("userid", userId);
            uriBuilder.appendQueryParameter("status", state);
            Log.v(TAG, uriBuilder.toString());
            return uriBuilder.toString();
        }
    }

    private boolean checkOrRequestUserPermission(String permission, int requestCode) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
                return false;
            }
        }

        return true;
    }

    Button.OnClickListener testClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v){
            final SnicksnackApplication snicksnackApplication = (SnicksnackApplication) getApplication();
            new AsyncTask<Void, Void, SpannableStringBuilder>() {
                @Override
                protected SpannableStringBuilder doInBackground(Void... params){
                    InputStream inputStream = null;
                    SpannableStringBuilder funfact = null;
                    try {

                        Integer index = SnicksnackApplication.random.nextInt(4);
                        OpenDataUrl openDataUrl = snicksnackApplication.getOpenDataUrls().get(index);

                        // 10. convert inputstream to string
                        HttpURLConnection conn = openDataUrl.getHttpConnection();
                        int responseCode = conn.getResponseCode();
                        StringBuilder buffer = new StringBuilder();
                        String result = "";
                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while (( line = br.readLine() ) != null) {
                                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                                // But it does make debugging a *lot* easier if you print out the completed
                                // buffer for debugging.
                                buffer.append(line + "\n");
                            }
                            result = buffer.toString();
                        } else {
                            result = "Did not work!";
                        }

                        JSONObject resultObject = new JSONObject(result);

                        if (resultObject.has("data")) {
                            JSONArray dataArray = resultObject.getJSONArray("data");
                            funfact = OpenDataUrl.extractData(dataArray, openDataUrl.getIdentifier(), openDataUrl.getDisplayInfo(), Arrays.asList(openDataUrl.getBoldText()));
                        }


                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }

                    // 11. return result
                    return funfact;
                }

                @Override
                protected void onPostExecute(SpannableStringBuilder aVoid){
                    super.onPostExecute(aVoid);
                    Intent infoIntent = new Intent(activity, InfoActivity.class);
                    infoIntent.putExtra(OpenDataUrl.INFO, aVoid);
                    activity.startActivity(infoIntent);
                }
            }.execute();
        }
    };
}