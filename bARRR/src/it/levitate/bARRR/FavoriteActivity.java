package it.levitate.bARRR;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
 
public class FavoriteActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView textview = new TextView(this);
		textview.setText("This is the Favorite tab");
		setContentView(textview);
		
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
}