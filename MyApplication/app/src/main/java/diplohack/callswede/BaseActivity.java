package diplohack.callswede;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private SnicksnackService.SnicsnackServiceInterface mSnicsnackServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SnicksnackService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SnicksnackService.class.getName().equals(componentName.getClassName())) {
            mSnicsnackServiceInterface = (SnicksnackService.SnicsnackServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SnicksnackService.class.getName().equals(componentName.getClassName())) {
            mSnicsnackServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SnicksnackService.SnicsnackServiceInterface getSnicsnackServiceInterface() {
        return mSnicsnackServiceInterface;
    }

}
