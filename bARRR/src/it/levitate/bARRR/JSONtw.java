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

public class JSONtw extends Activity {
	
	HttpClient client;
	JSONObject json;
	TextView httpStuff;
	
	final static String URL = "http://api.twitter.com/1/statuses/user_timeline.json?screen_name=";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsontw); 
        httpStuff = (TextView) findViewById(R.id.textUpdate);
        client = new DefaultHttpClient();
		new Read().execute("text");
    }
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();

    }
    
    public JSONObject lastTweet(String username) throws ClientProtocolException, IOException, JSONException
    {
    	StringBuilder url = new StringBuilder(URL);
    	url.append("cem2ran");
		
    	HttpGet get = new HttpGet(url.toString());
    	HttpResponse r = client.execute(get);
    	int status = r.getStatusLine().getStatusCode();
    	if (status == 200){
    		HttpEntity e = r.getEntity();
    		String data = EntityUtils.toString(e);
    		JSONArray timeline = new JSONArray(data);
    		JSONObject last = timeline.getJSONObject(0);
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
				json = lastTweet("cem2ran");
				return json.getString(params[0]); //text
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			httpStuff.setText(result);
		}
    	
    	
    }
}