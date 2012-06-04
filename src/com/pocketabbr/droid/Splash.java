package com.pocketabbr.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Splash extends Activity{

	ImageView splash;
	
	protected boolean splashactive = true;
	protected int splashTime = 2500;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        splash=(ImageView)findViewById(R.id.splash);
        splash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				splashactive = false;
			}
		});
        splash.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				splashactive = false;
				return false;
			}
		});
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(splashactive && (waited < splashTime)) {
                        sleep(100);
                        if(splashactive) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                } finally {
                    finish();
                    Intent intent = new Intent(Splash.this, PocketAbbreviationsDroidActivity.class);
    		        startActivity(intent);
                    stop();
                }
            }
        };
        splashTread.start();
        
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        splashactive = false;
	    }
	    return true;
	}
}
