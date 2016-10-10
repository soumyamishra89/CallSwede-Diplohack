package diplohack.callswede;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import diplohack.callswede.view.gcm.gcm;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gcm.INSTANCE.setActivity(this);
        gcm.INSTANCE.registerId();

        //setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

                Boolean registered = sharedPref.getBoolean("register", false);
                if(registered) {
                    Intent intent = new Intent(SplashActivity.this, AvailableActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);
    }
}
