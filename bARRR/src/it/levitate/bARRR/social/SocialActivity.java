package it.levitate.bARRR.social;


import it.levitate.bARRR.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.FriendsList;
import com.facebook.android.LoginButton;
import com.facebook.android.Places;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.facebook.android.UpdateStatusResultDialog;
import com.facebook.android.UploadPhotoResultDialog;
import com.facebook.android.Util;
import com.facebook.android.Utility;


 
public class SocialActivity extends Activity implements OnItemClickListener
{
	   
	//Make methods from MapAppActivity available from other places:
	private static SocialActivity theInstance;
	
	public static SocialActivity getInstance() {
        return SocialActivity.theInstance;
    }
	public SocialActivity() {
		SocialActivity.theInstance = this;
    }
	
	
	/*
	     * Your Facebook Application ID must be set before running this example See
	     * http://www.facebook.com/developers/createapp.php
	     */
	    public static final String APP_ID = "114364875341347";

	    
	    
	    ArrayList<String> listAttending, listFriends, listOnline;
	    
	    
	    private LoginButton mLoginButton;
	    private TextView mText;
	    private ImageView mUserPic;
	    private Handler mHandler;
	    ProgressDialog dialog;

	    final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	    final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;

	    private String graph_or_fql;

	    private ListView list;
	    String[] main_items = { "Update Status", "App Requests", "Get Friends", "Upload Photo",
	            "Place Check-in", "Run FQL Query", "Online Friends" };
	    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
	            "photo_upload", "user_events" };

	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        if (APP_ID == null) {
	            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
	                    + "specified before running this example: see FbAPIs.java");
	            return;
	        }

	        setContentView(R.layout.fbmain);
	        mHandler = new Handler();

	        mText = (TextView) SocialActivity.this.findViewById(R.id.txt);
	        mUserPic = (ImageView) SocialActivity.this.findViewById(R.id.user_pic);

	        // Create the Facebook Object using the app id.
	        Utility.mFacebook = new Facebook(APP_ID);
	        // Instantiate the asynrunner object for asynchronous api calls.
	        Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

	        mLoginButton = (LoginButton) findViewById(R.id.login);

	        // restore session if one exists
	        SessionStore.restore(Utility.mFacebook, this);
	        SessionEvents.addAuthListener(new FbAPIsAuthListener());
	        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

	        /*
	         * Source Tag: login_tag
	         */
	        mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook, permissions);

	        if (Utility.mFacebook.isSessionValid()) {
	            requestUserData();
	        }

	        list = (ListView) findViewById(R.id.main_list);

	        list.setOnItemClickListener(this);
	        list.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list_item, main_items));
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	        if (Utility.mFacebook != null && !Utility.mFacebook.isSessionValid()) {
	            mText.setText("You are logged out! ");
	            mUserPic.setImageBitmap(null);
	        }
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch (requestCode) {
	        /*
	         * if this is the activity result from authorization flow, do a call
	         * back to authorizeCallback Source Tag: login_tag
	         */
	            case AUTHORIZE_ACTIVITY_RESULT_CODE: {
	                Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
	                break;
	            }
	            /*
	             * if this is the result for a photo picker from the gallery, upload
	             * the image after scaling it. You can use the Utility.scaleImage()
	             * function for scaling
	             */
	            case PICK_EXISTING_PHOTO_RESULT_CODE: {
	                if (resultCode == Activity.RESULT_OK) {
	                    Uri photoUri = data.getData();
	                    if (photoUri != null) {
	                        Bundle params = new Bundle();
	                        try {
	                            params.putByteArray("photo",
	                                    Utility.scaleImage(getApplicationContext(), photoUri));
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                        params.putString("caption", "FbAPIs Sample App photo upload");
	                        Utility.mAsyncRunner.request("me/photos", params, "POST",
	                                new PhotoUploadListener(), null);
	                    } else {
	                        Toast.makeText(getApplicationContext(),
	                                "Error selecting image from the gallery.", Toast.LENGTH_SHORT)
	                                .show();
	                    }
	                } else {
	                    Toast.makeText(getApplicationContext(), "No image selected for upload.",
	                            Toast.LENGTH_SHORT).show();
	                }
	                break;
	            }
	        }
	    }

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
	        switch (position) {
	        /*
	         * Source Tag: update_status_tag Update user's status by invoking the
	         * feed dialog To post to a friend's wall, provide his uid in the 'to'
	         * parameter Refer to
	         * https://developers.facebook.com/docs/reference/dialogs/feed/ for more
	         * info.
	         */
	            case 0: {
	                Bundle params = new Bundle();
	                params.putString("caption", getString(R.string.app_name));
	                params.putString("description", getString(R.string.app_desc));
	                params.putString("picture", Utility.HACK_ICON_URL);
	                params.putString("name", getString(R.string.app_action));

	                Utility.mFacebook.dialog(SocialActivity.this, "feed", params, new UpdateStatusListener());
	                String access_token = Utility.mFacebook.getAccessToken();
	                System.out.println(access_token);
	                break;
	            }

	            /*
	             * Source Tag: app_requests Send an app request to friends. If no
	             * friend is specified, the user will see a friend selector and will
	             * be able to select a maximum of 50 recipients. To send request to
	             * specific friend, provide the uid in the 'to' parameter Refer to
	             * https://developers.facebook.com/docs/reference/dialogs/requests/
	             * for more info.
	             */
	            case 1: {
	                Bundle params = new Bundle();
	                params.putString("message", getString(R.string.request_message));
	                Utility.mFacebook.dialog(SocialActivity.this, "apprequests", params,
	                        new AppRequestsListener());
	                break;
	            }

	            /*
	             * Source Tag: friends_tag You can get friends using
	             * graph.facebook.com/me/friends, this returns the list sorted by
	             * UID OR using the friend table. With this you can sort the way you
	             * want it. 
	             * Friend table - https://developers.facebook.com/docs/reference/fql/friend/
	             * User table - https://developers.facebook.com/docs/reference/fql/user/
	             */
	            case 2: {
	                if (!Utility.mFacebook.isSessionValid()) {
	                    Util.showAlert(this, "Warning", "You must first log in.");
	                } else {
	                    dialog = ProgressDialog.show(SocialActivity.this, "",
	                            getString(R.string.please_wait), true, true);
	                    new AlertDialog.Builder(this)
	                            .setTitle(R.string.Graph_FQL_title)
	                            .setMessage(R.string.Graph_FQL_msg)
	                            .setPositiveButton(R.string.graph_button,
	                                    new DialogInterface.OnClickListener() {
	                                        @Override
	                                        public void onClick(DialogInterface dialog, int which) {
	                                            graph_or_fql = "graph";
	                                            Bundle params = new Bundle();
	                                            params.putString("fields", "name, picture, location");
	                                            Utility.mAsyncRunner.request("me/friends", params,
	                                                    new FriendsRequestListener());
	                                        }

	                                    })
	                            .setNegativeButton(R.string.fql_button,
	                                    new DialogInterface.OnClickListener() {
	                                        @Override
	                                        public void onClick(DialogInterface dialog, int which) {
	                                            graph_or_fql = "fql";
	                                            String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
	                                            Bundle params = new Bundle();
	                                            params.putString("method", "fql.query");
	                                            params.putString("query", query);
	                                            Utility.mAsyncRunner.request(null, params,
	                                                    new FriendsRequestListener());
	                                        }

	                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
	                                @Override
	                                public void onCancel(DialogInterface d) {
	                                    dialog.dismiss();
	                                }
	                            }).show();
	                }
	                break;
	            }

	            /*
	             * Source Tag: upload_photo You can upload a photo from the media
	             * gallery or from a remote server How to upload photo:
	             * https://developers.facebook.com/blog/post/498/
	             */
	            case 3: {
	                if (!Utility.mFacebook.isSessionValid()) {
	                    Util.showAlert(this, "Warning", "You must first log in.");
	                } else {
	                    dialog = ProgressDialog.show(SocialActivity.this, "",
	                            getString(R.string.please_wait), true, true);
	                    new AlertDialog.Builder(this)
	                            .setTitle(R.string.gallery_remote_title)
	                            .setMessage(R.string.gallery_remote_msg)
	                            .setPositiveButton(R.string.gallery_button,
	                                    new DialogInterface.OnClickListener() {
	                                        @Override
	                                        public void onClick(DialogInterface dialog, int which) {
	                                            Intent intent = new Intent(Intent.ACTION_PICK,
	                                                    (MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
	                                            startActivityForResult(intent,
	                                                    PICK_EXISTING_PHOTO_RESULT_CODE);
	                                        }

	                                    })
	                            .setNegativeButton(R.string.remote_button,
	                                    new DialogInterface.OnClickListener() {
	                                        @Override
	                                        public void onClick(DialogInterface dialog, int which) {
	                                            /*
	                                             * Source tag: upload_photo_tag
	                                             */
	                                            Bundle params = new Bundle();
	                                            params.putString("url",
	                                                    "http://www.facebook.com/images/devsite/iphone_connect_btn.jpg");
	                                            params.putString("caption",
	                                                    "FbAPIs Sample App photo upload");
	                                            Utility.mAsyncRunner.request("me/photos", params,
	                                                    "POST", new PhotoUploadListener(), null);
	                                        }

	                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
	                                @Override
	                                public void onCancel(DialogInterface d) {
	                                    dialog.dismiss();
	                                }
	                            }).show();
	                }
	                break;
	            }

	            /*
	             * User can check-in to a place, you require publish_checkins
	             * permission for that. You can use the default Times Square
	             * location or fetch user's current location. Get user's checkins:
	             * https://developers.facebook.com/docs/reference/api/checkin/
	             */
	            case 4: {
	                final Intent myIntent = new Intent(getApplicationContext(), Places.class);

	                new AlertDialog.Builder(this)
	                        .setTitle(R.string.get_location)
	                        .setMessage(R.string.get_default_or_new_location)
	                        .setPositiveButton(R.string.current_location_button,
	                                new DialogInterface.OnClickListener() {
	                                    @Override
	                                    public void onClick(DialogInterface dialog, int which) {
	                                        myIntent.putExtra("LOCATION", "current");
	                                        startActivity(myIntent);
	                                    }
	                                })
	                        .setNegativeButton(R.string.times_square_button,
	                                new DialogInterface.OnClickListener() {
	                                    @Override
	                                    public void onClick(DialogInterface dialog, int which) {
	                                        myIntent.putExtra("LOCATION", "times_square");
	                                        startActivity(myIntent);
	                                    }

	                                }).show();
	                break;
	            }

	            case 5: {
	                if (!Utility.mFacebook.isSessionValid()) {
	                    Util.showAlert(this, "Warning", "You must first log in.");
	                } else {
	                    //new FQLQuery(SocialActivity.this).show();
	                	dialog = ProgressDialog.show(SocialActivity.this, "",
	                            getString(R.string.please_wait), true, true);
	                    		String EVENT_ID = "279247205459244";
	                            
	                            Bundle params = new Bundle();
	                            params.putString("fields", "id");
	                            //Utility.mAsyncRunner.request(EVENT_ID+"/attending", params, new AttendingFriendsRequestListener());
	                            
	                            
	                            Utility.mAsyncRunner.request("me/friends", params, new OnlineFriendsRequestListener());
	                          //Compare the two arrays 
	                            
	                           /*
	                            int length = listAttending.length();
	            	            String lengthAsString = Integer.toString(length);
	            	            showToast(lengthAsString);
	                            */
	                            /*
	                            	listOnline = new ArrayList<String>();
	                            	for(int i = 0; i < listAttending.size(); i++){
	                            	
	                            		for(int j = 0; j < listFriends.size(); j++){
	                            	
	                            			if (listAttending.get(i).equals(listFriends.get(j))){
	                            				listOnline.add(listAttending.get(i));
	                            			}
	                            		}
	                            	
	                            	}
	                            */
	                            
	                            //showToast(listAttending.toString());  
	                            showToast(listFriends.toString());  
	                }
	                break;
	            }
	          
	            /*
	             * Source Tag: friends_tag You can get friends using
	             * graph.facebook.com/me/friends, this returns the list sorted by
	             * UID OR using the friend table. With this you can sort the way you
	             * want it. 
	             * Friend table - https://developers.facebook.com/docs/reference/fql/friend/
	             * User table - https://developers.facebook.com/docs/reference/fql/user/
	             */
	            case 6: {
	                if (!Utility.mFacebook.isSessionValid()) {
	                    Util.showAlert(this, "Warning", "You must first log in.");
	                } else {
	                   dialog = ProgressDialog.show(SocialActivity.this, "",
	                            getString(R.string.please_wait), true, true);
	                    		String EVENT_ID = "279247205459244";
	                            
	                            Bundle params = new Bundle();
	                            params.putString("fields", "name, picture, location");
	                            Utility.mAsyncRunner.request(EVENT_ID+"/attending", params,
	                            new OnlineFriendsRequestListener());
	                            }
	                break;
	            }
	        }
	    }

	    /*
	     * callback for the feed dialog which updates the profile status
	     */
	    public class UpdateStatusListener extends BaseDialogListener {
	        @Override
	        public void onComplete(Bundle values) {
	            final String postId = values.getString("post_id");
	            if (postId != null) {
	                new UpdateStatusResultDialog(SocialActivity.this, "Update Status executed", values)
	                        .show();
	            } else {
	                Toast toast = Toast.makeText(getApplicationContext(), "No wall post made",
	                        Toast.LENGTH_SHORT);
	                toast.show();
	            }
	        }

	        @Override
	        public void onFacebookError(FacebookError error) {
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }

	        @Override
	        public void onCancel() {
	            Toast toast = Toast.makeText(getApplicationContext(), "Update status cancelled",
	                    Toast.LENGTH_SHORT);
	            toast.show();
	        }
	    }

	    /*
	     * callback for the apprequests dialog which sends an app request to user's
	     * friends.
	     */
	    public class AppRequestsListener extends BaseDialogListener {
	        @Override
	        public void onComplete(Bundle values) {
	            Toast toast = Toast.makeText(getApplicationContext(), "App request sent",
	                    Toast.LENGTH_SHORT);
	            toast.show();
	        }

	        @Override
	        public void onFacebookError(FacebookError error) {
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }

	        @Override
	        public void onCancel() {
	            Toast toast = Toast.makeText(getApplicationContext(), "App request cancelled",
	                    Toast.LENGTH_SHORT);
	            toast.show();
	        }
	    }
	    
	    public class AttendingFriendsRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            dialog.dismiss();
	            
	            JSONArray jsonArray;
	            
	            try {
					jsonArray = new JSONObject(response).getJSONArray("data");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showToast("Error: " + e.getMessage());
		            return;
				}
	            //int length = jsonArray.length();
	            //String lengthAsString = Integer.toString(length);
	            //showToast(lengthAsString);
	            long friendId;
	            listAttending = new ArrayList<String>();
	            for (int i=0; i<jsonArray.length(); i++) {
	                try {
	                	friendId = jsonArray.getJSONObject(i).getLong("id");
	                	String friendId2 = Long.toString(friendId);
						listAttending.add(friendId2);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            showToast(listAttending.toString());
	        }

	        public void onFacebookError(FacebookError error) {
	            dialog.dismiss();
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	    
	    /*
	     * callback after friends are fetched via me/friends or fql query.
	     */
	    public class OnlineFriendsRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            dialog.dismiss();
	            
	            JSONArray jsonArray;
	            
	            try {
					jsonArray = new JSONObject(response).getJSONArray("data");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showToast("Error: " + e.getMessage());
		            return;
				}
	            
	            //int length = jsonArray.length();
	            //String lengthAsString = Integer.toString(length);
	            //showToast(lengthAsString);
	            long friendId;
	            listFriends = new ArrayList<String>();
	            for (int i=0; i<jsonArray.length(); i++) {
	                try {
	                	friendId = jsonArray.getJSONObject(i).getLong("id");
	                	String friendId2 = Long.toString(friendId);
						listFriends.add( friendId2 );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            //showToast(listFriends.toString());
	        }

	        public void onFacebookError(FacebookError error) {
	            dialog.dismiss();
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	    
	    /*
	     * callback after friends are fetched via me/friends or fql query.
	     */
	    public class FriendsRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            dialog.dismiss();
	            Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
	            myIntent.putExtra("API_RESPONSE", response);
	            myIntent.putExtra("METHOD", graph_or_fql);
	            startActivity(myIntent);
	        }

	        public void onFacebookError(FacebookError error) {
	            dialog.dismiss();
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }
	    }

	    /*
	     * callback for the photo upload
	     */
	    public class PhotoUploadListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            dialog.dismiss();
	            mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                    new UploadPhotoResultDialog(SocialActivity.this, "Upload Photo executed", response)
	                            .show();
	                }
	            });
	        }

	        public void onFacebookError(FacebookError error) {
	            dialog.dismiss();
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_LONG).show();
	        }
	    }

	    public class FQLRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                    Toast.makeText(getApplicationContext(), "Response: " + response,
	                            Toast.LENGTH_LONG).show();
	                }
	            });
	        }

	        public void onFacebookError(FacebookError error) {
	            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
	                    Toast.LENGTH_LONG).show();
	        }
	    }

	    /*
	     * Callback for fetching current user's name, picture, uid.
	     */
	    public class UserRequestListener extends BaseRequestListener {

	        @Override
	        public void onComplete(final String response, final Object state) {
	            JSONObject jsonObject;
	            try {
	                jsonObject = new JSONObject(response);

	                final String picURL = jsonObject.getString("picture");
	                final String name = jsonObject.getString("name");
	                Utility.userUID = jsonObject.getString("id");

	                mHandler.post(new Runnable() {
	                    @Override
	                    public void run() {
	                        mText.setText("Welcome " + name + "!");
	                        mUserPic.setImageBitmap(Utility.getBitmap(picURL));
	                    }
	                });

	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }

	    }

	    /*
	     * The Callback for notifying the application when authorization succeeds or
	     * fails.
	     */

	    public class FbAPIsAuthListener implements AuthListener {

	        @Override
	        public void onAuthSucceed() {
	            requestUserData();
	        }

	        @Override
	        public void onAuthFail(String error) {
	            mText.setText("Login Failed: " + error);
	        }
	    }

	    /*
	     * The Callback for notifying the application when log out starts and
	     * finishes.
	     */
	    public class FbAPIsLogoutListener implements LogoutListener {
	        @Override
	        public void onLogoutBegin() {
	            mText.setText("Logging out...");
	        }

	        @Override
	        public void onLogoutFinish() {
	            mText.setText("You have logged out! ");
	            mUserPic.setImageBitmap(null);
	        }
	    }

	    /*
	     * Request user name, and picture to show on the main screen.
	     */
	    public void requestUserData() {
	        mText.setText("Fetching user name, profile pic...");
	        Bundle params = new Bundle();
	        params.putString("fields", "name, picture");
	        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	    }

	    /**
	     * Definition of the list adapter
	     */
	    public class MainListAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;

	        public MainListAdapter() {
	            mInflater = LayoutInflater.from(SocialActivity.this.getBaseContext());
	        }

	        @Override
	        public int getCount() {
	            return main_items.length;
	        }

	        @Override
	        public Object getItem(int position) {
	            return null;
	        }

	        @Override
	        public long getItemId(int position) {
	            return 0;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	            View hView = convertView;
	            if (convertView == null) {
	                hView = mInflater.inflate(R.layout.main_list_item, null);
	                ViewHolder holder = new ViewHolder();
	                holder.main_list_item = (TextView) hView.findViewById(R.id.main_api_item);
	                hView.setTag(holder);
	            }

	            ViewHolder holder = (ViewHolder) hView.getTag();

	            holder.main_list_item.setText(main_items[position]);

	            return hView;
	        }

	    }
	    
	    public void showToast(final String msg) {
	        mHandler.post(new Runnable() {
	            @Override
	            public void run() {
	                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
	                toast.show();
	            }
	        });
	    }

	    class ViewHolder {
	        TextView main_list_item;
	    }

	}


