package diplohack.callswede;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import diplohack.callswede.view.MultiSelectionSpinner;
import diplohack.callswede.view.gcm.gcm;

public class RegistrationActivity extends BaseActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener{

    String callerType = "Learner";

    Button registerButton;

    private final Integer LOCATION_REQUEST = 1;

    private final String TAG = RegistrationActivity.class.getName();

    private TextView countryName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        countryName = (TextView) findViewById(R.id.country_name);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String caller = sharedPref.getString("callerType", "");
        if(!caller.isEmpty()) {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogrp);
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (radioGroup.getChildAt(i) instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    if(radioButton.getText().toString().equals(caller)){
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }
        }

        if(!checkOrRequestUserPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !checkOrRequestUserPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        } else {
            String country_name = null;
            LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Geocoder geocoder = new Geocoder(getApplicationContext());
            for(String provider: lm.getAllProviders()) {
                @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
                if(location!=null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if(addresses != null && addresses.size() > 0) {
                            country_name = addresses.get(0).getCountryName();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            countryName.setText(country_name);
        }
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(registerClickListener);
        String[] array = {"Arabic", "Dari", "English",  "Parsi",  "Somali", "Dutch", "Danish", "Finnish", "German", "Portuguese", "French", "Italian", "Polish", "Spanish"};
        List<String> languages = Arrays.asList(array);
        Collections.sort(languages, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs){
                return lhs.compareTo(rhs);
            }
        });
        MultiSelectionSpinner multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.language_selection);
        multiSelectionSpinner.setItems(languages);
        multiSelectionSpinner.setSelection(new int[]{0});
        multiSelectionSpinner.setListener(this);

    }

    public void onRadioButtonClicked(View view){
        RadioButton radioButton = (RadioButton) view;
        Boolean checked = radioButton.isChecked();
        switch (radioButton.getText().toString()) {
            case "Learner":
                if (checked) {
                    callerType = "Learner";
                    registerButton.setEnabled(true);
                }
                break;
            case "Native":
                if (checked) {
                    callerType = "Native";
                    registerButton.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices){

    }

    @Override
    public void selectedStrings(List<String> strings){

    }

    Button.OnClickListener registerClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v){
            new AsyncTask<String, Void, String>(){
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(RegistrationActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Registering");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                protected String doInBackground(String... params) {

                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    StringBuffer buffer = new StringBuffer();
                    URL url = null;
                    try {
                        String urlString = getRegisterSnicksnackUrl(getJSONObject(params[0]));
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");

                        urlConnection.connect();
                        InputStream inputStream = urlConnection.getInputStream();

                        if (inputStream == null) {
                            Log.v(TAG, "no result");
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                            // But it does make debugging a *lot* easier if you print out the completed
                            // buffer for debugging.
                            buffer.append(line + "\n");
                        }

                        Log.v(TAG, buffer.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return buffer.toString();
                }

                private JSONObject getJSONObject(String param) throws JSONException{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("regid", gcm.INSTANCE.getRegID());
                    jsonObject.put("caller_type", param);
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
                    jsonObject.put("user_id", sharedPref.getString(SnicksnackService.USER_ID, ""));
                    return jsonObject;

                }

                private String getRegisterSnicksnackUrl(JSONObject param){

                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.scheme("http");
                    uriBuilder.authority(RegistrationActivity.this.getString(R.string.remote_server));

                    uriBuilder.appendPath(RegistrationActivity.this.getString(R.string.register_snicksnack_path));

                    uriBuilder.appendQueryParameter("json", param.toString());

                    return uriBuilder.toString();
                }

                @Override
                protected void onPostExecute(String result){
                    super.onPostExecute(result);

                    if (!result.isEmpty()) {
                        try {
                            JSONObject resultObject = new JSONObject(result);
                            JSONObject responseObject = resultObject.getJSONObject("response");
                            if (responseObject.has("userId")) {
                                String userId = responseObject.getString("userId");
                                if (!getSnicsnackServiceInterface().isStarted()) {
                                    getSnicsnackServiceInterface().startClient(userId);
                                }

                                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("register", true);
                                editor.putString("callerType", callerType);
                                editor.putString(SnicksnackService.USER_ID, userId);
                                editor.commit();
                                Intent intent = new Intent(RegistrationActivity.this, AvailableActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            }.execute(callerType);
        }

    };

    private boolean checkOrRequestUserPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }

        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST){
            boolean permissionGranted = true;
            for(int i=0; i<grantResults.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionGranted = false;
                    break;
                }
            }

            if(permissionGranted){
                String country_name = null;
                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                Geocoder geocoder = new Geocoder(getApplicationContext());
                for(String provider: lm.getAllProviders()) {
                    @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
                    if(location!=null) {
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if(addresses != null && addresses.size() > 0) {
                                country_name = addresses.get(0).getCountryName();
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                countryName.setText(country_name);
            }
        }
    }
}
