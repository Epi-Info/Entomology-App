package gov.cdc.epiinfo_ento;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import gov.cdc.epiinfo_ento.analysis.CsvFileGenerator;
import gov.cdc.epiinfo_ento.analysis.Dashboard;
import gov.cdc.epiinfo_ento.cloud.BoxClient;
import gov.cdc.epiinfo_ento.cloud.IBoxActivity;
import gov.cdc.epiinfo_ento.etc.ExtFilter;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, IBoxActivity {

	private Button btnCollectData;
	private Button btnStatcalc;
	private Button btnAnalyze;
	private MenuItem mnuBoxSignin;
	private MenuItem mnuBoxSignout;
	private MainActivity self;
	private GoogleMap mMap;
	private MapView mMapView;
	private static boolean splashShown;

	private void LoadActivity(Class c) {
		startActivity(new Intent(this, c));
	}

	private void LoadActivity(String component, String activity) {
		Intent intent = new Intent();
		intent.setClassName(component, activity);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			openOptionsMenu();
		} else {
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	@Override
	public void openOptionsMenu() {
		Configuration config = getResources().getConfiguration();

		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE) {
			int originalScreenLayout = config.screenLayout;
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
			super.openOptionsMenu();
			config.screenLayout = originalScreenLayout;
		} else {
			super.openOptionsMenu();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!splashShown) {
			LoadActivity(SplashScreen.class);
			splashShown = true;
		}

		//VECTOR
		setContentView(R.layout.entry_vector);
		//setContentView(R.layout.entry); 

		self = this;

		DeviceManager.Init(this);
		DeviceManager.SetOrientation(this, false);

		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		LoadMap();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (!sharedPref.getBoolean("ei7", false) && !sharedPref.getBoolean("stacked", false) && !sharedPref.getBoolean("interview", false)) {
			SharedPreferences.Editor editor = sharedPref.edit();
			if (DeviceManager.IsLargeTablet()) {
				editor.putBoolean("ei7", true);
			} else {
				editor.putBoolean("stacked", true);
			}
			editor.putBoolean("sync_up_only", true);
			editor.putBoolean("sample_forms", true);
			editor.commit();
		}
		if (!sharedPref.contains("cloud_service")) {
			if ((!sharedPref.contains("application_key") || sharedPref.getString("application_key", "").equals(""))) {
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("cloud_service", "Box");
				editor.commit();
			} else {
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("cloud_service", "Microsoft Azure");
				editor.commit();
			}
		}
		if (!sharedPref.contains("cloud_sync_save")) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("cloud_sync_save", true);
			editor.commit();
		}
		if (!sharedPref.contains("decimal_symbol")) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("decimal_symbol", ".");
			editor.commit();
		}
		if (!sharedPref.contains("device_id")) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("device_id", Secure.getString(getContentResolver(), Secure.ANDROID_ID));
			editor.commit();
		}
		if (!sharedPref.contains("azure_classic")) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("azure_classic", true);
			editor.commit();
		}

		this.setTheme(R.style.AppTheme);

		btnCollectData = (Button) findViewById(R.id.btnCollectData);
		btnCollectData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//VECTOR
				//showDialog(1);
				Intent recordList = new Intent(self, RecordList.class);
				recordList.putExtra("ViewName", "Locations");
				startActivity(recordList);
			}
		});
		btnAnalyze = (Button) findViewById(R.id.btnAnalyze);
		btnAnalyze.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//VECTOR
				//showDialog(1);
				Intent dashboard = new Intent(self, Dashboard.class);
				startActivity(dashboard);
			}
		});
/*
		btnStatcalc = (Button)findViewById(R.id.btnStatcalc);
		btnStatcalc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(self, StatCalcMain.class));
			}
		});*/

		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			path.mkdirs();
			File syncPath = new File(path, "/EpiInfoEntomology/SyncFiles/");
			File quesPath = new File(path, "/EpiInfoEntomology/Questionnaires/");
			File imgPath = new File(path, "/EpiInfoEntomology/Images/");
			File preloadPath = new File(path, "/EpiInfoEntomology/Preload/");
			syncPath.mkdirs();
			quesPath.mkdirs();
			imgPath.mkdirs();
			preloadPath.mkdirs();

			File handshakeFile = new File(path, "/EpiInfoEntomology/Handshake.xml");
			FileWriter handshakeFileWriter = new FileWriter(handshakeFile);
			BufferedWriter handshakeOut = new BufferedWriter(handshakeFileWriter);
			handshakeOut.write(GetHandshakeContents());
			handshakeOut.close();

		} catch (Exception ex) {

		}

		GetEntoForms();

		AssetManager assetManager = getAssets();
		try {
			String fileName = "EpiGrammar.cgt";
			File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfo/" + fileName);
			InputStream in = assetManager.open(fileName);
			FileOutputStream f = new FileOutputStream(destinationFile);
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {

		}

		try {
			String fileName = "displayMetrics.xml";
			File outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File outputFile = new File(outputDirectory + "/EpiInfoEntomology/" + fileName);

			if (outputFile.exists() == false) {
				android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

				writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");

				writer.write("<displayMetrics ");

				writer.write("xdpi=\"" + Float.toString(displayMetrics.xdpi) + "\" ");
				writer.write("ydpi=\"" + Float.toString(displayMetrics.ydpi) + "\" ");
				writer.write("widthPixels=\"" + Integer.toString(displayMetrics.widthPixels) + "\" ");
				writer.write("heightPixels=\"" + Integer.toString(displayMetrics.heightPixels) + "\" ");

				writer.write("/>");

				writer.close();
			}
		} catch (Exception e) {
		}

		new Preloader().Load(this);

		loadDefaults();

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("ViewName")) {
			String viewName = extras.getString("ViewName");
			Intent recordList = new Intent(this, RecordList.class);
			recordList.putExtra("ViewName", viewName);
			startActivity(recordList);
		} else if (!AppManager.getDefaultForm().equals("")) {
			Intent recordList = new Intent(this, RecordList.class);
			recordList.putExtra("ViewName", AppManager.getDefaultForm());
			startActivity(recordList);
			finish();
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		KmlLoader.Load(googleMap, this);
	}

	private void loadDefaults() {
		AppManager.setDefaultForm("");
		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, "EpiInfoEntomology/defaults.xml");

			InputStream obj_is = null;
			Document obj_doc = null;
			DocumentBuilderFactory doc_build_fact = null;
			DocumentBuilder doc_builder = null;
			obj_is = new FileInputStream(file);
			doc_build_fact = DocumentBuilderFactory.newInstance();
			doc_builder = doc_build_fact.newDocumentBuilder();

			obj_doc = doc_builder.parse(obj_is);
			NodeList obj_nod_list = null;
			if (null != obj_doc) {
				Element feed = obj_doc.getDocumentElement();
				String form = feed.getAttributes().getNamedItem("Form").getNodeValue().replace(".xml", "");
				if (!form.equals("") && !form.equals(null)) {
					AppManager.setDefaultForm(form);
				}
			}
		} catch (Exception ex) {

		}
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
		removeDialog(1);
		removeDialog(2);
		removeDialog(3);
	}

	@Override
	public void onDestroy() {
		if (mMapView != null) {
			try {
				mMapView.onDestroy();
			} catch (NullPointerException e) {

			}
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mMapView != null) {
			mMapView.onLowMemory();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView != null) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	private void ShowSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(self);
		if (sharedPref.getString("admin_password", "").equals("")) {
			startActivity(new Intent(self, AppSettings.class));
		} else {
			showDialog(3);
		}
	}

	private Dialog showPasswordDialog() {
		final Dialog passwordDialog = new Dialog(this);
		passwordDialog.setTitle(getString(R.string.admin_password));
		passwordDialog.setContentView(R.layout.admin_password_dialog);
		passwordDialog.setCancelable(true);

		final EditText txtPassword = passwordDialog.findViewById(R.id.txtPassword);

		Button btnSet = passwordDialog.findViewById(R.id.btnSet);
		btnSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(self);
				if (sharedPref.getString("admin_password", "").equals(txtPassword.getText().toString())) {
					passwordDialog.dismiss();
					startActivity(new Intent(self, AppSettings.class));
				} else {
					Alert("Invalid password");
				}
			}
		});

		return passwordDialog;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 44)
			return showSyncPasswordDialog();
		else
			return showPasswordDialog();
	}

	private void Alert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create();
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem mnuSave = menu.add(8000, 6001, 0, R.string.menu_settings);
		mnuSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		mnuSave.setIcon(android.R.drawable.ic_menu_preferences);

		MenuItem mnuExport = menu.add(8000, 6003, 1, R.string.menu_export_all);
		mnuExport.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPref.getString("cloud_service", "").equals("Box")) {
			mnuBoxSignin = menu.add(8000, 9001, 2, R.string.box_signin);
			mnuBoxSignin.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

			mnuBoxSignout = menu.add(8000, 9002, 2, R.string.box_signout);
			mnuBoxSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

			if (BoxClient.isAuthenticated(this)) {
				mnuBoxSignout.setVisible(true);
				mnuBoxSignin.setVisible(false);
			} else {
				mnuBoxSignout.setVisible(false);
				mnuBoxSignin.setVisible(true);
			}
		}

		MenuItem mnuCloud = menu.add(8000, 6004, 3, R.string.menu_cloud_sync);
		mnuCloud.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		MenuItem mnuSyncFile = menu.add(8000, 6006, 4, R.string.menu_sync_file);
		mnuSyncFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		MenuItem mnuHelp = menu.add(8000, 6002, 5, R.string.menu_help);
		mnuHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 6001:
				ShowSettings();
				return true;
			case 6002:
				Uri uriUrl = Uri.parse("https://goo.gl/41Hdnb");
				startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
				return true;
			case 6003:
				ExportAllData();
				return true;
			case 6004:
				doCloudSync();
				return true;
			case 6006:
				showDialog(44);
				return true;
			case 9001:
				BoxSignin();
				return true;
			case 9002:
				BoxSignout();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void BoxSignin() {
		new BoxClient("Locations", this);
	}

	private void BoxSignout() {
		BoxClient.SignOut(this);
		mnuBoxSignin.setVisible(true);
		mnuBoxSignout.setVisible(false);
	}

	public void OnBoxLoggedIn() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					mnuBoxSignin.setVisible(false);
					mnuBoxSignout.setVisible(true);
				} catch (Exception ex) {

				}
			}
		});
	}

	private String GetHandshakeContents() {
		return "<?xml version=\"1.0\"?><Handshake ClientId=\"90fdc40c-f53d-4e66-930c-261b05a1d84b\"/>";
	}

	private void LoadMap() {
		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File geoPath = new File(basePath + "/EpiInfoEntomology");
		geoPath.mkdirs();
		String[] files = geoPath.list(new ExtFilter("kml", "kmz", "_"));
		if (files == null || files.length == 0) {
			try {
				AssetManager am = getAssets();
				File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfoEntomology/default.kml");
				InputStream in = am.open("default.kml");
				FileOutputStream f = new FileOutputStream(destinationFile);
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = in.read(buffer)) > 0) {
					f.write(buffer, 0, len1);
				}
				f.close();
			} catch (Exception ex) {

			}
		}

		mMapView.getMapAsync(this);
	}

	private void GetEntoForms() {

		String suffix = this.getString(R.string.lang_suffix);

		AssetManager am = getAssets();
		String locationFileName = "";
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPref.getBoolean("lab_mode", false)) {
			locationFileName = "LocationsLab" + suffix + ".xml";
		} else {
			locationFileName = "Locations" + suffix + ".xml";
		}
		try {
			File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfoEntomology/Questionnaires/Locations.xml");
			InputStream in = am.open(locationFileName);
			FileOutputStream f = new FileOutputStream(destinationFile);
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {

		}

		try {
			LinkedList<String> fileNames = new LinkedList<String>();
			fileNames.add("_Collection.xml");
			fileNames.add("_Collectionlab.xml");
			fileNames.add("_BottleBioassay.xml");
			fileNames.add("_BottleBioassayLab.xml");
			fileNames.add("_Conebioassay.xml");
			fileNames.add("_ConebioassayLab.xml");
			fileNames.add("_Trapping.xml");
			fileNames.add("_TrappingLab.xml");
			fileNames.add("_Vectorcontrol.xml");

			for (int x = 0; x < fileNames.size(); x++) {
				String fileName = fileNames.get(x);
				File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EpiInfoEntomology/Questionnaires/" + fileName);
				InputStream in = am.open(fileName.replace(".xml", suffix + ".xml"));
				FileOutputStream f = new FileOutputStream(destinationFile);
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = in.read(buffer)) > 0) {
					f.write(buffer, 0, len1);
				}
				f.close();
			}
		} catch (Exception e) {

		}

		new Thread(new Runnable() {
			public void run() {
				File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");
				EpiDbHelper db = new EpiDbHelper(self, new FormMetadata("EpiInfoEntomology/Questionnaires/Locations.xml", self), "Locations");
				db.open();
				String[] files = quesPath.list(new ExtFilter("xml", null));
				if (files != null) {
					for (int x = 0; x < files.length; x++) {
						int idx = files[x].indexOf(".");
						String viewName = files[x].substring(0, idx);
						FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/" + viewName + ".xml", self);

						if (viewName.startsWith("_")) {
							viewName = viewName.toLowerCase();
						}

						db.CreateUpdateTable(formMetadata, viewName);

					}
				}
			}
		}).start();
	}

	private boolean SyncAllData(AsyncTask asyncTask) {
		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml", null));
		boolean retval = false;
		if (files != null) {
			for (int x = 0; x < files.length; x++) {
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);
				FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/" + viewName + ".xml", this);

				if (viewName.startsWith("_")) {
					viewName = viewName.toLowerCase();
				}

				EpiDbHelper mDbHelper = new EpiDbHelper(this, formMetadata, viewName);
				mDbHelper.open();

				int status = mDbHelper.SyncWithCloud(asyncTask);
				retval = retval || (status > -1);
			}
		}
		return retval;
	}

	private void ExportAllData() {
		Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_LONG).show();

		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml", null));
		if (files != null) {
			for (int x = 0; x < files.length; x++) {
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);
				FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/" + viewName + ".xml", this);

				if (viewName.startsWith("_")) {
					viewName = viewName.toLowerCase();
				}

				EpiDbHelper mDbHelper = new EpiDbHelper(this, formMetadata, viewName);
				mDbHelper.open();

				new CsvFileGenerator().Generate(this, mDbHelper, formMetadata, viewName);
			}

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
							.setSmallIcon(android.R.drawable.stat_notify_sdcard)
							.setContentTitle("Epi Info")
							.setContentText(getString(R.string.csv_location) + " Download/EpiInfoEntomology/Output");

			int mNotificationId = Calendar.getInstance().getTime().getSeconds() * 10000 + new java.util.Random().nextInt(9999);
			NotificationManager mNotifyMgr =
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotifyMgr.notify(mNotificationId, mBuilder.build());

		}
	}

	private void GenerateSyncFiles(String password) {
		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfoEntomology/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml", null));
		if (files != null) {
			for (int x = 0; x < files.length; x++) {
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);
				FormMetadata formMetadata = new FormMetadata("EpiInfoEntomology/Questionnaires/" + viewName + ".xml", this);

				if (viewName.startsWith("_")) {
					viewName = viewName.toLowerCase();
				}

				EpiDbHelper mDbHelper = new EpiDbHelper(this, formMetadata, viewName);
				mDbHelper.open();

				Cursor syncCursor;
				if (viewName.startsWith("_")) {
					syncCursor = mDbHelper.fetchAllRecordsPlusFkey();
				} else {
					syncCursor = mDbHelper.fetchAllRecords();
				}
				new SyncFileGenerator(self).Generate(formMetadata, password, syncCursor, viewName, mDbHelper);
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {

				}
			}
		}
	}

	private Dialog showSyncPasswordDialog() {
		final Dialog passwordDialog = new Dialog(this);
		passwordDialog.setTitle(getString(R.string.sync_file_password));
		passwordDialog.setContentView(R.layout.password_dialog);
		passwordDialog.setCancelable(false);

		final EditText txtPassword = passwordDialog.findViewById(R.id.txtPassword);
		final EditText txtPasswordConfirm = passwordDialog.findViewById(R.id.txtPasswordConfirm);

		Button btnSet = passwordDialog.findViewById(R.id.btnSet);

		btnSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (txtPassword.getText().toString().equals(txtPasswordConfirm.getText().toString())) {
					txtPasswordConfirm.setError(null);
					new AsyncExporter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, txtPassword.getText().toString());
					((InputMethodManager) self.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
					Toast.makeText(self, getString(R.string.sync_file_started), Toast.LENGTH_LONG).show();
					passwordDialog.dismiss();
				} else {
					txtPasswordConfirm.setError(self.getString(R.string.not_match_password));
				}

			}
		});

		return passwordDialog;
	}

	private class AsyncExporter extends AsyncTask<String, Double, Boolean> {

		@Override
		protected Boolean doInBackground(String... password) {

			GenerateSyncFiles(password[0]);
			return true;
		}
	}

	private void doCloudSync() {
		Toast.makeText(self, getString(R.string.cloud_sync_started), Toast.LENGTH_LONG).show();
		new CloudSynchronizer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class CloudSynchronizer extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			return SyncAllData(this);
		}

		@Override
		protected void onPostExecute(Boolean status) {

			if (status) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(self)
						.setSmallIcon(R.drawable.ic_cloud_done)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(String.format(getString(R.string.cloud_sync_complete), getString(R.string.all)))
						.setContentText(getString(R.string.cloud_sync_complete_detail));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(8069, builder.build());
			} else {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(self)
						.setSmallIcon(R.drawable.ic_sync_problem)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(String.format(getString(R.string.cloud_sync_failed),  getString(R.string.all)))
						.setContentText(getString(R.string.cloud_sync_failed_detail));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(8069, builder.build());
			}
		}


	}

}