package id.starkey.mitra.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import id.starkey.mitra.ConfigLink;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = ConfigLink.loginPref;
	
	// All Shared Preferences Keys
	private static final String TAG_ID = "idUser";
	public static final String TAG_NAMA = "namaUser";
	public static final String TAG_PHONE = "phoneUser";
	public static final String TAG_EMAIL= "emailUser";
	public static final String TAG_TOKEN= "tokenIdUser";
	public static final String TAG_ROLE= "roleUser";

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public String getID(){
		return pref.getString(TAG_ID, "");
	}

	public String getNama(){
		return pref.getString(TAG_NAMA, "");
	}

	public String getRole(){
		return pref.getString(TAG_ROLE, "");
	}

	public String getPhone(){
		return pref.getString(TAG_PHONE, "");
	}

	public String getEmail(){
		return pref.getString(TAG_EMAIL, "");
	}

	public String getToken(){
		return pref.getString(TAG_TOKEN, "");
	}

}
