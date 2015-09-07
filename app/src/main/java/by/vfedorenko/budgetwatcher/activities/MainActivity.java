package by.vfedorenko.budgetwatcher.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.fragments.MainFragment;
import by.vfedorenko.budgetwatcher.utils.SmsSyncronizer;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, new MainFragment())
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_sync_sms) {
			SmsSyncronizer.syncSms(this.getApplicationContext());
			return true;
		} else if (id == R.id.action_sync_network) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		switch (position) {
			case 0:
				// Insert the fragment by replacing any existing fragment
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new MainFragment())
						.commit();
				break;
			case 1:
				break;
			case 2:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
		}

//		// Create a new fragment and specify the planet to show based on position
//		Fragment fragment = new PlanetFragment();
//		Bundle args = new Bundle();
//		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//		fragment.setArguments(args);
//
//		// Insert the fragment by replacing any existing fragment
//		FragmentManager fragmentManager = getFragmentManager();
//		fragmentManager.beginTransaction()
//				.replace(R.id.content_frame, fragment)
//				.commit();

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		//setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}
}
