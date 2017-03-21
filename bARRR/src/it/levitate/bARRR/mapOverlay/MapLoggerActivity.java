package it.levitate.bARRR.mapOverlay;



import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import it.levitate.bARRR.R;
import it.levitate.bARRR.TabLayoutActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


 
public class MapLoggerActivity extends Activity {
	public static String tappedLocations;
	private Handler mHandler;
	private TextView t;
	private EditText e;
	ToggleButton tb;
	//Make methods from TabLayoutActivity available from other places:
		private static MapLoggerActivity theInstance;
		
		public static MapLoggerActivity getInstance() {
	        return MapLoggerActivity.theInstance;
	    }
		public MapLoggerActivity() {
			MapLoggerActivity.theInstance = this;
	    }
		
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplogger);
		t = (TextView) findViewById(R.id.locationsView);
		mHandler = new Handler();
        mHandler.post(mUpdate);
        tb = (ToggleButton) findViewById(R.id.MapLoggerToggle);
        
        if (TabLayoutActivity.getInstance().logTaps == true) 
    	{
    	  tb.setChecked(true);
    	}
    	else
    	{
    	  tb.setChecked(false);
    	}
        
	}
	@Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    private Runnable mUpdate = new Runnable() {
    	   public void run() {

    		   t.setText(tappedLocations);
    	       //mHandler.post(mUpdate);
    	       mHandler.postDelayed(this, 100);

    	    }
    	};
    
    public void onClick (View view){
    	switch (view.getId()){
    	case R.id.add:
    		//add
    		e = (EditText) findViewById(R.id.addText);
    		tappedLocations += e.getText().toString();
    		t.setText(tappedLocations);
    		break;
    	case R.id.clear:
    		//clear
    		tappedLocations = "";
    		TabLayoutActivity.getInstance().tappedLocations  = "";
    		t.setText("");
    		
    		break;
    	case R.id.delete:
    		//delete last entered Char
    		int strlen = tappedLocations.length();
    		String tL2 = tappedLocations.substring(0, (strlen-1));
    		tappedLocations = "";
    		TabLayoutActivity.getInstance().tappedLocations = "";
    		tappedLocations = tL2;
    		t.setText(tappedLocations);
    		
    		break;
    	case R.id.MapLoggerToggle:
    		
    		if (TabLayoutActivity.getInstance().logTaps == false) 
        	{
    			TabLayoutActivity.getInstance().logTaps=true;
        	}
        	else
        	{
        		TabLayoutActivity.getInstance().logTaps=false;
        	}
    		
    		
    		Toast.makeText(this,
    				"MapLogging is "+tb.getText().toString(),
    				Toast.LENGTH_LONG).show(); 
    		
    		break;	
    		
    		
    	}
    }
    
    
    /*
    public void populateArray(){
    	AddMarkersActivity.getInstance().pointsList.add(new GeoPoint((int)(32.864*1E6), (int)(-117.2353*1E6)));
    	
    	
    	
    	String[] firstArray = {tappedLocations};
    	ArrayList<String> list = new ArrayList<String>();
    	for (String s : firstArray)
    }
    */
    
}