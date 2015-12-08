package by.vfedorenko.budgetwatcher.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.fragments.OperationDetailFragment;
import by.vfedorenko.budgetwatcher.fragments.OperationListFragment;

/**
 * An activity representing a single Operation detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OperationListFragment}.
 */
public class OperationDetailActivity extends Activity {

    public static Intent buildIntent(Context context, long date) {
        Intent intent = new Intent(context, OperationDetailActivity.class);
        intent.putExtra(OperationDetailFragment.ARG_OPERATION_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_detail);

        // Show the Up button in the action bar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            long date = getIntent().getLongExtra(OperationDetailFragment.ARG_OPERATION_DATE, 0);
            getFragmentManager().beginTransaction()
                    .add(R.id.operation_detail_container, OperationDetailFragment.buildFragment(date))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, OperationListFragment.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
