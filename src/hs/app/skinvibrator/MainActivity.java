package hs.app.skinvibrator;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdView;


public class MainActivity extends Activity {
	private AdView adView;
	VibSelectView vibView;
	static final boolean AD_ON = true;
//	static final boolean AD_ON = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        setContentView(R.layout.activity_main);
        
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        vibView = new VibSelectView(this);
        mainLayout.addView(vibView);
        
        if (AD_ON) {
        	AdRequest adrequest = new AdRequest();
        	adrequest.setGender(AdRequest.Gender.FEMALE);
        	adView = (AdView) findViewById(R.id.adView);
            adView.loadAd(adrequest);
        }
        startFrameAnim();
    }
    
    protected void startFrameAnim(){
		ImageView ivAnimTitle=(ImageView)findViewById(R.id.iv_anim);
		ivAnimTitle.setBackgroundResource(R.drawable.anim_top);
		AnimationDrawable frameAnim = (AnimationDrawable) ivAnimTitle.getBackground();
		frameAnim.start();
	}

    @Override
    public void onDestroy(){
    	if (adView != null){
    		adView.destroy();
    	}
    	vibView.vibratorCancel();
    	super.onDestroy();
    }
    
    @Override
    public void onPause(){
    	vibView.vibratorCancel();
    	super.onPause();
    }
    
    @Override
    public void onResume(){
    	vibView.vibratroResume();
    	super.onResume();
    }
}
