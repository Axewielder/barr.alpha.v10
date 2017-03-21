package it.levitate.bARRR.listView;

import it.levitate.bARRR.R;
import it.levitate.bARRR.listView.DatabaseHelper;
import it.levitate.bARRR.listView.EmployeeDetails;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListViewActivity extends ListActivity {

	protected EditText searchText;
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        searchText = (EditText) findViewById(R.id.searchText);
    	db = (new DatabaseHelper(this)).getWritableDatabase();
    }
    
    public void onListItemClick(ListView parent, View view, int position, long id) {
    	Intent intent = new Intent(this, EmployeeDetails.class);
    	Cursor cursor = (Cursor) adapter.getItem(position);
    	intent.putExtra("EMPLOYEE_ID", cursor.getInt(cursor.getColumnIndex("_id")));
    	startActivity(intent);
    }
    
    public void search(View view) {
    	// || is the concatenation operation in SQLite
		cursor = db.rawQuery("SELECT _id, firstName, lastName, title FROM employee WHERE firstName || ' ' || lastName LIKE ?", 
						new String[]{"%" + searchText.getText().toString() + "%"});
		adapter = new SimpleCursorAdapter(
				this, 
				R.layout.employee_list_item, 
				cursor, 
				new String[] {"firstName", "lastName", "title"}, 
				new int[] {R.id.firstName, R.id.lastName, R.id.title});
		setListAdapter(adapter);
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Vibrator k = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    long milliseconds = 10;
	    k.vibrate(milliseconds);
    }
}