package it.levitate.bARRR;


import com.facebook.android.Hackbook;

import it.levitate.bARRR.listView.ListViewActivity;

import it.levitate.bARRR.TabLayoutActivity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SettingsActivity extends PreferenceActivity { 
	
	private static SettingsActivity theInstance;
	
	public static SettingsActivity getInstance() {
        return SettingsActivity.theInstance;
    }
	public SettingsActivity() {
		SettingsActivity.theInstance = this;
    }
	
	
	  @Override 
	  public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.settings);
	    addPreferencesFromResource(R.xml.preferences); 
	  /*
	    WebView webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://ITCOM.AAU.DK");
	  */
	  Preference about = (Preference) findPreference("about");
	  about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   //TabLayoutActivity.getInstance().tabHost.setCurrentTab(2);
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                   
	               }
	           });
	  
	  Preference jsontw = (Preference) findPreference("jsontw");
	  jsontw.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   startActivity(new Intent(SettingsActivity.this, JSONtw.class));
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                   
	               }
	           });
	  
	  Preference hackbook = (Preference) findPreference("hackbook");
	  hackbook.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   //startActivity(new Intent(SettingsActivity.this, Hackbook.class));
	            	   Intent hackbookIntent = new Intent().setClass(SettingsActivity.this, Hackbook.class);
	   				startActivity(hackbookIntent);
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                  
	               }
	           });
	  
	  Preference awesomepager = (Preference) findPreference("awesomepager");
	  awesomepager.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   Intent hackbookIntent = new Intent().setClass(SettingsActivity.this, AwesomePagerActivity.class);
		   				startActivity(hackbookIntent);
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                   
	               }
	           });
	  
	  Preference jsonfb = (Preference) findPreference("jsonfb");
	  jsonfb.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   startActivity(new Intent(SettingsActivity.this, JSONfb.class));
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                   
	               }
	           });
	  
	  Preference listview = (Preference) findPreference("listview");
	  listview.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   startActivity(new Intent(SettingsActivity.this, ListViewActivity.class));
	            	   closeOptionsMenu();
	            	   finish();
	            	  
	            	     
	            	   
					return false;
	                  
	               }
	           });
	  
	  Preference maplogger = (Preference) findPreference("maplogger");
	  maplogger.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   
	            	   startActivity(TabLayoutActivity.getInstance().MapLoggerIntent);
	            	   
	            	   closeOptionsMenu();
	            	   finish();
	            	 
	            	   
					return false;
	                   
	               }
	           });
	  
	  Preference marker = (Preference) findPreference("marker");
	  marker.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   startActivity(TabLayoutActivity.getInstance().AddMarkersIntent);
	            	 
	            	   closeOptionsMenu();
	            	   finish();
	            	   
					return false;
	                  
	               }
	           });
	  
	  Preference mapthumbnail = (Preference) findPreference("mapthumbnail");
	  mapthumbnail.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   startActivity(new Intent(SettingsActivity.this, MapThumbnail.class));
	            	   closeOptionsMenu();
	            	   finish();
					return false;
	                   
	               }
	           });
	  
	  }
	  
	    @Override
	    public boolean onPrepareOptionsMenu (Menu menu) 
	    {
	    	super.onCreateOptionsMenu(menu);
	    	finish();
	    	closeOptionsMenu();
	        return true;
	    }
	  
	}