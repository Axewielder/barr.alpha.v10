package it.levitate.bARRR;

import it.levitate.bARRR.mapOverlay.AddMarkersActivity;
import it.levitate.bARRR.mapOverlay.CustomItemizedOverlay;
import it.levitate.bARRR.mapOverlay.CustomOverlayItem;
import it.levitate.bARRR.mapOverlay.MapLoggerActivity;
import it.levitate.bARRR.mapOverlay.MyLocation;
import it.levitate.bARRR.mapOverlay.MyLocation.LocationResult;
import it.levitate.bARRR.social.SocialActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SlidingDrawer;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TabLayoutActivity extends TabbedMapActivity {
	
	//Tablayout
	LinearLayout linearLayoutMenu; // has to be there for the menu
	int lastTabId = R.id.imageButton1;
	public TabHost tabHost;
	
	//MapView
	public static MapView map;
	MyLocationOverlay mapOverlay;
	public MapController controller;
	int x,y;
	GeoPoint touchedPoint;
	Drawable d, d2;
	static Drawable d3;
	Drawable gps;
	public static List<Overlay> overlayList;
	String towers;
	int lat, longi;
	public static double lat1, longi1;
	TouchOverlay t;
	

    public GeoPoint currentLocation, Destination;
    String A1, foundAddress, fullAddress;
    public static String tappedLocations;
    public static List<GeoPoint> pointsList;
    double touchedLat, touchedLongi;
    public CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay;
	CustomItemizedOverlay<CustomOverlayItem> myLocationOverlay;
	CustomItemizedOverlay<CustomOverlayItem> StartPointOverlay;
    
	
	EditText editText;
	Context c;
	
	
	
	
	//Other
	public SlidingDrawer mapDrawer;
	public boolean onKeyDownEnabled = false;
	long start;
	long stop;
	int switch1;
	 public Intent AddMarkersIntent, MapLoggerIntent;
	    public boolean logTaps = false;
	    public boolean logSaveTaps = false;
	
	
	private static TabLayoutActivity theInstance;
	public static TabLayoutActivity getInstance() {
		return TabLayoutActivity.theInstance;
	}

	public TabLayoutActivity() {
		TabLayoutActivity.theInstance = this;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Parse.initialize(this, "TNkwkJHARFHKRhtI63KioBB6NJrf7LImBYz3gkoN", 
				"GXiyK1juAZ7qVfztAUJq1mxOWW0iDLmtNi9T4Wbx"); 
		//Tablayout
		linearLayoutMenu = (LinearLayout) findViewById(R.id.linearLayoutMenu); //Cast the menu
		tabHost = (TabHost) findViewById(R.id.my_tabhost); // The activity TabHost
		tabHost.setup(mLocalActivityManager);
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		
		//MapView
		map = (MapView) findViewById(R.id.mapview2);
		mapDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		c = map.getContext();
		
		
		gps = getResources().getDrawable(R.drawable.gps_location);
        d = getResources().getDrawable(R.drawable.marker);
        d2 = getResources().getDrawable(R.drawable.marker2);
        d3 = getResources().getDrawable(R.drawable.marker3);
        
        LinearLayout zoomLayout =(LinearLayout)findViewById(R.id.layout_zoom);
		View zoomView = map.getZoomControls();
		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
		map.displayZoomControls(true);
		
		editText=(EditText)findViewById(R.id.editText1);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            performSearch();
		            return true;
		        }
		        return false;
		    }
		});
		
		pointsList = new ArrayList<GeoPoint>();
        AddMarkersIntent = new Intent(TabLayoutActivity.this, AddMarkersActivity.class);
        MapLoggerIntent = new Intent(TabLayoutActivity.this, MapLoggerActivity.class);
		
		checkedLocationSettings(true);
        initMap();
        addMarkersFromParse();
		locationClick();
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, HomeActivity.class); // Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("home")
				.setIndicator("Home")
				.setContent(intent);
		tabHost.addTab(spec); // Do the same for the other tabs

		intent = new Intent().setClass(this, FilterActivity.class);
		spec = tabHost
				.newTabSpec("filter")
				.setIndicator("Filter")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, TabLayoutActivity.class); // this should be set to the Activity in the Google API
		spec = tabHost.newTabSpec("map")
				.setIndicator("Map")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FavoriteActivity.class);
		spec = tabHost
				.newTabSpec("favorite")
				.setIndicator("Favorite")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SocialActivity.class);
		spec = tabHost
				.newTabSpec("check in")
				.setIndicator("Check in")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, AwesomePagerActivity.class);
		spec = tabHost
				.newTabSpec("ViewPager")
				.setIndicator("ViewPager")
				.setContent(intent);
		tabHost.addTab(spec);

		// The first view to be startup standard
		tabHost.setCurrentTab(0);

	} // end of onCreate

	@Override
	public void onPause() {
		super.onPause();
		mapOverlay.disableCompass();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapOverlay.enableCompass();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			// Toast.makeText(this,"KEYCODE_SEARCH", Toast.LENGTH_LONG).show();
			//tabHost.setCurrentTab(2);
			// TabLayoutActivity.getInstance().getTabHost().setCurrentTab(2);
			if(lastTabId != R.id.imageButton3){
				//mapDrawer.isOpened()==false
				mapDrawer.animateToggle();

			}
			
			if (onKeyDownEnabled) {
				onKeyDownEnabled = false;
				AndroidTools.keyFocus(onKeyDownEnabled, editText, c);
				
			} else {
				onKeyDownEnabled = true;
				AndroidTools.keyFocus(onKeyDownEnabled, editText, c);
				
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	
	
	/*//zoom in and out with the volume keys... Not working anymore :(
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
	    	//Zoom not closer than possible
	    	controller.setZoom(Math.min(21, map.getZoomLevel() + 1));
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	    	//Zoom not farer than possible 
	    	controller.setZoom(Math.max(1, map.getZoomLevel() - 1));
            return true;
        } 
        return false;
    }
	*/
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		startActivity(new Intent(this, SettingsActivity.class));
		closeOptionsMenu();
		// ListView list = (ListView)myLayout.findViewById(R.id.streamListView);
		// ProgressBar bar =
		// (ProgressBar)myLayout.findViewById(R.id.streamProgressBar);
		return true;
	}

	public void onClick(View view) { // This is the onClick for the menu buttons

		if (lastTabId != view.getId()) // if the last ImageView is not the same
										// as the last ImageView, then change
										// the image
		{
			switch (view.getId()) {
			case R.id.imageButton1:
				TabLayoutActivity.getInstance().tabHost.setCurrentTab(0);
				((ImageView) view).setImageResource(R.drawable.menu_update_focused);
				break;
			case R.id.imageButton2:
				TabLayoutActivity.getInstance().tabHost.setCurrentTab(1);
				((ImageView) view).setImageResource(R.drawable.menu_filter_focused);
				break;
			case R.id.imageButton3:
				mapDrawer.animateToggle();
				((ImageView) view).setImageResource(R.drawable.menu_map_focused);
				break;
			case R.id.imageButton4:
				TabLayoutActivity.getInstance().tabHost.setCurrentTab(3);
				((ImageView) view).setImageResource(R.drawable.menu_favorite_focused);
				break;
			case R.id.imageButton5:
				TabLayoutActivity.getInstance().tabHost.setCurrentTab(4);
				((ImageView) view).setImageResource(R.drawable.menu_social_focused);
				break;

			} // end switch

			switch (lastTabId) {
			case R.id.imageButton1:
				((ImageView) linearLayoutMenu.findViewById(lastTabId))
						.setImageResource(R.drawable.menu_update_unfocused);
				break;
			case R.id.imageButton2:
				((ImageView) linearLayoutMenu.findViewById(lastTabId))
						.setImageResource(R.drawable.menu_filter_unfocused);
				break;
			case R.id.imageButton3:
				mapDrawer.animateToggle();
				((ImageView) linearLayoutMenu.findViewById(lastTabId))
				.setImageResource(R.drawable.menu_map_unfocused);
				break;
			case R.id.imageButton4:
				((ImageView) linearLayoutMenu.findViewById(lastTabId))
						.setImageResource(R.drawable.menu_favorite_unfocused);
				break;
			case R.id.imageButton5:
				((ImageView) linearLayoutMenu.findViewById(lastTabId))
						.setImageResource(R.drawable.menu_social_unfocused);
				break;

			} // end switch
		} // end of if statement

		lastTabId = view.getId();
	} // end of onClick
	

	
	//VOID: Currently only calls keyFocus(false):
	public void mapClickHandler (View view){
    		keyFocus(false);
    		
    	}
	
	
	
	//Hide or show keyboard and request and clear focus from editText1.
			public void keyFocus (boolean is){
				// Old version: //public void hideKeyFocus (View view){}
				EditText editText=(EditText)findViewById(R.id.editText1);
				if(is==true){
					editText.requestFocus();
					InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.showSoftInput(editText, 0);
					
				}
				else{
					editText.clearFocus();
					editText.setText("");
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					
					// see http://androidsnippets.com/how-to-remove-focus-and-hide-the-on-screen-keyboard
				}
			}
			

			@Override
			protected boolean isRouteDisplayed() {
				// TODO Auto-generated method stub
				return false;
			}
		    
			
			boolean setCurrentTab(int i) {
		        if (getParent() instanceof TabLayoutActivity) {
		            ((TabLayoutActivity) getParent()).tabHost.setCurrentTab(i);
		            return true;
		        }
		        return false;
		    }
		 /*  
			private void initLocation(){
				 
		        myLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
		        //lm.requestLocationUpdates(provider, minTime, minDistance, intent)
		        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, intent)
		}
		*/	
			public void initMap(){
		        overlayList = map.getOverlays();
		        overlayList.clear();
		        TouchOverlay t = new TouchOverlay(null, map);
		        overlayList.add(t);
		        mapOverlay = new MyLocationOverlay(TabLayoutActivity.this, map);
		        overlayList.add(mapOverlay);
		        //locationClick();
		        //addMarkersFromParse();
		        //addSecondaryLocations();
			}

			
		    MyLocation myLocation = new MyLocation();
		    public void locationClick() {
		        myLocation.getLocation(this, locationResult);
		    } //end of locationClick()

		    public LocationResult locationResult = new LocationResult(){
		        @Override
		        public void gotLocation(final Location location){
		            //Got the location!
		        	lat = (int) (location.getLatitude() *1E6);
		        	longi = (int) (location.getLongitude() *1E6);
		        	lat1 = (location.getLatitude());
		        	longi1 = (location.getLongitude());
		        	currentLocation = new GeoPoint(lat, longi);
		        	String pinAddress = getAddress(currentLocation);
					
		           
		           /*//////////My Location///////////////
		        	* something something... 
		        	* 
		        	* 
		        	***********************************/ 
		        	myLocationOverlay = new CustomItemizedOverlay<CustomOverlayItem>(gps, map);
					
					controller = map.getController();
		            controller.animateTo(currentLocation);
		            controller.setZoom(15);
		            
		            CustomOverlayItem overlayItemMe = new CustomOverlayItem(currentLocation, "Current location:", 
		            pinAddress, "http://maps.googleapis.com/maps/api/streetview?size=300x300&location="+lat1+","+longi1+"&fov" +
		            "=90&sensor=false");
		            myLocationOverlay.addOverlay(overlayItemMe);
		            
		            overlayList.add(myLocationOverlay);
		            
		            }
		        
		        
		        
		    }; //end of LocationResult
		    
		    
		    public static void addMarkersFromParse(){
		 		ParseQuery query = new ParseQuery("BarObject");
		 		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		 		query.findInBackground(new FindCallback() {
		 		public void done(List<ParseObject> barList, ParseException e) {
		 	        if (e == null) {
		 	        	
		 	        	for(int i = 0; i < barList.size(); i++)
		 	        	{
		 	        	//Get first row from DB
	 	        		ParseObject barObject = barList.get(i);
		 	    		
	 	        		//Get Informations
	 	        		String barName = barObject.getString("BarName");
	 	           		String address = barObject.getString("Address");
						int addressNumber = barObject.getInt("AddressNumber");
						String city = barObject.getString("City");
	 	        		
		 	        	//Get GeoPoint
		 	            String pointFromParse = barObject.getString("GeoPoint");
		 	    		//Split comma-separated GeoPoint String
		 	            if(pointFromParse==null){}else{
		 	    		String[] separated = pointFromParse.split(",");
		 	    		// I suppose, this will contain latitude
		 	    		double latitudeE6 = Double.parseDouble(separated[0])*1E6; 
		 	    		// I suppose, this will contain longitude
		 	    		double longitudeE6 = Double.parseDouble(separated[1])*1E6; 
		 	    		GeoPoint point = new GeoPoint((int)latitudeE6, (int)longitudeE6);
		 	    		 		
		 	    		CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d3, map);
		 				CustomOverlayItem overlayItemX = new CustomOverlayItem(point, barName, address+" "+addressNumber+"\n"+city +"\n"+AndroidTools.distanceTo(TabLayoutActivity.lat1, TabLayoutActivity.longi1, latitudeE6/1E6, longitudeE6/1E6, true),
		 				"http://maps.googleapis.com/maps/api/streetview?size=300x300&location="+latitudeE6/1E6+","+longitudeE6/1E6+"&fov=90&sensor=false");
		 				itemizedOverlay.addOverlay(overlayItemX);
		 		  	  	
		 		  	  	
		 				overlayList.add(itemizedOverlay);
		 	            }
		 	        	}
		 	    		
		 	        	Log.d("Bar(s)", "Retrieved: " + barList.size()+" - from ParseDB");
		 	    		
		 	        } else {
		 	            Log.d("Parse", "Error: " + e.getMessage());
		 	        }
		 	    }
		 	});
		 		
		   
		   
	   } 
		    
		    //Hardcoded locations
		    public void addSecondaryLocations(){
		    	//Create new CustomItemizedOverlay:
		        itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d3, map);
		        
		        ////////////First Location ///////////////
		        GeoPoint point = new GeoPoint((int)(51.5174723*1E6),(int)(-0.0899537*1E6));
				CustomOverlayItem overlayItem1 = new CustomOverlayItem(point, "Tomorrow Never Dies (1997)", 
						"(M gives Bond his mission in Daimler car)", 
						"http://maps.googleapis.com/maps/api/streetview?size=300x300&location=51.5174723,-0.0899537&fov=90&sensor=false");
				itemizedOverlay.addOverlay(overlayItem1);
				
				
		        //////////// Second Location ///////////////
		        GeoPoint point2 = new GeoPoint((int)(51.515259*1E6),(int)(-0.086623*1E6));
				CustomOverlayItem overlayItem2 = new CustomOverlayItem(point2, "London", 
						"(This is the london marker)", 
						"http://maps.googleapis.com/maps/api/streetview?size=300x300&location=51.515259,-0.086623&fov=90&sensor=false");
				itemizedOverlay.addOverlay(overlayItem2);
		        
				
				//////////// Third Location ///////////////
				GeoPoint point3 = new GeoPoint((int)(55.730448*1E6),(int)(12.400287*1E6));
				CustomOverlayItem overlayItem3 = new CustomOverlayItem(point3, "Verners Baggaard", 
						"(M gives Bond his mission in Daimler car)", 
						"http://maps.googleapis.com/maps/api/streetview?size=300x300&location=55.730448,12.400287&fov=90&sensor=false");
				itemizedOverlay.addOverlay(overlayItem3);
				
			
				//Add itemizedOverlay to the overlayList
				overlayList.add(itemizedOverlay);
		    	
		    }
		    
			public class TouchOverlay extends Overlay{
			    private Context mContext;
			    
				public TouchOverlay(Context context, MapView map) {
					super();
					mContext = context;
					
					// TODO Auto-generated constructor stub
					
				}
				
			    public boolean onTouchEvent(MotionEvent e, MapView m){
					if (e.getAction()== MotionEvent.ACTION_DOWN){
					switch1 = 1;
					start = e.getEventTime();
					x = (int) e.getX();
					y = (int) e.getY();
					 touchedPoint = map.getProjection().fromPixels(x, y);
					 mapClickHandler(map);
					}
				/*	//not used...yet?
				   if(e.getAction()== MotionEvent.ACTION_MOVE){
						
					}

					if(e.getAction()== MotionEvent.ACTION_UP){
						
					} */
					if(switch1==1 && e.getEventTime()-start>800){
						AndroidTools.vibrateFor(42, c);
						switch1=0;
						stop=e.getEventTime();
					}
					
					if(switch1==0 && e.getEventTime()-stop>300){
						AndroidTools.vibrateFor(42, c);
						switch1=2;
						start=stop;
						openOnTouchSettings();
					}
					return false;
					
				}
			    
				@Override //shows coordinates for the tapped spot.
				public boolean onTap(GeoPoint point, MapView map) 
				 {
				   Context contexto = map.getContext();
				   String msg = "Lat: " + point.getLatitudeE6()/1E6 + " - " + 
				                "Lon: " + point.getLongitudeE6()/1E6;
				   Toast toast = Toast.makeText(contexto,"Hold down to set a new start point!\n"+ msg, Toast.LENGTH_SHORT);
				   toast.show();
				   
				   for (Overlay overlay : map.getOverlays()) {
						if (overlay instanceof CustomItemizedOverlay<?>) {
							((CustomItemizedOverlay<?>) overlay).hideBalloon();
						}
					}
				   
				   touchedLat = point.getLatitudeE6();
				   touchedLongi = point.getLongitudeE6();
				   
				   if(logTaps){
				   
					   //startActivity(MapLoggerIntent);
					   tappedLocations += "\"" + point.getLatitudeE6()/1E6 +"\"" +","+ "\""+point.getLongitudeE6()/1E6+"\""+",\n"; 
				   
					   MapLoggerActivity.getInstance().tappedLocations = "";
					   MapLoggerActivity.getInstance().tappedLocations += tappedLocations;
					   TabLayoutActivity.getInstance().tabHost.setCurrentTab(2);
				   }
				   
				   if(logSaveTaps){
				   pointsList.add(new GeoPoint((int)touchedLat, (int)touchedLongi));  
					   
				   }
				   
				   
				   
				  /*
				   
				   Arrays.asList(list.split(separator));
				   String[] pieces = textAreaValue.split( "\n" );
				   List<String> list = Arrays.asList( pieces );
				   
				   tappedLocations.spl
				   
				   (Arrays.asList(Pattern.compile("!!").split(input))
				   
					*/
				   
					/*
				   
				   Bundle lat = new Bundle();
				   lat.putInt("lat", (int) (point.getLatitudeE6()));
				   Bundle longi = new Bundle();
				   longi.putInt("longi", (int) (point.getLongitudeE6()));

				   AddMarkersIntent.putExtras(lat);
				   AddMarkersIntent.putExtras(longi);

					//startActivityForResult(newIntent, 0);
				   //startActivity(newIntent);
				   
				   //anotherActivityIntent.putExtra("my.package.dataToPass",point);
				   //startActivity(new Intent(TabLayoutActivity.this, AddMarkersActivity.class));
					
				  */
					 
				   
				   /*
				   if (addresses.size() > 0) {
		               StringBuilder result = new StringBuilder();
		               for(int i = 0; i < addresses.size(); i++){
		                   Address address =  addresses.get(i);
		                   int maxIndex = address.getMaxAddressLineIndex();
		                   for (int x = 0; x <= maxIndex; x++ ){
		                       result.append(address.getAddressLine(x));
		                       result.append(",");
		                   }               
		                   result.append(address.getLocality());
		                   result.append(",");
		                   result.append(address.getPostalCode());
		                   result.append("\n\n");
		               }
		               addressText.setText(result.toString());
		           }
				   */
				   
				  return true;
				 }

				
			} //end of TouchMe
					
						
			// switch between Satellite and StreetView:
			public void setView(){
						if (map.isSatellite()){
							map.setSatellite(false);
							map.setStreetView(true);
						}else{
							map.setStreetView(false);
							map.setSatellite(true);
						}
						
					}
		

		    
			//INPUT: GeoPoint //RETURN: Address of GeoPoint as String.
			public String getAddress(GeoPoint geoPoint){
				Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
				try{ List<Address> address = geocoder.getFromLocation(geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6, 1); 
				if (address.size()> 0){
					String foundAddress = "";
					for(int i=0; i<address.get(0).getMaxAddressLineIndex(); i++){
						
						foundAddress += address.get(0).getAddressLine(i) + "\n";
					}
					return foundAddress;
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					
				}
				return null ;	
				
			}
			
			
			//INPUT: Address //OUTPUT: Destination GeoPoint
			public GeoPoint addressToGeo (String Address) {
				if (!AndroidTools.isOnline()){
					alertDialog("You are offline", "You need an Internet connection to Search!");
					}else{
								
				String strAddress = Address;
				Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
				if (strAddress.length() > 2){
					try{
						List<Address> list = gc.getFromLocationName(strAddress, 1);
						if (list.size() == 0) {
			                return null;
			            }
						else{
						  Address address = list.get(0);

						  
						  
						  /*
						  //for API Level 9 or higher
						  if (!Geocoder.isPresent()){
						   Toast.makeText(AndroidgetFromLocationNameActivity.this,
						   "Sorry! Geocoder service not Present.",
						   Toast.LENGTH_LONG).show();
						  }
						 } */
						  
						  Destination = new GeoPoint((int) (address.getLatitude() * 1E6),
				                  (int) (address.getLongitude() * 1E6));
						  lat1 = address.getLatitude();
						  longi1 = address.getLongitude();
						  
						  fullAddress = getAddress(Destination);
						  
						 
						}}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (AndroidTools.isOnline()){
						Toast.makeText(TabLayoutActivity.this, "Almost there...",Toast.LENGTH_SHORT).show();
						//NOT THE BEST SOLUTION!
						addressToGeo(strAddress);
						strAddress ="";} else{
							alertDialog("You are offline", "You need an Internet connection to Search!");		
						}
						}
				}
				else if (strAddress.length() == 0){
					alertDialog("Search","You can't search for nothing...");
				}
			      else{
			    	  alertDialog("Google Maps",strAddress + " Is not a valid search term");   
			    	  
			      }}
				return Destination;
			} //end of addresseToGeo
			
			
			
			
			
			
				
			

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				//locationClick();
				
			}
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			/*///////////////////////////////////////////////////\
		   | - 1 -  ButtonHandler: Handles buttons in MapView. -  |
		    \\*///////////////////////////////////////////////////
				public void buttonClickHandler (View view){
					
					switch (view.getId()){
			    	
			    	case R.id.button1:
			    		if(checkedLocationSettings(true))
			    		initMap();
			    		addMarkersFromParse();
			    		locationClick();
						AndroidTools.vibrateFor(1500, c);
						AndroidTools.keyFocus(false, editText, c);
			    		break;
			    	
			    	case R.id.button3:
			    		AndroidTools.vibrateFor(42, c);
			    		//AndroidTools.directionsTo(lat1, longi1, lat2, longi2, c);
			    		
					break;
			    		
			    	}
					
				}

			public void performSearch(){
				AndroidTools.vibrateFor(333, c);
    			
    			EditText editText=(EditText)findViewById(R.id.editText1);
    			A1 = editText.getText().toString();
    			
    			if(addressToGeo(A1) != null){
    			
    			controller = map.getController();
				  controller.animateTo(Destination);
		          controller.setZoom(12);
		          StartPointOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d2, map);
		          
		          ////////////New start point ///////////////
		  		CustomOverlayItem overlayItemStart = new CustomOverlayItem(Destination, "New Start Point:", fullAddress, 
		  				"http://maps.googleapis.com/maps/api/streetview?size=300x300&location="+lat1+","+longi1+"&fov=90&sensor=false");
		  		StartPointOverlay.addOverlay(overlayItemStart);
		  		initMap();
		  		overlayList.add(StartPointOverlay);
    			}
    			
    			//Toast.makeText(this, (lat1+" : "+longi1+"  -  "+lat2+" : "+longi2), Toast.LENGTH_LONG).show();
				//AndroidTools.distanceTo(lat1, longi1, lat2, longi2, true);
				AndroidTools.keyFocus(false, editText, c);
				//arrowToDirection(currentLocation, Destination);
				
			}	
			
			private boolean checkedLocationSettings(boolean showGPSDisabledAlertToUser ){
					boolean showAlert = showGPSDisabledAlertToUser;
				  String GpsProvider = Settings.Secure.getString(getContentResolver(),
						  Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
						 
						if(GpsProvider.equals("") && showAlert!=true){
						 //GPS Disabled
							Toast.makeText(this, "Your GPS is disabled", Toast.LENGTH_SHORT).show();
							return false;
						}
						if(GpsProvider.equals("") && showAlert==true){
							 //GPS Disabled
								showGPSDisabledAlertToUser();
								return false;
							}
						else{
						 //GPS Enabled
							Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
						}
						return true;
			}
			
			private void showGPSDisabledAlertToUser(){
				 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				 alertDialogBuilder.setMessage("GPS is disabled on your device. Would you like to enable it?")
				 .setCancelable(false)
				 .setPositiveButton("Enable GPS",
				 new DialogInterface.OnClickListener(){
				 public void onClick(DialogInterface dialog, int id){
				 Intent callGPSSettingIntent = new Intent(
				 android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 startActivity(callGPSSettingIntent);

					 
				 }
				 });
				 alertDialogBuilder.setNegativeButton("Cancel",
				 new DialogInterface.OnClickListener(){
				 public void onClick(DialogInterface dialog, int id){
				 dialog.cancel();
				 }
				 });
				 AlertDialog alert = alertDialogBuilder.create();
				 alert.show();
				 }
			
				public boolean openOnTouchSettings() {
					AlertDialog alert= new AlertDialog.Builder(TabLayoutActivity.this).create();
					alert.setTitle("TouchedPoint Settings");
					alert.setMessage("Select an Action:");
					
					alert.setButton("New Start Point", new DialogInterface.OnClickListener() {
						
						//@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							
							String pinAddress = getAddress(touchedPoint);
							StartPointOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d2, map);
							lat1 = touchedLat / 1E6;
							longi1 = touchedLongi / 1E6;
					  		CustomOverlayItem overlayItem = new CustomOverlayItem(touchedPoint, (touchedLat / 1E6 +" : " + touchedLongi / 1E6), 
									pinAddress, 
					  				"http://maps.googleapis.com/maps/api/streetview?size=300x300&location="+touchedLat / 1E6 +","+touchedLongi / 1E6 +"&fov=90&sensor=false");
					  		StartPointOverlay.addOverlay(overlayItem);
					  				    						
							initMap();
							overlayList.add(StartPointOverlay);
						}
					});
						alert.setButton2("Get address!", new DialogInterface.OnClickListener() {
						
						//@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
								String display = getAddress(touchedPoint);
								Toast t = Toast.makeText(getBaseContext(), display, Toast.LENGTH_LONG);
								t.show();
							
						}
						});
						alert.setButton3("View modes", new DialogInterface.OnClickListener() {
			
						//@Override
						public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						setView();
						}		
					});
					alert.show();
					return true;
				}


				
				
				
			
				//INPUT: title, message. //OUTPUT: receive alertDialog with button
					public void alertDialog (String title, String message){
					String setTitle=title, setMessage=message;
					AlertDialog.Builder adb = new AlertDialog.Builder(TabLayoutActivity.this);
					adb.setTitle(setTitle);
					adb.setMessage(setMessage);
					adb.setPositiveButton("Close",null);
					adb.show();	
					}			
					
	
}// END