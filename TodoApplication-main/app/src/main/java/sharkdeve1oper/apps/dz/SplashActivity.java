package sharkdeve1oper.apps.dz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Calendar;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView greetingTextView = findViewById(R.id.greetingTextView);

        // Display greeting based on the time of day
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12) {
            greetingTextView.setText("Доброе утро");
        } else if (hour >= 12 && hour < 17) {
            greetingTextView.setText("Добрый день");
        } else if (hour >= 18 && hour < 24) {
            greetingTextView.setText("Добрый вечер");
        } else {
            greetingTextView.setText("Доброй ночи");
        }

        // Start main activity after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
