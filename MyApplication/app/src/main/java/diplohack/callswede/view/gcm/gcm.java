package diplohack.callswede.view.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import diplohack.callswede.R;

public class gcm {

    public static final gcm INSTANCE = new gcm();
    private Activity activity;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static String TAG = gcm.class.getName();
    protected String SENDER_ID = "1040852833140";
    private GoogleCloudMessaging gcm = null;
    private String regid = null;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private gcm() {
    }

    public void registerId() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }

        }
    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported - Google Play Services.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(){
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration ID not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(activity);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(){
        return activity.getSharedPreferences(activity.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params){
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.d(TAG, "########################################");
                    Log.d(TAG, "Current Device's Registration ID is: " + regid);
                    //send_server(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d(TAG, msg);
                }
                return regid;
            }     //protected void onPostExecute(Object result);
            @Override
            protected void onPostExecute(String s){

                if(s != null) {
                    SharedPreferences sharedPref = getGCMPreferences();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("registration_id", s);
                    editor.commit();
                }
            }

        }.execute();
    }

    public String getRegID(){
        return regid;
    }

    public void send_server(String regID) {
        String link = getGCMRegisterUrl(regID);//"http://itsallaboutyour.space/registergcmid.php?id=" + regID + "&uid=" + user_id + "&username=" + user_name;
        //String link = "http://itsallaboutyour.space/get_RegID.php?id=febirjgbwjkbg34"+"&at="+User_id;

        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("POST");
            conn.connect();

            Log.v("RESPONSE: ", conn.getResponseMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getGCMRegisterUrl(String regID){

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http");
        uriBuilder.authority("itsallaboutyour.space");

        uriBuilder.appendPath("nativespeakerregister.php");

        uriBuilder.appendQueryParameter("id", regID);
        return uriBuilder.toString();
    }
}
