package it.levitate.bARRR.mapOverlay;

import it.levitate.bARRR.R;
import it.levitate.bARRR.TabLayoutActivity;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class AddMarkersActivity extends Activity {
	ListView listView;
	static List<GeoPoint> pointsList;
	GeoPoint gp;
	static List<Overlay> overlayList;
	static MapView map;
	static Drawable d3;
	boolean update = false;
	boolean stopupdate = false;
	int touchedLat, touchedLongi;
	ListAdapter myAdapter;
	ToggleButton tb;

	
	
	//Make methods from TabLayoutActivity available from other places:
		private static AddMarkersActivity theInstance;
		
		public static AddMarkersActivity getInstance() {
	        return AddMarkersActivity.theInstance;
	    }
		public AddMarkersActivity() {
			AddMarkersActivity.theInstance = this;
	    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		tb = (ToggleButton) findViewById(R.id.AddMarkerToggle);
		
		if (TabLayoutActivity.getInstance().logSaveTaps == true) 
    	{
    	  tb.setChecked(true);
    	}
    	else
    	{
    	  tb.setChecked(false);
    	}
		
				// setup list view
				listView = (ListView) findViewById(R.id.list);
				d3 = this.getResources().getDrawable(R.drawable.marker3);
				overlayList = TabLayoutActivity.getInstance().map.getOverlays();
				
				listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, TabLayoutActivity.getInstance().pointsList));
				myAdapter = listView.getAdapter();
				
				
				
				// add an onclicklistener to see point on the map
				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView parent, View view, int position, long id) {
						GeoPoint geoPoint = (GeoPoint) listView.getAdapter().getItem(position);
						if(geoPoint != null) {
							// have map view moved to this point
							//startActivity(new Intent(AddMarkersActivity.this, TabLayoutActivity.class));
							//programatically switch tabs to the map view
							//TabLayoutActivity.getInstance().tabHost.setCurrentTab(2);
							map = TabLayoutActivity.getInstance().map;
							
							
							CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d3, map);
							CustomOverlayItem overlayItemOnItemClick = new CustomOverlayItem(geoPoint, "Title#", "Description", 
									"http://maps.googleapis.com/maps/api/streetview?size=300x300&location=55.754074,12.466386&fov=90&sensor=false");
							itemizedOverlay.addOverlay(overlayItemOnItemClick);
							overlayList.add(itemizedOverlay);

							
					  	  	 //wrong one... does not expand...
							 //CustomItemizedOverlay.getInstance().onBalloonTap(position, overlayItem);
							
							
							TabLayoutActivity.getInstance().controller.animateTo(geoPoint);
				            TabLayoutActivity.getInstance().controller.setZoom(15);
				            finish();
							
							
						}
					}		
				});
				
	}
	
	@Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();
 
    }
    

  

    
    public void addMarkers(){
    	map = TabLayoutActivity.getInstance().map;
    	
	    for (int i = 0; i < TabLayoutActivity.getInstance().pointsList.size(); i++) {
	        GeoPoint foo = TabLayoutActivity.getInstance().pointsList.get(i);
	        GeoPoint point = new GeoPoint((int)(foo.getLatitudeE6()),
	                                      (int)(foo.getLongitudeE6()));

	        
	        CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(d3, map);
			CustomOverlayItem overlayItemX = new CustomOverlayItem(point, "Title#" +i, "Description"+i, 
					"http://maps.googleapis.com/maps/api/streetview?size=300x300&location=55.754074,12.466386&fov=90&sensor=false");
			itemizedOverlay.addOverlay(overlayItemX);
	  	  	
	  	  	
			overlayList.add(itemizedOverlay);
	        
	    }
		
	}
    
    
    public void onClick (View view){
    	switch (view.getId()){
    	case R.id.addMarkers:
    		//add
    		//TabLayoutActivity.getInstance().tabHost.setCurrentTab(2);
    		addMarkers();
    		finish();
    		break;
    	case R.id.clearMarkers:
    		//clear
    		
    		TabLayoutActivity.getInstance().overlayList.clear();
    		TabLayoutActivity.getInstance().initMap();

    		finish();
    		
    		break;
    	
    		case R.id.AddMarkerToggle:
    		
    			if (TabLayoutActivity.getInstance().logSaveTaps == false) 
    	    	{
    				TabLayoutActivity.getInstance().logSaveTaps=true;
    	    	}
    	    	else
    	    	{
    	    		TabLayoutActivity.getInstance().logSaveTaps=false;
    	    	}
    			
    			Toast.makeText(this,
        				"AddMarkers is "+tb.getText().toString(),
        				Toast.LENGTH_LONG).show(); 

    		break;		
    		
    		
    	}
    } 
    
}