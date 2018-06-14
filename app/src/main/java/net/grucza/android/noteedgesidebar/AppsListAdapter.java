package net.grucza.android.noteedgesidebar;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import net.grucza.android.noteedgesidebar.utils.Config;
import net.grucza.android.noteedgesidebar.utils.Launcher;

import java.util.List;

public class AppsListAdapter extends ArrayAdapter {
	//to reference the Activity
	private final Activity context;
	
	private Config config;
	
	private List<Launcher.App> apps;
	
	public AppsListAdapter(Activity context, Config config) {
		//super(context,R.layout.list_launcher_apps_row, nameArrayParam);
		super(context, R.layout.list_launcher_apps_row, config.getLauncher().getInstalledApps());
		
		this.context = context;
		this.config = config;
	}
	
	@NonNull
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.list_launcher_apps_row, parent, false);
		
		//this code gets references to objects in the listview_row.xml file
		ImageView app_icon = rowView.findViewById(R.id.app_icon);
		TextView app_name = rowView.findViewById(R.id.app_name);
		TextView app_package_name = rowView.findViewById(R.id.app_package_name);
		Switch launcher_switch = rowView.findViewById(R.id.launcher_switch);
		
		//launcher_switch.setOnCheckedChangeListener(this);
		
		//this code sets the values of the objects to values from the arrays
		Launcher.App app = (Launcher.App) getItem(position);
		
		app_name.setText(app.getName());
		app_package_name.setText(app.getPackageName());
		app_icon.setImageDrawable(app.getIcon());
		
		launcher_switch.setChecked(app.getLaunchable());
		
		return rowView;
	}
}
