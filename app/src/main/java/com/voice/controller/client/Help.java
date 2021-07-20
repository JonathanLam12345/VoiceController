package com.voice.controller.client;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Help extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.help_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.ip_address) {
			finish();
			Intent intent = new Intent(this, IPAddress.class);
			startActivity(intent);
		} else if (id == R.id.about) {
			finish();
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
		} else if (id == R.id.main) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		this.setTitle("Voice Controller");
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

}
