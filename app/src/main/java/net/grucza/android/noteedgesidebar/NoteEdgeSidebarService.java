package net.grucza.android.noteedgesidebar;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.grucza.android.noteedgesidebar.utils.Config;
import net.grucza.android.noteedgesidebar.utils.Launcher;

import java.util.List;

/**
 * NoteEdgeSidebarService This is the main service providing the sidebar view
 */
public class NoteEdgeSidebarService extends Service implements ImageView.OnTouchListener {
	private static final String TAG = "noteedgesidebarservice";
	
	private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
	
	private LinearLayout oView;
	private WindowManager.LayoutParams oParams;
	private LinearLayout cView;
	private LinearLayout.LayoutParams cParams;
	
	private Config config;
	
	private OrientationEventListener orientationEventListener;
	
	private int lastrotation = -1;
	
	private int _xTouchDelta;
	private int _yTouchDelta;
	private int _touchStart;
	
	@Override
	public void onCreate() {
		this.config = new Config(this);
		createSidebar();
		
		this.orientationEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
				Display display = ((WindowManager) config.getContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
				int rotation = display.getRotation();
				/*
				 * rotation 0 is portrait mode
				 * rotation 1 is landscape left (sidebar on top)
				 * rotation 2 is portrait on top (non existing?)
				 * rotation 3 is landscape right (sidebar on bottom)
				 */
				if (rotation != lastrotation) {
					lastrotation = rotation;
					Log.i(TAG, "rotation: " + rotation);
					updateSidebarView(rotation);
				}
			}
		};
		
		if (orientationEventListener.canDetectOrientation()) {
			orientationEventListener.enable();
		} else {
			orientationEventListener.disable();
		}
	}
	
	/**
	 * Used to react to the settings app commands (mainly "reloadConfig")
	 *
	 * @param intent  Intent The intent
	 * @param flags   not used
	 * @param startId not used
	 *
	 * @return Returns START_STICKY to prevent the OS from killing the service (Does not work as
	 * 		expected, service is killed when device goes to sleep)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		if (intent.hasExtra("reloadConfig")) {
			Log.i(TAG, "Received reloadConfig");
			this.reloadConfig();
		}
		
		return START_STICKY;
	}
	
	/**
	 * This method is used to reload the configuration settings
	 */
	private void reloadConfig() {
		this.config.load();
		this.updateSidebarContent();
	}
	
	/**
	 * Used to update the sidebar view
	 */
	private void updateSidebarContent() {
		cView.removeAllViews();
		
		// GETTING DATA FROM CONFIG TO BUILD UI
		if (config.getShowContent() == true) {
			switch (config.getActiveSidebarView()) {
				case "LAUNCHER":
					launcherView();
					break;
				default:
					launcherView();
			}
		}
	}
	
	/**
	 * This method is used to construct the launcher view
	 */
	private void launcherView() {
		List<Launcher.App> apps = config.getLauncher().getLauncherApps();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(160, 160);
		
		ImageView imageView = new ImageView(config.getContext());
		
		imageView.setImageResource(R.drawable.baseline_visibility_white_24);
		imageView.setImageAlpha(63);
		imageView.setLayoutParams(layoutParams);
		
		imageView.setTag(0);
		//imageView.setOnClickListener(this);
		
		cView.addView(imageView);
		
		if (config.getContentVisibility()) {
			for (int i = 0; i < apps.size(); i++) {
				imageView = new ImageView(config.getContext());
				imageView.setImageDrawable(apps.get(i).getIcon());
				imageView.setLayoutParams(layoutParams);
				
				if (apps.get(i).getHighlight()) {
					imageView.setBackgroundColor(Color.GRAY);
				}
				
				imageView.setTag(i + 1);
				
				//imageView.setOnClickListener(this);
				
				cView.addView(imageView);
			}
		}
		
		cView.setOnTouchListener(this);
	}
	
	/**
	 * Pretty standard
	 * TODO: Probably start service again to keep it running all the time?
	 */
	@Override
	public void onDestroy() {
		Log.i(TAG, "noteedgesidebarservice destroy");
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return null;
	}
	
	/**
	 * Creates the initial sidebar view
	 */
	private void createSidebar() {
		// MAKING CONTENT VIEW
		cView = new LinearLayout(this);
		cParams = new LinearLayout.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT
		);
		
		cView.setOrientation(LinearLayout.VERTICAL);
		
		updateSidebarView(((WindowManager) config.getContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation());
	}
	
	private void updateSidebarView(int rotation) {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		if (oView != null) {
			wm.removeView(oView);
			oView.removeAllViews();
		}
		
		// MAKING BASE OVERLAY VIEW
		oView = new LinearLayout(this);
		oView.setBackgroundColor(0xFF000000);
		
		oParams = new WindowManager.LayoutParams(
				160,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				,
				PixelFormat.TRANSLUCENT
		);
		
		switch (rotation) {
			case 0:
				// portrait
				oParams.x = 1440;
				oParams.y = 0;
				oParams.width = 160;
				oParams.height = WindowManager.LayoutParams.MATCH_PARENT;
				cView.setOrientation(LinearLayout.VERTICAL);
				break;
			case 1:
				// landscape left
				oParams.x = 0;
				oParams.y = 0;
				oParams.width = WindowManager.LayoutParams.MATCH_PARENT;
				oParams.height = 160;
				oParams.gravity = Gravity.TOP;
				cView.setOrientation(LinearLayout.HORIZONTAL);
				break;
			case 2:
				// portrait top (not possible?)
				break;
			case 3:
				// landscape right
				oParams.x = 0;
				oParams.y = 1440;
				oParams.width = WindowManager.LayoutParams.MATCH_PARENT;
				oParams.height = 160;
				cView.setOrientation(LinearLayout.HORIZONTAL);
				break;
		}
		
		updateSidebarContent();
		
		oView.addView(cView, cParams);
		
		wm.addView(oView, oParams);
	}
	
	/*
	@Override
	public void onClick(View v) {
		// Icons are as following:
		// 0 is the context visibility icon
		// 1 ... n are the launcher apps but starting at 0
		// launcher app index is ((int)v.getTag())-1
		int position = (int) v.getTag();
		if (position == 0) {
			// visibility icon is clicked
			config.setContentVisibility(!config.getContentVisibility());
			updateSidebarContent();
		} else {
			// app icon is clicked
			position -= 1;
			config.getLauncher().startApp(position);
		}
	}
	*/
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int size = config.getLauncher().getLauncherApps().size();
		final int x = (int) event.getRawX();
		final int y = (int) event.getRawY();
		
		final int swipedetect = 50;
		boolean swipe = false;
		boolean swipenext = true;
		
		boolean returnval = true;
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				_xTouchDelta = x;
				_yTouchDelta = y;
				_touchStart = (int) Math.ceil((lastrotation == 0 ? _yTouchDelta : _xTouchDelta) / 160) - 1;
				if (_touchStart < size) {
					if (_touchStart >= 0) {
						config.getLauncher().getLauncherApp(_touchStart).setHightlight(true);
					} else {
						// visibility icon is clicked
						config.setContentVisibility(!config.getContentVisibility());
						returnval = false;
					}
					updateSidebarContent();
				}
				break;
			case MotionEvent.ACTION_UP:
				int after = ((int) Math.ceil((lastrotation == 0 ? y : x) / 160) - 1);
				
				if (after > size) {
					after = size - 1;
				}
				
				if (_touchStart >= 0 && _touchStart < size) {
					config.getLauncher().getLauncherApp(_touchStart).setHightlight(false);
					
					if (_touchStart == after) {
						config.getLauncher().startApp(_touchStart);
					} else {
						config.moveLauncherApp(_touchStart, after);
					}
				}
				
				updateSidebarContent();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				if (lastrotation == 0) {
					// try to detect left and right swiping
					if ((_xTouchDelta - swipedetect) > x) {
						// left swipe
						swipe = true;
						swipenext = true;
					} else if ((_xTouchDelta + swipedetect) < x) {
						// right swipe
						swipe = true;
					}
				} else {
					// try to detect up and down swiping
					if ((_yTouchDelta - swipedetect) > y) {
						// up swipe
						swipe = true;
					} else if ((_xTouchDelta + swipedetect) < x) {
						// down swipe
						swipe = true;
						swipenext = true;
					}
				}
				
				if (swipe) {
					Log.d(TAG, "swipe direction: " + (swipenext ? "next" : "last"));
					returnval = true;
				}
				break;
		}
		//rootLayout.invalidate();
		
		return returnval;
	}
}
