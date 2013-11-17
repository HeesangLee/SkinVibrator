package hs.app.skinvibrator;

import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.lang.Object;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
//import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Display;
//import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class VibSelectView extends View{
	private boolean flag_touch_on = false;
	private boolean flag_touch_handle_on = false;
	private Bitmap img_background = null;
	private Bitmap img_dial = null;
	private Bitmap img_off_icon = null;
	private Bitmap img_max_icon = null;
	private Bitmap img_dial_handle = null;
	private Bitmap img_levelView = null;
	private Bitmap img_title = null;
	private Bitmap img_touchOnLight = null;
	private Bitmap img_handle_touch = null;
	private Bitmap img_sat = null;
	private Bitmap img_num_stop = null;
	private Bitmap img_num_max = null;
//	private Bitmap img_move = null;
	private ArrayList<Bitmap> btNumArray = new ArrayList<Bitmap>();
	
	private int display_width;
	private int display_height;
	private int admob_y_offset;
	
	private double off_angle = 90+45;
	private double max_angle = 90-45;
	
	private double dial_handle_angleDegree = 90+45;
	private double dial_handle_angle_before;
	private double dial_handle_radius;
	Point dial_handle_size = new Point();
	
	Point dial_center=new Point();
	
	private int vib_level=0;
	private Point ofsLevel = new Point();
	
	private int level_num_width = 0;
//	private int max_volume = 0;
	
	Vibrator vibe =null;
	SoundPool mSoundPool = null;
	int spId_init;
	int spId_dial;
	
	private int flag_toastView = 0;
	boolean flag_vibe_cancled = false;
	int tVibLevel =0;
	int ani_x=0;
	
	public class vibePattern{
		final static long step_msec = 200;
		final static long minOff_msec = 5;//max_on = step_msec - minOff_msec
		final static long minOn_msec = 5;
		
//		final private int[] stepDivide = {6,10,15,22,22,15,10};
		final private int[] stepDivide = {22,22,15,10,6,10,15};
		
		public vibePattern(){
			
		}
		public long[] getPattern(int vibeLevel){
			int[] stepTimes = {0,0,0,0,0,0,0};
			long[] pattern_array = new long[stepDivide.length*2];
			
			for(int i=0;i<vibeLevel;i++){
				if(stepTimes[i%stepTimes.length]<stepDivide[i%stepTimes.length]){
					stepTimes[i%stepTimes.length]+=1;
				}else{
					for(int ii=0;ii<stepTimes.length;ii++){
						if(stepTimes[ii]<stepDivide[ii]){
							stepTimes[ii] += 1;
							break;
						}
					}
				}
			}
			
			for(int k=0;k<stepTimes.length;k++){
				pattern_array[k*2+1] = minOn_msec+((stepTimes[k]*(step_msec-minOff_msec-minOn_msec))/stepDivide[k]);//#on
				pattern_array[k*2] = step_msec -pattern_array[k*2+1];//off
			}
			
			return pattern_array;
		}
		public long getTest(int vibeLevel){
			return (long)vibeLevel;
		}
	}
	
	vibePattern vp = new vibePattern();
	
	public class clsSoundKind{
		final static int welcom = 0;
		final static int dial = 1;
	}
	
	public VibSelectView(Context context) {
		super(context);
		setupDisplaySize(context);
		importImage(context);
		vibe = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//		max_volume = getMaxVolume(context);
		init_soundPool(context);
//		playSound(clsSoundKind.welcom);
//		if (vibe.hasVibrator()){
//			Toast.makeText(getContext(), R.string.hasVib, Toast.LENGTH_SHORT).show();
//		}else{
//			Toast.makeText(getContext(), R.string.hasNoVib, Toast.LENGTH_SHORT).show();

		Toast.makeText(getContext(), R.string.hasVib, Toast.LENGTH_SHORT).show();
	}

	public void vibratorCancel(){
		vibe.cancel();
	}
	
	public void vibratroResume(){
		if(vib_level==0){
 			vibe.cancel();
 		}else{
 			vibe.vibrate(vp.getPattern(vib_level),0);
 		}
	}
	protected int getMaxVolume(Context context){
		int maxVol=0;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		return maxVol;
	}
	protected void init_soundPool(Context context){
		mSoundPool = new SoundPool(16,AudioManager.STREAM_MUSIC,0);
		spId_init = mSoundPool.load(context, R.raw.typing,1);
		spId_dial = mSoundPool.load(context, R.raw.togglebuttonon,1);
	}
	protected void playSound(int soundKind){
		switch(soundKind){
		case clsSoundKind.welcom:
			mSoundPool.play(spId_init, 0.99f, 0.99f, 1, 0, 1);
			break;
		
		case clsSoundKind.dial:
			mSoundPool.play(spId_dial, 0.99f, 0.99f, 1, 0, 1);
			break;
		}
		

	}
	protected Bitmap getFixedImage(){
		Bitmap img_fixed = Bitmap.createScaledBitmap(img_background, display_width, display_height, true);
		return img_fixed;
	}
	
	protected void importImage(Context context){
		img_background = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_bg);
		img_dial = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_dial);
		img_off_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_off_icon);
		img_max_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_max_icon);
		img_dial_handle = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_dial_handle);
		img_title = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_title);
		img_touchOnLight = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_touch_on_light);
		img_levelView = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_levelview);
		img_handle_touch = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_handle_touch);
		img_sat = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_saturate);
		img_num_stop = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_num_stop);
		img_num_max = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_num_max);
//		img_move = BitmapFactory.decodeResource(context.getResources(), R.drawable.imge_move);
		
		for(int i=0;i<10;i++){//load image number.
			String resName = "@drawable/image_num_"+i;
			String packName = context.getPackageName();
			int resId = getResources().getIdentifier(resName, "drawable", packName);
			btNumArray.add(BitmapFactory.decodeResource(context.getResources(), resId));
		}
		ofsLevel.y = (int)((img_levelView.getHeight()-btNumArray.get(4).getHeight())/2);
		ofsLevel.x = (int)(img_levelView.getWidth()-btNumArray.get(4).getWidth()*3)/2;
		level_num_width = btNumArray.get(4).getWidth();
	}
	
	protected void setupDisplaySize(Context context){
		Display display = ((WindowManager)context.getSystemService
							(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		display_height = display.getHeight();
		display_width = display.getWidth();
		dial_handle_radius = display_width*0.22;
		admob_y_offset = display_height/11;
	}
	
	
	protected Bitmap resizeBitmapPerScreen(Bitmap inBitmap,double resizeFactor){
		Bitmap outBitmap = null;
		int re_width = 0;
		int re_height = 0;
		double re_factor = 0;
		
		re_width = (int)((double)display_width * resizeFactor);
		re_factor = (double)re_width/(double)inBitmap.getWidth();
		re_height = (int)((double)inBitmap.getHeight() * re_factor);
		
		outBitmap = Bitmap.createScaledBitmap(inBitmap, re_width, re_height, true);
		return outBitmap;
	}
	
	protected Bitmap resizeBitmap(Bitmap inBitmap,double resizeFactor){
		Bitmap outBitmap = null;
		int re_width = 0;
		int re_height = 0;
		
		re_width = (int)((double)inBitmap.getWidth() * resizeFactor);
		re_height = (int)((double)inBitmap.getHeight() * resizeFactor);
		
		outBitmap = Bitmap.createScaledBitmap(inBitmap, re_width, re_height, true);
		return outBitmap;
	}
	
	protected Point getImgXy(Bitmap inBitmap,int screen_x,int screen_y){
		Point xy = new Point();
		if(screen_x>inBitmap.getWidth()/2){
			xy.x = screen_x-inBitmap.getWidth()/2;
		}else{
			xy.x = 0;
		}
		
		if(screen_y>inBitmap.getHeight()/2){
			xy.y = screen_y-inBitmap.getHeight()/2;
		}else{
			xy.y = 0;
		}
		
		return xy;
	}
	
	protected Point getImgAngularXy(Bitmap inBitmap,int center_x,int center_y,double radius,double degreeAngle){
		Point xy = new Point();
		double deg2rad = Math.toRadians(degreeAngle);
		xy.x = (int)(center_x+radius*Math.cos(deg2rad))-inBitmap.getWidth()/2;
		xy.y = (int)(center_y+radius*Math.sin(deg2rad))-inBitmap.getHeight()/2;

		return xy;
	}
	
	protected int[] getDigitNum(int currentLevel){
		int[] retVal = new int[]{0,0,0};
		retVal[0] = (int)(currentLevel%10);
		retVal[1] = (int)((currentLevel/10)%10);
		retVal[2] = (int)(currentLevel/100);
		
		return retVal;		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap tempBitmap = null;
		Point tempxy;
		Point tempLevelXy;
		int level_width;
		int[] levelDigit = new int[3];
		System.arraycopy(getDigitNum(vib_level), 0, levelDigit, 0, 3);

		
		double reSizeFactor=0;
		
		dial_center.set(display_width/2,display_height*4/7-admob_y_offset);
		
		canvas.drawBitmap(getFixedImage(), 0,0,null);
		//img_dial
		tempBitmap = resizeBitmapPerScreen(img_dial,1);
		reSizeFactor = (double)tempBitmap.getWidth()/(double)img_dial.getWidth();
		tempxy = getImgXy(tempBitmap, dial_center.x, dial_center.y);
		canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);

		//img_title
		tempBitmap = resizeBitmap(img_title,reSizeFactor);
		tempxy = getImgXy(tempBitmap, display_width/2, (int)display_height*9/10-admob_y_offset);
		canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		
		//img_levelView
		tempBitmap = resizeBitmap(img_levelView,reSizeFactor);
		tempLevelXy = getImgXy(tempBitmap, display_width-tempBitmap.getWidth()/2, (int)display_height*3/11-admob_y_offset);
		canvas.drawBitmap(tempBitmap, tempLevelXy.x, tempLevelXy.y,null);
		level_width=tempBitmap.getWidth();
		
		if(vib_level==0){
			tempBitmap = resizeBitmap(img_sat,reSizeFactor);
			tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,display_width*0.48,off_angle);
			canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
			
			tempBitmap = resizeBitmap(img_num_stop,reSizeFactor);
			canvas.drawBitmap(tempBitmap, 
					(float) (tempLevelXy.x+level_width-ofsLevel.x*reSizeFactor-tempBitmap.getWidth()), 
					(float) (tempLevelXy.y+ofsLevel.y*reSizeFactor),null);
		}else if(vib_level<100){
			tempBitmap = resizeBitmap(btNumArray.get(levelDigit[0]),reSizeFactor);
			canvas.drawBitmap(tempBitmap, 
					(float) (tempLevelXy.x+level_width-ofsLevel.x*reSizeFactor-level_num_width*reSizeFactor), 
					(float) (tempLevelXy.y+ofsLevel.y*reSizeFactor),null);
		}
		if(vib_level>99){//100%
			tempBitmap = resizeBitmap(img_num_max,reSizeFactor);
			canvas.drawBitmap(tempBitmap, 
					(float) (tempLevelXy.x+level_width-ofsLevel.x*reSizeFactor-tempBitmap.getWidth()), 
					(float) (tempLevelXy.y+ofsLevel.y*reSizeFactor),null);
			
			tempBitmap = resizeBitmap(img_sat,reSizeFactor);
			tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,display_width*0.48,max_angle);
			canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		}else if (vib_level>9){//display digit 10
			tempBitmap = resizeBitmap(btNumArray.get(levelDigit[1]),reSizeFactor);
			canvas.drawBitmap(tempBitmap, 
					(float) (tempLevelXy.x+level_width-ofsLevel.x*reSizeFactor-level_num_width*2*reSizeFactor), 
					(float) (tempLevelXy.y+ofsLevel.y*reSizeFactor),null);
		}
		
		
		
		//img_off_icon
		tempBitmap = resizeBitmap(img_off_icon,reSizeFactor);
		tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,display_width*0.48,off_angle);
		canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		
		//img_max_icon
		tempBitmap = resizeBitmap(img_max_icon,reSizeFactor);
		tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,display_width*0.48,max_angle);
		canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		
		//img_sat
		if(vib_level==0){
			tempBitmap = resizeBitmap(img_sat,reSizeFactor);
			tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,display_width*0.48,off_angle);
			canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		}else if(vib_level==100){
			
		}
		
		//img_dial_handle
		tempBitmap = resizeBitmap(img_dial_handle,reSizeFactor);
		tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,dial_handle_radius,dial_handle_angleDegree);
		canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
		dial_handle_size.x = tempBitmap.getWidth();
		dial_handle_size.y = tempBitmap.getHeight();
		
		if(flag_touch_on){
			if(flag_touch_handle_on){
				//img_touchOnLight
				tempBitmap = resizeBitmap(img_handle_touch,reSizeFactor);
				tempxy = getImgAngularXy(tempBitmap,dial_center.x,dial_center.y,dial_handle_radius,dial_handle_angleDegree);
				canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
			}else{
				//img_touchOnLight
				tempBitmap = resizeBitmap(img_touchOnLight,reSizeFactor);
				tempxy = getImgXy(tempBitmap, dial_center.x, dial_center.y);
				canvas.drawBitmap(tempBitmap, tempxy.x, tempxy.y,null);
			}
		}else{//initialize the canvas

		}
	}
	
	protected boolean velidateTouchPosition(MotionEvent event,double pointAcu) {
		double touch_x,touch_y,handle_x,handle_y;
		boolean touch_validity = false;
		
		touch_x = (double)event.getX();
		touch_y = (double)event.getY();
		handle_x = dial_center.x+dial_handle_radius*Math.cos(Math.toRadians(dial_handle_angleDegree));
		handle_y = dial_center.y+dial_handle_radius*Math.sin(Math.toRadians(dial_handle_angleDegree));

		if((Math.hypot(touch_x-handle_x, touch_y-handle_y)<pointAcu*dial_handle_size.x)){
			touch_validity = true;
		}else{
			touch_validity = false;
		}
		
		return touch_validity;
	}
	protected void testTouchAngle(MotionEvent event){
		double angle;
		angle = Math.toDegrees(Math.atan2((double)event.getY()-dial_center.y,(double)event.getX()-dial_center.x));
		Log.v("angle",""+angle);
	}
	protected int getVibLevel(double angle){
		double tempAngle;
		int tempVibLevel;
		if(angle<90){
			tempAngle = 360+angle;
		}else{
			tempAngle = angle;
		}
		tempVibLevel = (int)(0.4*(tempAngle-145));
		if(tempVibLevel<0){
			tempVibLevel = 0;
		}else if(tempVibLevel>100){
			tempVibLevel = 100;
		}
		
		return tempVibLevel;
	}
	
	protected double touchToAngle(MotionEvent event){
		double angle;
		
		angle = Math.toDegrees(Math.atan2((double)event.getY()-dial_center.y,(double)event.getX()-dial_center.x));//radian
		
		return angle;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction(); 
		double tempAngle;
		boolean flag_update=false;
		
		switch (eventaction ) { 
    	case MotionEvent.ACTION_DOWN:
    		flag_touch_on = true;
    		tVibLevel = vib_level;
    		if(velidateTouchPosition(event,0.5)){
				dial_handle_angle_before = dial_handle_angleDegree;
    			flag_touch_handle_on = true;
    			dial_handle_angleDegree = touchToAngle(event);
    			vib_level = getVibLevel(dial_handle_angleDegree);
    			flag_toastView = 0;
    			vibe.cancel();
    			flag_vibe_cancled = true;
    		}else{
    			flag_touch_handle_on = false;
    			
    			if (flag_toastView ==0){
    				Toast toastMessage = Toast.makeText(getContext(), R.string.toast_touch_dial, Toast.LENGTH_SHORT);
        			toastMessage.setGravity(Gravity.TOP|Gravity.CENTER, 0, 80);
        			toastMessage.show();
        			flag_toastView += 1;
    			}else{
    				flag_toastView = flag_toastView<3?flag_toastView+1:0;
    			}
    		}
    		flag_update = true;
    		
    		break;

    	case MotionEvent.ACTION_MOVE:
    		if(!flag_touch_handle_on){
    			break;
    		}
    		if(velidateTouchPosition(event,1.2)){
    			tempAngle = touchToAngle(event);
    			if(!(tempAngle>max_angle&&tempAngle<off_angle)){//valid
    				dial_handle_angleDegree = tempAngle;
    				vibe.cancel();
    				flag_vibe_cancled = true;
    			}
    		}else{

    			flag_touch_on = true;
        		flag_touch_handle_on = false;
        		dial_handle_angleDegree = dial_handle_angle_before;
    			if(flag_vibe_cancled == true){
         			flag_vibe_cancled = false;
         			vib_level = getVibLevel(dial_handle_angleDegree);
         			vibe.vibrate(vp.getPattern(vib_level),0);
         		}
    		}
    		
    		if(vib_level!=getVibLevel(dial_handle_angleDegree)){
    			vib_level = getVibLevel(dial_handle_angleDegree);
    			playSound(clsSoundKind.dial);
    			vibe.vibrate(5+vib_level/8);
    			flag_update = true;
    		}

    		break;
    	case MotionEvent.ACTION_UP:
     		flag_touch_on = false;
     		flag_touch_handle_on = false;
     		if(velidateTouchPosition(event,1.1)){
     			if(dial_handle_angleDegree>max_angle&&dial_handle_angleDegree<off_angle){//invalid
        			if((off_angle+max_angle-2*dial_handle_angleDegree)>0){//Close to max
        				dial_handle_angleDegree = max_angle;
        			}else{//Close to off
        				dial_handle_angleDegree = off_angle;
        			}
    			}
     		}
     		if(vib_level==0){
     			vibe.cancel();
     		}else if(flag_vibe_cancled == true){
     			flag_vibe_cancled = false;
     			vibe.vibrate(vp.getPattern(vib_level),0);
     		}
     		flag_update = true;
     		break;
    	
    	}
		if (flag_update==true){
			invalidate();   
		}
        return true;
	}
	
	
}
