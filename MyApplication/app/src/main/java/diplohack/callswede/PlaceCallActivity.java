package diplohack.callswede;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

public class PlaceCallActivity extends BaseActivity {

    private FloatingActionButton mCallButton;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mCallButton = (FloatingActionButton) findViewById(R.id.callStart);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);

        /*Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);*/
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        userName = intent.getExtras().getString(SnicksnackService.RECIPIENT_ID, "");
    }

    @Override
    protected void onServiceConnected() {

        mCallButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSnicsnackServiceInterface() != null) {
            getSnicsnackServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        userName = getIntent().getExtras().getString(SnicksnackService.RECIPIENT_ID, "");

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = getSnicsnackServiceInterface().callUser(userName);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SnicksnackService.CALL_ID, callId);
            startActivity(callScreen);
            finish();
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callStart:
                    callButtonClicked();
                    break;

                /*case R.id.stopButton:
                    stopButtonClicked();
                    break;*/

            }
        }
    };
}
