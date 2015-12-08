package by.vfedorenko.budgetwatcher.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.fragments.OperationListFragment;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		//ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(false);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				selectItem(item.getItemId());
				return true;
			}
		});

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, new OperationListFragment())
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

		if (id == R.id.action_save) {
			startActivity(DriveActivity.buildIntent(this, DriveActivity.LAUNCH_TYPE_EXPORT));
			return true;
		} else if (id == R.id.action_load) {
			startActivity(DriveActivity.buildIntent(this, DriveActivity.LAUNCH_TYPE_IMPORT));
		}

		return super.onOptionsItemSelected(item);
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int id) {
		switch (id) {
			case R.id.drawer_home:
				// Insert the fragment by replacing any existing fragment
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new OperationListFragment())
						.commit();
				break;
			case R.id.drawer_statistic:
				break;
			case R.id.drawer_settings:
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
		//mNavigationView.setItemChecked(position, true);
		//setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mNavigationView);
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}
}
