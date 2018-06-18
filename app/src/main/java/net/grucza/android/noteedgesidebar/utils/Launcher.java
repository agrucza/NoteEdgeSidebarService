package net.grucza.android.noteedgesidebar.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main Launcher class
 */
public class Launcher {
	Context context;
	List<App> installed_apps;
	List<App> launcher_apps;

	/**
	 * Main class of the launcher
	 *
	 * @param context      Context The context of the settings app/service
	 * @param launcherApps String  The stored visible apps package names seperated by commata
	 */
	public void Launcher(Context context, String launcherApps) {
		this.context = context;
		this.installed_apps = new ArrayList<App>();
		this.launcher_apps = new ArrayList<App>();
		
		loadApps(launcherApps);
	}
	
	/**
	 * This is a helper function to load all installed apps
	 *
	 * @param launcherApps String  The stored visible apps package names seperated by commata
	 */
	private void loadApps(String launcherApps) {
		// Make a list out of the launcherApps parameter
		List<String> launchable_apps = Arrays.asList(launcherApps.split(";"));
		
		final PackageManager pm = context.getPackageManager();
		
		//get a list of installed apps
		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		
		// iterate over the installed packages
		for (PackageInfo packageInfo : packages) {
			// Check if the package has a valid package name and a valid visible name
			if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
				// Add the package to installed apps
				if (launchable_apps.contains(packageInfo.packageName)) {
					launcher_apps.add(new App(packageInfo.applicationInfo.loadIcon(pm), packageInfo.applicationInfo.loadLabel(pm).toString(), packageInfo.packageName, pm.getLaunchIntentForPackage(packageInfo.packageName), true));
					installed_apps.add(new App(packageInfo.applicationInfo.loadIcon(pm), packageInfo.applicationInfo.loadLabel(pm).toString(), packageInfo.packageName, pm.getLaunchIntentForPackage(packageInfo.packageName), true));
				} else {
					installed_apps.add(new App(packageInfo.applicationInfo.loadIcon(pm), packageInfo.applicationInfo.loadLabel(pm).toString(), packageInfo.packageName, pm.getLaunchIntentForPackage(packageInfo.packageName), false));
				}
			}
		}
		
		for (int i = 0; i < launcher_apps.size(); i++) {
			int index = launchable_apps.indexOf(launcher_apps.get(i).packageName);
			launcher_apps.add(index, launcher_apps.remove(i));
		}
	}
	
	public String getLauncherAppsPackages() {
		String output = "";
		
		for (int i = 0; i < this.launcher_apps.size(); i++) {
			App app = this.launcher_apps.get(i);
			output += app.getPackageName() + ";";
		}
		
		if (output.length() > 0) {
			output = output.substring(0, output.length() - 1);
		}
		
		return output;
	}
	
	public List<App> getInstalledApps() {
		return installed_apps;
	}
	
	public List<App> getLauncherApps() {
		return launcher_apps;
	}
	
	public void addLauncherApp(App app) {
		this.launcher_apps.add(app);
	}
	
	public void removeLauncherApp(App app) {
		for (int i = 0; i < this.launcher_apps.size(); i++) {
			if (this.launcher_apps.get(i).getPackageName() == app.getPackageName()) {
				this.launcher_apps.remove(i);
			}
		}
	}
	
	public App getLauncherApp(int position) {
		return this.launcher_apps.get(position);
	}
	
	public void startApp(int position) {
		App app = this.launcher_apps.get(position);
		Intent i = app.getIntent();
		
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		this.context.startActivity(i);
	}
	
	public void moveLauncherApp(int before, int after) {
		App app = this.launcher_apps.remove(before);
		this.launcher_apps.add(after, app);
	}
	
	/**
	 * Subclass for Launcher apps
	 */
	public class App {
		private Drawable icon;
		private String name;
		private String packageName;
		private Intent intent;
		private boolean launchable;
		private boolean highlight = false;
		
		/**
		 * Constructor for Launcher Apps
		 *
		 * @param icon        Drawable    The icon to be displayed for the app
		 * @param name        String      The Name to be displayed for the app
		 * @param packageName String      The package name of the app
		 * @param intent      Intent      The intent to start the app
		 */
		public App(Drawable icon, String name, String packageName, Intent intent, boolean launchable) {
			this.icon = icon;
			this.name = name;
			this.packageName = packageName;
			this.intent = intent;
			this.launchable = launchable;
		}
		
		/**
		 * Getter for the apps icon
		 *
		 * @return Drawable The Icon of the app
		 */
		public Drawable getIcon() {
			return icon;
		}
		
		/**
		 * Getter for the apps name
		 *
		 * @return String The name of the app
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Getter gor the apps package name
		 *
		 * @return String  The package name of the app
		 */
		public String getPackageName() {
			return packageName;
		}
		
		/**
		 * Getter for the apps launch intent use this intent to start the app
		 *
		 * @return String  The launch intent of the app
		 */
		public Intent getIntent() {
			return intent;
		}
		
		/**
		 * Getter for the apps launchable setting use this to get launchable setting
		 *
		 * @return boolean True if launchable
		 */
		public boolean getLaunchable() {
			return launchable;
		}
		
		/**
		 * Setter for the apps launchable setting use this to set launchable setting
		 */
		public void setLaunchable(boolean launchable) {
			this.launchable = launchable;
		}
		
		public boolean getHighlight() {
			return this.highlight;
		}
		
		public void setHightlight(boolean highlight) {
			this.highlight = highlight;
		}
	}
}
