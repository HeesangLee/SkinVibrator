package hs.app.skinvibrator;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
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
    
	@Override
	public void onBackPressed(){
		if (Math.random()<0.8){
			popUpExtiMessageDlg();
		}else{
			popUpAdMsgDlg();
		}
	}
	
	private void popUpExtiMessageDlg(){
		AlertDialog.Builder dlgBackPressed = new AlertDialog.Builder(this);
		dlgBackPressed.setMessage(R.string.say_good_bye)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		.setTitle("Exit")
		.setIcon(R.drawable.ic_launcher)
		.show();
	}
	private void popUpAdMsgDlg(){
		AlertDialog.Builder dlgBackPressed = new AlertDialog.Builder(this);
		dlgBackPressed.setMessage(R.string.advertize_myself)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				try{
					startActivity(
							new Intent(Intent.ACTION_VIEW,
									Uri.parse("market://search/?q=pub:Dalcoms")));
				}catch(android.content.ActivityNotFoundException e){
					startActivity(
							new Intent(Intent.ACTION_VIEW,
									Uri.parse("https://play.google.com/store/search?q=dalcoms")));
				}
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				popUpExtiMessageDlg();
			}
		})
		.setTitle("Free app")
		.setIcon(R.drawable.ic_launcher)
		.show();
	}
    
}
