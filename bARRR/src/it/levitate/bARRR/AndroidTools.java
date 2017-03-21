package it.levitate.bARRR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidTools extends Activity {

	//Hide or show keyboard and request and clear focus from editText1.
			public static void keyFocus (boolean is, EditText editText, Context context){
				// Old version: //public void hideKeyFocus (View view){}
				EditText e = editText;
				
				
				if(is==true){
					e.requestFocus();
					InputMethodManager imm = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.showSoftInput(e, 0);
					
				}
				else{
					e.clearFocus();
					e.setText("");
					InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
					
					// see http://androidsnippets.com/how-to-remove-focus-and-hide-the-on-screen-keyboard
				}
			}
	
			
			//Check if phone is online...
			public static boolean isOnline() {
				try {
				InetAddress.getByName("google.com").isReachable(3);
				return true;
				} catch (UnknownHostException e){
				return false;
				} catch (IOException e){
				return false;
				}
			}
			
			/* Gets location coordinates and sends as a SMS
			 * Google Maps URL parameters: 
			 * http://mapki.com/wiki/Google_Map_Parameters
			 */
				
				
			private void sendSMS(String phoneNumber, String message){
				/*
				String message = 
			            "http://maps.google.com/maps?q=" + lat1 + "," + longi1 + "&iwloc=A";
				String phoneNumber = "31166060";
				*/
			   SmsManager sms = SmsManager.getDefault();
			   Log.d("SENDING_SMS", "Attempting to send an SMS to: " + phoneNumber);
			   try {
			       sms.sendTextMessage(phoneNumber, null, message, null, null);
			   } catch (Exception e) {
			       Log.e("ERROR_SMS", "Error sending an SMS to: " + phoneNumber + " :: " + e);
			   }       

			} 
			
			
			//INPUT: x milliseconds. //Vibrate for x milliseconds.
			public static void vibrateFor(int ms, Context context){
				Context c = context;
				Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(ms);	
			}


			
			
			public static class NullRemove {

			    public static String[] removeNull(String[] a) {
			        ArrayList<String> aux = new ArrayList<String>();
			        for (String elem : a) {
			            if (elem != null) {
			                aux.add(elem);
			            }
			        }
			        return (String[]) aux.toArray(new String[aux.size()]);
			    }
			   
			}	

			
			//INPUT: Two set of coordinates, lat1,longi1 AND lat2, longi2. //OUTPUT: Distance
			public static String distanceTo (double lat1, double longi1, double lat2, double longi2, boolean showFormat){
				
				double distance;
				String Distance;
				String format;
				boolean sFormat = showFormat;
				StringBuilder sb;
				
				Location locationA = new Location("point A");

				locationA.setLatitude(lat1);
				locationA.setLongitude(longi1);

				Location locationB = new Location("point B");

				locationB.setLatitude(lat2);
				locationB.setLongitude(longi2);

				NumberFormat nf = NumberFormat.getInstance();
				
				distance = (locationA.distanceTo(locationB));
				if (distance > 1000){
				format = "KM";
				distance = distance/1000;
				nf.setMaximumFractionDigits(1);
				Distance = nf.format(distance);
				//Toast.makeText(TabLayoutActivity.this, "Distance: "+Distance+" KM", Toast.LENGTH_SHORT).show();
				} else{
				format = "M";
				nf.setMaximumFractionDigits(0);
				Distance = nf.format(distance);
				//Toast.makeText(TabLayoutActivity.this, "Distance: "+Distance+" Meter", Toast.LENGTH_SHORT).show();
				}
				
				if(sFormat==true){
				sb = new StringBuilder(Distance);
				if(format.equals("KM"))
				sb.append(" KM");
				Distance = sb.toString();
				if(format.equals("M")){
				sb.append(" M");}	
				Distance = sb.toString();
				}
				
				return Distance;
			}
			
			

			//INPUT: Two set of coordinates. //OUTPUT: Directions in Google Maps
			public static void directionsTo (double lat1, double longi1, double lat2, double longi2, Context context){
				Context c = context;
				if (lat1!=0 && longi1!=0 && lat2!=0 && longi2!=0){
					
					final Intent intent = new Intent(Intent.ACTION_VIEW,
					          /** Using the web based turn by turn directions url. */
					          Uri.parse(
					                   "http://maps.google.com/maps?" + "saddr=" + lat1 + 
					                   "," + longi1 + "&daddr=" + lat2 + "," + longi2));
					                /** Setting the Class Name that should handle 
					                 *  this intent.  We are setting the class name to 
					                 *  the class name of the native maps activity.
					                 *  Android platform recognizes this and now knows that
					                 *  we want to open up the Native Maps application to
					                 *  handle the URL.  Hence it does not give the choice of
					                 *  application to the user and directly opens the 
					                 *  Native Google Maps application.
					                 */
					                intent.setClassName(
					                 "com.google.android.apps.maps",
					                 "com.google.android.maps.MapsActivity");
					          c.startActivity(intent);
				}
				else{
					Toast.makeText(c.getApplicationContext(),
							  "One of your coordinates is Invalid!",
							  Toast.LENGTH_LONG).show();
				}
					
				}
			
			
		    /**
		     * Instructs the map view to navigate to the point and zoom level specified.
		     * @param geoPoint
		     * @param zoomLevel
		     */
		    private void setMapZoomPoint(GeoPoint geoPoint, int zoomLevel, MapView map) {
				map.getController().setCenter(geoPoint);
				map.getController().setZoom(zoomLevel);
				map.postInvalidate();
			}
			

			//===================================\\=========================================
			//########## OTHER METHODS ###########\\--------------------------------------
			//=====================================\\======================================
			/*	
				private void setupForGPSAutoRefreshing() {
					/* Register with out LocationManager to send us 
					 * an intent (whos Action-String we defined above)
					 * when  an intent to the location manager,
					 * that we want to get informed on changes to our own position.
					 * This is one of the hottest features in Android.
					 */
			/*
					final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 25; // in Meters
					final long MINIMUM_TIME_BETWEEN_UPDATE = 5000; // in Milliseconds
					// Get the first provider available
					List<LocationProvider> providers = this.myLocationManager.getProviders();
					LocationProvider provider = providers.get(0);
					
					this.myLocationManager.requestUpdates(provider, MINIMUM_TIME_BETWEEN_UPDATE,
							MINIMUM_DISTANCECHANGE_FOR_UPDATE, new Intent(MY_LOCATION_CHANGED_ACTION));
					
					// Create an IntentReceiver, that will react on the
					// Intents we made our LocationManager to send to us. 
					// 
					this.myIntentReceiver = new MyIntentReceiver();
				
					// 
					// In onResume() the following method will be called automatically!
					// registerReceiver(this.myIntentReceiver, this.myIntentFilter); 
					//
				}
			
			*/
			
			public static boolean isEqual(Object o1, Object o2) {
			    return o1 == o2 || (o1 != null && o1.equals(o2));
			}
			
}
