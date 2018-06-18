package net.grucza.android.noteedgesidebar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import net.grucza.android.noteedgesidebar.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Main Config class
 */
public class Config {
	private static final String TAG = "noteedgesidebar.Util.Config";
	private Context context;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	private String activeSidebarView;
	private boolean showContent = true;
	private boolean contentVisibility = true;
	
	private Launcher launcher;
	
	/**
	 * Constructor of the Config class
	 *
	 * @param context The context of the Activity/Service
	 */
	public Config(Context context) {
		// FILL NEEDED PARAMETERS
		this.context = context;
		this.preferences = context.getSharedPreferences(context.getString(R.string.preferencesFileKey), MODE_PRIVATE);
		this.editor = preferences.edit();
		this.launcher = new Launcher();
		
		// LOAD CONFIGURATION
		this.load();
	}
	
	/**
	 * Loads all config data from SharedPreferences
	 */
	public void load() {
		// LOAD SETTINGS FOR PREFERENCES
		this.showContent = preferences.getBoolean("showContent", false);
		this.contentVisibility = preferences.getBoolean("contentVisibility", true);
		this.activeSidebarView = preferences.getString("activeSidebarView", "LAUNCHER");
		
		// LOAD LAUNCHER APPS AND INSTANCIATE LAUNCHER
		this.launcher.Launcher(this.context, preferences.getString("launcherApps", ""));
	}
	
	/**
	 * Getter for context This method returns the context of the config
	 *
	 * @return Context  Returns the context
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Getter for activeSidebarView This method returns the active sidebar view e.g. "LAUNCHER"
	 *
	 * @return String  Returns the active Sidebar view name
	 */
	public String getActiveSidebarView() {
		return activeSidebarView;
	}
	
	/**
	 * Setter for activeSidebarView Use this function to set the sidebar view e.g. "LAUNCHER"
	 *
	 * @param activeSidebarView String  Set the active sidebar view
	 */
	public void setActiveSidebarView(String activeSidebarView) {
		this.activeSidebarView = activeSidebarView;
		this.storeConfig();
	}
	
	/**
	 * This method stores all config data to SharedPreferences
	 */
	public void storeConfig() {
		editor.putBoolean("showContent", this.showContent);
		editor.putBoolean("contentVisibility", this.contentVisibility);
		editor.putString("activeSidebarView", this.activeSidebarView);
		
		editor.putString("launcherApps", launcher.getLauncherAppsPackages());
		editor.commit();
	}
	
	/**
	 * Getter for showContent This method gets the visibility of the sidebar view
	 *
	 * @return Boolean Returns true if the sidebar content is visible
	 */
	public Boolean getShowContent() {
		return showContent;
	}
	
	/**
	 * Setter for show Content This method sets the visibility of the sidebar content
	 *
	 * @param showContent Boolean True is the content of the sidebar should be shown
	 */
	public void setShowContent(boolean showContent) {
		this.showContent = showContent;
		this.storeConfig();
	}
	
	/**
	 * Getter for contentVisibility This method gets the visibility of the sidebar view content
	 *
	 * @return Boolean Returns true if the sidebar content is visible
	 */
	public Boolean getContentVisibility() {
		return contentVisibility;
	}
	
	/**
	 * Setter for contentVisibility This method sets the visibility of the sidebar content of set
	 * off the switch icon (visibility) is still shown
	 *
	 * @param contentVisibility Boolean True if the content of the sidebar should be shown
	 */
	public void setContentVisibility(boolean contentVisibility) {
		this.contentVisibility = contentVisibility;
		this.storeConfig();
	}
	
	/**
	 * This method launches an app with an intent
	 *
	 * @param app App The app to launch
	 */
	public void addLauncherApp(Launcher.App app) {
		this.launcher.addLauncherApp(app);
		this.storeConfig();
	}
	
	/**
	 * Removes an app from the Launcher
	 * @param app App the app to remove
	 */
	public void removeLauncherApp(Launcher.App app) {
		this.launcher.removeLauncherApp(app);
		this.storeConfig();
	}
	
	/**
	 * returns the launcher object
	 * @return launcher Launcher the launcher object
	 */
	public Launcher getLauncher() {
		return this.launcher;
	}
	
	public void moveLauncherApp(int before, int after) {
		this.launcher.moveLauncherApp(before, after);
		this.storeConfig();
	}
}
