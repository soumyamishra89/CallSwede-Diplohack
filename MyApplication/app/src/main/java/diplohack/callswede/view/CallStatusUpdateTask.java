package diplohack.callswede.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;

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

import diplohack.callswede.InfoActivity;
import diplohack.callswede.R;
import diplohack.callswede.ReportActivity;
import diplohack.callswede.SnicksnackApplication;
import diplohack.callswede.SnicksnackService;

/**
 * Created by SOMU on 28/05/16.
 */
public class CallStatusUpdateTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    boolean showReportAlert;

    private final String TAG = CallStatusUpdateTask.class.getName();

    SpannableStringBuilder funfact = null;

    public CallStatusUpdateTask(Activity activity, boolean showReportAlert){
        this.activity = activity;
        this.showReportAlert = showReportAlert;
    }

    @Override
    protected String doInBackground(Void... params){
        URL url = null;
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.app_name), Activity.MODE_PRIVATE);
        String userId = sharedPref.getString(SnicksnackService.USER_ID, "");
        if (!userId.isEmpty()) {
            try {
                url = new URL(getPushNotificatonUrl(userId));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestMethod("POST");
                conn.connect();
                Log.v("State changed: ", conn.getResponseMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private String getPushNotificatonUrl(String userid){

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http");
        uriBuilder.authority(activity.getString(R.string.remote_server));

        uriBuilder.appendPath(activity.getString(R.string.call_status_update_path));

        uriBuilder.appendQueryParameter("userid", userid);

        return uriBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);

        if (showReportAlert) {
            final SnicksnackApplication snicksnackApplication = (SnicksnackApplication) activity.getApplication();
            new AsyncTask<Void, Void, SpannableStringBuilder>() {

                private ProgressDialog progressDialog;
                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Call Ending...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                protected SpannableStringBuilder doInBackground(Void... params){
                    InputStream inputStream = null;

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
                        Log.v(TAG, result);

                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }

                    // 11. return result
                    return funfact;
                }

                @Override
                protected void onPostExecute(SpannableStringBuilder s){
                    super.onPostExecute(s);
                    progressDialog.dismiss();
                    new AlertDialog.Builder(activity)
                            .setTitle("Report")
                            .setMessage("thanks! are you satisfied with the call?")
                            .setIcon(R.drawable.ic_info_outline_red_800_24dp)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if (funfact!=null) {
                                        Intent infoIntent = new Intent(activity, InfoActivity.class);
                                        infoIntent.putExtra(OpenDataUrl.INFO, funfact);
                                        activity.startActivity(infoIntent);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    Intent reportIntent = new Intent(activity, ReportActivity.class);
                                    reportIntent.putExtra(OpenDataUrl.INFO, funfact);
                                    activity.startActivity(reportIntent);
                                    dialog.dismiss();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog){
                                    activity.finish();
                                }

                            }).show();
                }
            }.execute();

        } else {
            activity.finish();
        }
    }
}
