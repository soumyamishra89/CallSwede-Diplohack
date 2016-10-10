package diplohack.callswede;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import diplohack.callswede.view.OpenDataUrl;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        final Activity activity = this;
        findViewById(R.id.reportButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast msg = Toast.makeText(getApplicationContext(), "Your report has been registered", Toast.LENGTH_LONG);
                msg.setGravity(Gravity.CENTER, 0, 0);
                msg.show();

                CharSequence info = getIntent().getExtras().getCharSequence(OpenDataUrl.INFO, "");
                Intent infoIntent = new Intent(activity, InfoActivity.class);
                infoIntent.putExtra(OpenDataUrl.INFO, info);
                startActivity(infoIntent);
                finish();
            }
        });
    }
}
