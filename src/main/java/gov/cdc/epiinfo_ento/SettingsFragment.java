package gov.cdc.epiinfo_ento;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsFragment extends PreferenceFragment {

	private Activity activity;

	public void SetActivity(Activity activity)
	{
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		final CheckBoxPreference sync_up_only = (CheckBoxPreference) getPreferenceManager().findPreference("sync_up_only");
		final CheckBoxPreference sync_up_down = (CheckBoxPreference) getPreferenceManager().findPreference("sync_up_down");
		final CheckBoxPreference sync_down_only = (CheckBoxPreference) getPreferenceManager().findPreference("sync_down_only");
		final SwitchPreference lab_mode = (SwitchPreference) getPreferenceManager().findPreference("lab_mode");
		final ListPreference cloud_service = (ListPreference) getPreferenceManager().findPreference("cloud_service");
		final SwitchPreference azure_classic = (SwitchPreference) getPreferenceManager().findPreference("azure_classic");
		final Preference service_name = getPreferenceManager().findPreference("service_name");
		final Preference application_key = getPreferenceManager().findPreference("application_key");
		final Preference sftp_url = getPreferenceManager().findPreference("sftp_url");
		final Preference cloud_user_name = getPreferenceManager().findPreference("cloud_user_name");
		final Preference cloud_pwd = getPreferenceManager().findPreference("cloud_pwd");


		sync_up_only.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				sync_up_only.setChecked(true);
				sync_down_only.setChecked(false);
				sync_up_down.setChecked(false);
				return true;
			}
		});

		sync_up_down.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				sync_up_only.setChecked(false);
				sync_down_only.setChecked(false);
				sync_up_down.setChecked(true);
				return true;
			}
		});
		
		sync_down_only.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				sync_up_only.setChecked(false);
				sync_down_only.setChecked(true);
				sync_up_down.setChecked(false);
				return true;
			}
		});

		lab_mode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object val) {
				if ((Boolean)val)
				{
					GetLabForm();
				}
				else
				{
					GetFieldForm();
				}
				return true;
			}
		});
		
		if (cloud_service.getValue().equals("Box"))
		{
			azure_classic.setEnabled(false);
			service_name.setEnabled(false);
			application_key.setEnabled(false);
			sftp_url.setEnabled(false);
			cloud_user_name.setEnabled(false);
			cloud_pwd.setEnabled(false);
		}
		else if (cloud_service.getValue().equals("SFTP"))
		{
			azure_classic.setEnabled(false);
			service_name.setEnabled(false);
			application_key.setEnabled(false);
			sftp_url.setEnabled(true);
			cloud_user_name.setEnabled(true);
			cloud_pwd.setEnabled(true);
		}
		else
		{
			azure_classic.setEnabled(true);
			service_name.setEnabled(true);
			application_key.setEnabled(true);
			sftp_url.setEnabled(false);
			cloud_user_name.setEnabled(false);
			cloud_pwd.setEnabled(false);
		}
		
		cloud_service.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object val) {
				if (val.toString().equals("Box"))
				{
					azure_classic.setEnabled(false);
					service_name.setEnabled(false);
					application_key.setEnabled(false);
					sftp_url.setEnabled(false);
					cloud_user_name.setEnabled(false);
					cloud_pwd.setEnabled(false);
				}
				else if (val.toString().equals("SFTP"))
				{
					azure_classic.setEnabled(false);
					service_name.setEnabled(false);
					application_key.setEnabled(false);
					sftp_url.setEnabled(true);
					cloud_user_name.setEnabled(true);
					cloud_pwd.setEnabled(true);
				}
				else
				{
					azure_classic.setEnabled(true);
					service_name.setEnabled(true);
					application_key.setEnabled(true);
					sftp_url.setEnabled(false);
					cloud_user_name.setEnabled(false);
					cloud_pwd.setEnabled(false);
				}
				return true;
			}
		});
	}

	private void GetFieldForm() {

		String suffix = this.getString(R.string.lang_suffix);

		AssetManager am = activity.getAssets();
		try {
			String fileName = "Locations" + suffix + ".xml";
			File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfoEntomology/Questionnaires/Locations.xml");
			InputStream in = am.open(fileName);
			FileOutputStream f = new FileOutputStream(destinationFile);
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {

		}

		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");
		FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/Locations.xml", activity);
		EpiDbHelper db = new EpiDbHelper(activity, formMetadata, "Locations");
		db.open();
		db.CreateUpdateTable(formMetadata, "Locations");
	}

	private void GetLabForm() {

		String suffix = this.getString(R.string.lang_suffix);

		AssetManager am = activity.getAssets();
		try {
			String fileName = "LocationsLab" + suffix + ".xml";
			File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfoEntomology/Questionnaires/Locations.xml");
			InputStream in = am.open(fileName);
			FileOutputStream f = new FileOutputStream(destinationFile);
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {

		}

		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");
		FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/Locations.xml", activity);
		EpiDbHelper db = new EpiDbHelper(activity, formMetadata, "Locations");
		db.open();
		db.CreateUpdateTable(formMetadata, "Locations");

	}
}