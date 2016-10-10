package diplohack.callswede;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import diplohack.callswede.view.OpenDataUrl;

public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView infoView = (TextView) findViewById(R.id.infoText);
        CharSequence info = getIntent().getExtras().getCharSequence(OpenDataUrl.INFO, "");
        if(info.length()!=0) {
            infoView.setText(info);
        }

        final Activity activity = this;
        findViewById(R.id.okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity, AvailableActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
