package it.levitate.bARRR;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class JSONfb extends Activity {
	
	HttpClient client;
	JSONObject json;
	TextView userName, friendsList;
	
	final static String URL = "https://graph.facebook.com/";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsonfb); 
        userName = (TextView) findViewById(R.id.fbName);
        friendsList = (TextView) findViewById(R.id.friendsList);
        client = new DefaultHttpClient();
        String URL = "https://graph.facebook.com/1020821093";
        String id = "name";
        String URL2 = "https://graph.facebook.com/1020821093/friends?access_token=AAABoA6XEZCiMBAABzCaCntflOZCsg9DaXdiXduV8XO3lJVgYilZAAm8bQjtPhb0JAXZC8O1vhMPIMsX72ZB1rs9O1X7bsyC8ZD";
        String id2 = "data";
        //JsonReader jr = new JsonReader();
        try {
			
			String jsonResult = JsonReader.getSomethingWithJSON(URL, id);
			userName.setText(jsonResult);
			String jsonResult2 = JsonReader.getSomethingWithJSON(URL2, id2);
			friendsList.setText(jsonResult2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userName.setText("IOException");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userName.setText("JSONException");
		}
        
       /* 
        try {
			
			String jsonResult2 = JsonReader.getSomethingWithJSON(URL2, id2);
			friendsList.setText(jsonResult2);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			friendsList.setText("IOException");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			friendsList.setText("JSONException");
		}
        
        */
        
        
		//new Read().execute("");
    }
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();

    }
  /*  
    public JSONObject fbUSER(String username) throws IOException, JSONException
    {
    	StringBuilder url = new StringBuilder(URL);
    	url.append("1020821093");
		
    	StringBuilder friendsURL = new StringBuilder(URL);
    	url.append("1020821093"+"/");
    	friendsURL.append("friends");
    	friendsURL.append("?access_token=");
    	friendsURL.append("AAAAAAITEghMBAO36yTLQzhKbUpZBmv26LkYj2iFQTbDMNiVACsPef
    	OZC3qUYQ4S7HobDM1XUPPZAtDBiHUVGP5ONqslUB8y4dWzZC9g3dKjhHWuCLXjo");
    	 
        //?access_token=AAAAAAITEghMBAO36yTLQzhKbUpZBmv26LkYj2iFQTbDMNiVACsPefOZC3qUYQ4S7HobDM1XUPPZAtDBiHUVGP5ONqslUB8y4dWzZC9g3dKjhHWuCLXjo     
       	//grant_type=read_friendlists: 	
       	     
        
    	//Filter option
    	//url.append("?fields=id,name,picture");
    	//https://graph.facebook.com/cem2ran?fields=id,name,picture
    	//https://graph.facebook.com/?ids=http://www.imdb.com/title/tt0117500/
    	//url.append("?ids=http://www.imdb.com/title/tt0117500/");
    	
    	//read_friendlists
    	//?access_token=114364875341347|RFsG19WzL6LSWifatf9ekAsSG2Q 
    	HttpGet get = new HttpGet(url.toString());
    	HttpResponse r = client.execute(get);
    	int status = r.getStatusLine().getStatusCode();
    	if (status == 200){
    		HttpEntity e = r.getEntity();
    		String data = EntityUtils.toString(e);
    		JSONArray userData = new JSONArray(data);
    		JSONObject last = userData.getJSONObject(0);
    		return last;
    	}else {
    		Toast.makeText(this, "error", Toast.LENGTH_LONG);
    		return null;	
    	}
    	
    	
    	
    }
    
    public class Read extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				json = fbUSER("1020821093");
				return json.getString(params[0]); //text
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			userName.setText(result);
		}
    	
    	
    } */
}