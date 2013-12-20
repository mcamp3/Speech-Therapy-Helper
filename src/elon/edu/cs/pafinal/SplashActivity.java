package elon.edu.cs.pafinal;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {
	
	//number is in milliseconds so 1000 would be 1 second
	private final int SPLASH_DISPLAY_LENGTH = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
             public void run() {

                Intent intent = new Intent(SplashActivity.this, AssignmentsActivity.class);
                startActivity(intent);
                finish();
             }

         }, SPLASH_DISPLAY_LENGTH);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
