package net.grucza.android.noteedgesidebar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import net.grucza.android.noteedgesidebar.utils.Config;
import net.grucza.android.noteedgesidebar.utils.Launcher;

import java.util.Arrays;
import java.util.List;

public class NoteEdgeSidebarSettings extends Activity implements AdapterView.OnItemClickListener {
	private static final String TAG = "noteedgesidebar";
	ListView listView;
	// CONFIG FILE
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	// CONFIGURATION SETTINGS
	private Config config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// NEW CONFIG
		config = new Config(this);
		
		setContentView(R.layout.activity_settings);
		
		AppsListAdapter appsListAdapter = new AppsListAdapter(this, config);
		
		listView = findViewById(R.id.listLauncherApps);
		
		listView.setOnItemClickListener(this);
		
		listView.setAdapter(appsListAdapter);
		
		toggleService();
		
		// SET VIEW WHEN STARTED BY LAUNCHER
		//setContentView(R.layout.activity_settings);
		
		// FINISH IF STARTED OTHERWISE
		//finish();
		
		setupSettings();
	}
	
	private void toggleService() {
		Intent intent = new Intent(this, NoteEdgeSidebarService.class);
		if (!stopService(intent)) {
			Log.i(TAG, "Starting Note Edge Sidebar Service");
			startService(intent);
		}
	}
	
	private void setupSettings() {
		// SETTING UI ACCORDING TO CONFIG PREFERENCES
		Switch showContentSwitch = findViewById(R.id.switchShowContent);
		showContentSwitch.setChecked(config.getShowContent());
		
		showContentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "visibility: " + isChecked);
				config.setShowContent(isChecked);
				sendToService("reloadConfig", "test");
			}
		});
	}
	
	private void sendToService(String intentExtra, String intentValue) {
		Intent intent = new Intent(this, NoteEdgeSidebarService.class);
		intent.putExtra(intentExtra, intentValue);
		startService(intent);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Launcher.App app = config.getLauncher().getInstalledApps().get(position);
		
		List<String> launchable_apps = Arrays.asList(config.getLauncher().getLauncherAppsPackages().split(";"));
		
		if (!launchable_apps.contains(app.getPackageName())) {
			Log.d(TAG, "Setting launcher app: " + app.getPackageName());
			config.addLauncherApp(app);
		} else {
			Log.d(TAG, "Remove launcher app: " + app.getPackageName());
			config.removeLauncherApp(app);
		}
		
		sendToService("reloadConfig", "test");
	}
}
