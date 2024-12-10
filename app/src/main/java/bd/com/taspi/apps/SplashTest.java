package bd.com.taspi.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_test);
        Activity activity = this;


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finishAffinity();
                System.exit((int) 0);
            }
        }, 300);
    }
}