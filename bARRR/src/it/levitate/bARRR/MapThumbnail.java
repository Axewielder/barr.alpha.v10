

package it.levitate.bARRR;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

 
public class MapThumbnail extends Activity {
	
	String iconUrl = "http://stint.dk/ic_beer.png"; //URL of the icon used. Expect that the search 
	private static final String LOG_TAG = "Could not find map thumbnail";
	String objectId;
	 String barName, address, city, country, email, openingHours, prices, description, webSite, type, geoPoint, phone;
	    int addressNumber, postalCode, phoneNumber, admin;
	    TextView textBarName, textview, textDescription, textOpen, textPrices, textContact, textDistance, textPhone, textEmail, textWeb;
	    double latitudeE6, longitudeE6;
	    
	    int cityId = R.id.mapThumbnail; 	//the id of the object
		//engine needs some time to discover it before going insane!
		int sMapHeight=450;					//the map Height
		int sMapWith= 450;					//the map With
		int zoomMap = 18;					//what the zoom is for the map
		
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thumbnail);
		// Add your initialization code here
		Parse.initialize(this, "TNkwkJHARFHKRhtI63KioBB6NJrf7LImBYz3gkoN", "GXiyK1juAZ7qVfztAUJq1mxOWW0iDLmtNi9T4Wbx"); 
		setContentView(R.layout.thumbnail);
		
		//Get Bundle
		Bundle getTitle = this.getIntent().getExtras();
	  	barName = getTitle.getString("Title");
		
	  	textBarName = ((TextView) findViewById(R.id.viewTitle));
		textview = ((TextView) findViewById(R.id.view1));
		//textContact = ((TextView) findViewById(R.id.view2));
		textDescription = ((TextView) findViewById(R.id.view3));
		textOpen = ((TextView) findViewById(R.id.view4));
		textPrices = ((TextView) findViewById(R.id.view5));
		textDistance = ((TextView) findViewById(R.id.distanceView));
		textPhone = ((TextView) findViewById(R.id.textPhone));
		textEmail = ((TextView) findViewById(R.id.textEmail));
		textWeb = ((TextView) findViewById(R.id.textWeb));
		
		
		//This works!!
		ParseQuery query = new ParseQuery("BarObject");
		query.whereEqualTo("BarName", barName);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK );
		query.findInBackground(new FindCallback() {
		public void done(List<ParseObject> barList, ParseException e) {
	        if (e == null) {
	            ParseObject barInformation = barList.get(0);
	    		
	    		objectId = barInformation.getObjectId();
	    		insertData();
	    		
	        } else {
	            Log.d("score", "Error: " + e.getMessage());
	        }
	    }
	});
		//End of This Works!!
		
				
		} //end of onCreate


	public Drawable getDrawableFromWebOperation(String url){							//getDrawableFromWebOperation method
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, url);
			return d;
		} // end of try
		catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			return null;
		} //end of catch

} // end of Drawable
	
	@Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	
    	super.onResume();

    }
    
    public void insertData(){
    	try {
			ParseObject barInformation = new ParseObject("BarObject");
			ParseQuery query2 = new ParseQuery("BarObject");
			query2.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		    barInformation = query2.get(objectId);
		 
						address = barInformation.getString("Address");
						addressNumber = barInformation.getInt("AddressNumber");
						city = barInformation.getString("City");
						postalCode = barInformation.getInt("PostalCode");
						country = barInformation.getString("Country");
						email = barInformation.getString("Email");
						phoneNumber = barInformation.getInt("PhoneNumber");
						phone = Integer.toString(phoneNumber);
						openingHours = barInformation.getString("OpeningHours");
						prices = barInformation.getString("Prices");
						description = barInformation.getString("Description");
						webSite = barInformation.getString("WebSite");
						admin = barInformation.getInt("Admin");
						type = barInformation.getString("Type");
						geoPoint = barInformation.getString("GeoPoint");
						
						String[] separated = geoPoint.split(",");
			    		// I suppose, this will contain latitude
			    		latitudeE6 = Double.parseDouble(separated[0]); 
			    		// I suppose, this will contain longitude
			    		longitudeE6 = Double.parseDouble(separated[1]);
						
							textBarName.setText(barName);
							textview.setText(address+" "+Integer.toString(addressNumber)+"\n"+city+" \n"+postalCode);
							textDescription.setText(description);
							textOpen.setText("Opening Hours:\n"+openingHours);
							textPrices.setText("Prices:\n"+prices);
							//textContact.setText("Contact Info:\n"+phoneNumber+"\n"+email+"\n"+webSite);
							textDistance.setText(AndroidTools.distanceTo(TabLayoutActivity.lat1, TabLayoutActivity.longi1, latitudeE6, longitudeE6, true));
							textPhone.setText(phone);
							textEmail.setText(email);
							textWeb.setText(webSite);
							
						((ImageView) findViewById(cityId)).setImageDrawable(getDrawableFromWebOperation
						("http://maps.googleapis.com/maps/api/staticmap?center="+geoPoint+"&zoom="+zoomMap+"&size="+sMapWith+"x"+sMapHeight+"&markers=icon:"
						+iconUrl+"%7C"+geoPoint+"&sensor=false"));
						
						
						
						
		} catch (ParseException e) {
		    // e.getMessage() will have information on the error.
		}	
    }

    
	public void onClick (View view){ 		
	    		
		//String destination = address+" "+addressNumber+","+postalCode+" "+city;
		
    	switch (view.getId()){
    	case R.id.NavigationButton:
    		AndroidTools.directionsTo	(
    				TabLayoutActivity.lat1, 
    				TabLayoutActivity.longi1, 
    				latitudeE6,
    				longitudeE6, TabLayoutActivity.getInstance());
    				finish();
    		break;
    	case R.id.mapThumbnail:
    		String uri = "google.streetview:cbll="+ latitudeE6+","+longitudeE6+"&cbp=1,99.56,,1,-5.27&mz=21";
    		Intent streetView = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uri));
    		startActivity(streetView);
    		finish();
    		break;	
    		
    	case R.id.textPhone:
    		String uri2 = "tel:"+phoneNumber;
    		Intent callPhone = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uri2));
    		startActivity(callPhone);
    		finish();
    		break;	
    	case R.id.textEmail:
    		/* Create the Intent */
        	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        	/* Fill it with Data */
        	emailIntent.setType("plain/text");
        	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

        	/* Send it off to the Activity-Chooser */
        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        	
    		finish();
    		break;			
    		case R.id.textWeb:
    		String uri3 = "http://"+webSite;
    		Intent openWebsite = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uri3));
    		startActivity(openWebsite);
    		finish();
    		break;		
    	}
    	
    	
    	
	}
    
}