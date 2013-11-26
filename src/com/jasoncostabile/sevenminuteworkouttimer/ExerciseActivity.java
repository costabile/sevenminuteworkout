package com.jasoncostabile.sevenminuteworkouttimer;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasoncostabile.sevenminuteworkouttimer.R;
import com.jasoncostabile.sevenminuteworkouttimer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ExerciseActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	//private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	//private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	//private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	//private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	//private SystemUiHider mSystemUiHider;

	/**
	 * Keep track of the timer that is currently running so we can stop it if necessary.
	 */
	CountDownTimer currentTimer;
	
	/**
	 * List of IDs of the drawables for each exercise image, in order.
	 */
	int[] exerciseDrawables = {R.drawable.jumpingjacks, R.drawable.wallsit, R.drawable.pushup, R.drawable.abdominalcrunch};
	
	/**
	 * For playing sounds.
	 */
	private SoundPool mSoundPool;
	private AudioManager  mAudioManager;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private int mStream = 0;
	final static int START_EX_SOUND = 0;
	final static int END_EX_SOUND = 1;
	final static int FINISH_SOUND = 2;
	
	/**
	 * Views that will be updated dynamically by multiple functions.
	 */
	TextView timerText;
	TextView exerciseText;
	TextView nextExerciseText;
	ImageView exerciseImg;

	Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_exercise);
		//setupActionBar();
		
		res = getResources();
		
		timerText = (TextView) findViewById(R.id.timer_text);
		exerciseText = (TextView) findViewById(R.id.exercise_text);
		nextExerciseText = (TextView) findViewById(R.id.next_exercise_text);
		exerciseImg = (ImageView) findViewById(R.id.exercise_image);
		
		//set up the audio player
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		//load fx
		mSoundPoolMap.put(START_EX_SOUND, mSoundPool.load(this, R.raw.start_exercise, 1));
		mSoundPoolMap.put(END_EX_SOUND, mSoundPool.load(this, R.raw.end_exercise, 1));
		mSoundPoolMap.put(FINISH_SOUND, mSoundPool.load(this, R.raw.finish, 1));

		/*final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.stop_button).setOnTouchListener(
				mDelayHideTouchListener);*/
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		// The activity is either being restarted or started for the first time
		String getReady = getString(R.string.exercise_default);
		String next = getString(R.string.next);
		String nextExerciseName = res.getStringArray(R.array.exercise_names)[0];
		
    	exerciseText.setText(getReady);
    	nextExerciseText.setText(next + ": " + nextExerciseName);
    	exerciseImg.setImageResource(exerciseDrawables[0]);
		
		//"Get ready" countdown
		currentTimer = new CountDownTimer(5000, 1000) {
            @Override
		    public void onTick(long millisUntilFinished) {
            	timerText.setText("" + (int)Math.ceil(millisUntilFinished / 1000.0));
		    }

            @Override
		    public void onFinish() {
		    	startExercise(0);
		    }
		};
		
		currentTimer.start();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();  

	    stopExercise();
	}
	
	public void startExercise(int exerciseNum) {
		final int exNum = exerciseNum;
		final String[] exerciseNames = res.getStringArray(R.array.exercise_names);

		playSound(START_EX_SOUND);
    	exerciseText.setText(exerciseNames[exNum]);
    	nextExerciseText.setText(" ");
		
    	currentTimer = new CountDownTimer(30000, 1000) {
            @Override
		    public void onTick(long millisUntilFinished) {
		    	timerText.setText("" + (int)Math.ceil(millisUntilFinished / 1000.0));
		    }

            @Override
		    public void onFinish() {
		    	if (exNum < exerciseNames.length - 1) {
		    		playSound(END_EX_SOUND);
			    	startRest(exNum);
		    	} else {
		    		playSound(FINISH_SOUND);
			    	finished();
		    	}
		    }
		};
		
		currentTimer.start();
	}
	
	public void startRest(int exerciseNum) {
		final int exNum = exerciseNum + 1;
		String rest = getString(R.string.rest);
		String next = getString(R.string.next);
		String nextExerciseName = res.getStringArray(R.array.exercise_names)[exNum];

    	exerciseText.setText(rest);
    	nextExerciseText.setText(next + ": " + nextExerciseName);
    	if (exNum < exerciseDrawables.length) exerciseImg.setImageResource(exerciseDrawables[exNum]);
    	
    	currentTimer = new CountDownTimer(10000, 1000) {
            @Override
			public void onTick(long millisUntilFinished) {
		    	timerText.setText("" + (int)Math.ceil(millisUntilFinished / 1000.0));
		    }

            @Override
		    public void onFinish() {
		    	startExercise(exNum);
		    }
		};
		
		currentTimer.start();
	}
	
	public void finished() {
		final String finished = getString(R.string.finished);
		final String done = getString(R.string.done);
		
		timerText = (TextView) findViewById(R.id.timer_text);
		exerciseText = (TextView) findViewById(R.id.exercise_text);
		final Button stopButton = (Button) findViewById(R.id.stop_button);

		if (currentTimer != null) currentTimer.cancel();
		
		exerciseText.setText(finished);
    	stopButton.setText(done);
    	timerText.setText(" ");
	}
	
	
	/***************
	 * Button click methods
	 */

	//DEBUG (+ button)
	public void skip(View view) {
		finished();
	}
	
	public void stopExercise(View view) {
		stopExercise();
	}
	
	
	/***************
	 * Private helper methods
	 */
	
	private void stopExercise() {
		if (currentTimer != null) currentTimer.cancel();

		this.finish();
	}
	
	private void playSound(int soundKey) {
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		mSoundPool.stop(mStream);
		mStream = mSoundPool.play(mSoundPoolMap.get(soundKey), streamVolume, streamVolume, 1, 0, 1f);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		//delayedHide(100);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			//if (AUTO_HIDE) {
				//delayedHide(AUTO_HIDE_DELAY_MILLIS);
			//}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			//mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	/*private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}*/
}
