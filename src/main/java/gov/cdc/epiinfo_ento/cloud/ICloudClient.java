package gov.cdc.epiinfo_ento.cloud;

import android.content.ContentValues;

import org.json.JSONArray;

import gov.cdc.epiinfo_ento.EpiDbHelper;

public interface ICloudClient {

	JSONArray getData(boolean downloadImages, boolean downloadMedia, EpiDbHelper dbHelper);

	boolean insertRecord(ContentValues values);

	boolean deleteRecord(String recordId);

	boolean updateRecord(String recordId, ContentValues values);
	
}
