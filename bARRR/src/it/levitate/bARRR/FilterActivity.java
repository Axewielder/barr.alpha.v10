package it.levitate.bARRR;

import it.levitate.bARRR.mapOverlay.CustomItemizedOverlay;
import it.levitate.bARRR.mapOverlay.CustomOverlayItem;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class FilterActivity extends Activity {
	
	CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay;
	SeekBar distanceBar;
	EditText distanceText;
	double distance;
	static List<Overlay> overlayList;
	static Drawable d3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter); 
        
        
        d3 = this.getResources().getDrawable(R.drawable.marker3);
		overlayList = TabLayoutActivity.getInstance().map.getOverlays();
		
		distanceBar = (SeekBar) findViewById(R.id.distanceBar);
		distanceText = (EditText) findViewById(R.id.distanceText);
	    distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	        	distanceText.setText(Integer.toString(progress));
	        	distance = progress;
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {}
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
		
    }
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();
    	AndroidTools.vibrateFor(10, getApplicationContext());
    }
    
    
    
public ArrayList<ParseObject> nearByLocations(double latitude, double longitude, double maxDistance) throws ParseException{
        
    	double lat = latitude;
    	double longi = longitude;
    	double mD = maxDistance;
    	
    	ParseGeoPoint point = new ParseGeoPoint();
    	point.setLatitude(lat);
    	point.setLongitude(longi);
    	
    	ParseQuery query = new ParseQuery("BarObject");
    	query.whereWithinKilometers("location", point, mD);
    	query.setLimit(10);
    	ArrayList<ParseObject> nearPlaces = query.find();
		
		return nearPlaces;
		
    }
    //public void addParseGeoPoints(ArrayList<ParseObject> ParseArray){
    public void addParseGeoPoints(double currentLat, double currentLongi) throws ParseException{
    	double parseLat = currentLat;
    	double parseLongi = currentLongi;
    	
    	ArrayList<ParseObject> ParseArray = nearByLocations(parseLat, parseLongi, distance);
    	int parseSize = ParseArray.size();
    	String parseElements = Integer.toString(parseSize);
    	
    	
    	Toast.makeText(this, parseElements, Toast.LENGTH_LONG).show();
    	TabLayoutActivity.getInstance().initMap();
    	TabLayoutActivity.getInstance().locationClick();
    	for (int i = 0; i < ParseArray.size(); i++) {
    		ParseObject barObject = ParseArray.get(i);
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
		 	    		 		
		 	    		itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d3, TabLayoutActivity.map);
		 				CustomOverlayItem overlayItemX = new CustomOverlayItem(point, barName, address+" "+addressNumber+"\n"+city +"\n"+AndroidTools.distanceTo(TabLayoutActivity.lat1, TabLayoutActivity.longi1, latitudeE6/1E6, longitudeE6/1E6, true),
		 				"http://maps.googleapis.com/maps/api/streetview?size=300x300&location="+latitudeE6/1E6+","+longitudeE6/1E6+"&fov=90&sensor=false");
		 				itemizedOverlay.addOverlay(overlayItemX);
		 				
		 				overlayList.add(itemizedOverlay);
		 				
    	}}
    		
			if(!TabLayoutActivity.getInstance().mapDrawer.isOpened())
			TabLayoutActivity.getInstance().mapDrawer.animateToggle();
    	
    }
    
    
    public void onClick (View view){
    	switch (view.getId()){
case R.id.parseButton:
	//add
	
	try {
		addParseGeoPoints(TabLayoutActivity.lat1, TabLayoutActivity.longi1);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

	//addMarkersFromParse();
	//finish();
	break;
    
    	}}
    
    
}