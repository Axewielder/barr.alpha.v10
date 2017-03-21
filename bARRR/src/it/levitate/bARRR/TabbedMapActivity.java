package it.levitate.bARRR;

import android.app.Activity; 
import android.app.LocalActivityManager; 
import android.os.Bundle; 
import com.google.android.maps.MapActivity; 
/** 
 * A screen that contains and runs multiple embedded activities. 
 * AND NOW... allows you to contain a MapView 
 * Edited by Miles 
 */ 
public class TabbedMapActivity extends MapActivity { 
    private static final String STATES_KEY = "android:states"; 
    /** 
     * This field should be made private, so it is hidden from the 
		SDK. 
     * {@hide} 
     */ 
    protected LocalActivityManager mLocalActivityManager; 
    public TabbedMapActivity() { 
        this(true); 
    } 
    public TabbedMapActivity(boolean singleActivityMode) { 
        mLocalActivityManager = new LocalActivityManager(this, 
singleActivityMode); 
    } 
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        Bundle states = savedInstanceState != null 
                ? (Bundle) savedInstanceState.getBundle(STATES_KEY) : 
null; 
        mLocalActivityManager.dispatchCreate(states); 
    } 
    @Override 
    protected void onResume() { 
        super.onResume(); 
        mLocalActivityManager.dispatchResume(); 
    } 
    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
        super.onSaveInstanceState(outState); 
        Bundle state = mLocalActivityManager.saveInstanceState(); 
        if (state != null) { 
            outState.putBundle(STATES_KEY, state); 
        } 
    } 
    @Override 
    protected void onPause() { 
        super.onPause(); 
        mLocalActivityManager.dispatchPause(isFinishing()); 
    } 
    @Override 
    protected void onStop() { 
        super.onStop(); 
        mLocalActivityManager.dispatchStop(); 
    } 
    @Override 
    protected void onDestroy() { 
        super.onDestroy(); 
        mLocalActivityManager.dispatchDestroy(isFinishing()); 
    } 
    public Activity getCurrentActivity() { 
        return mLocalActivityManager.getCurrentActivity(); 
    } 
        @Override 
        protected boolean isRouteDisplayed() { 
                return false; 
        } 
} 