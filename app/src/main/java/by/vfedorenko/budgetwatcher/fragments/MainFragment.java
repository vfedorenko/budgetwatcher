package by.vfedorenko.budgetwatcher.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.adapters.OperationsAdapter;
import by.vfedorenko.budgetwatcher.content.BudgetProvider;
import by.vfedorenko.budgetwatcher.content.OperationsTable;
import by.vfedorenko.budgetwatcher.utils.BalanceUtils;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int OPERATIONS_LOADER_ID = 1;

	private OperationsAdapter mAdapter;

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		long balance = BalanceUtils.getCurrentBalance(getActivity());

		TextView amount = (TextView) v.findViewById(R.id.amount_text_view);
		amount.setText(String.valueOf(balance));

		ListView operationsListView = (ListView) v.findViewById(R.id.operations_list_view);

		mAdapter = new OperationsAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		operationsListView.setAdapter(mAdapter);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(OPERATIONS_LOADER_ID, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().getLoader(OPERATIONS_LOADER_ID).forceLoad();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == OPERATIONS_LOADER_ID) {
			return new CursorLoader(getActivity(), BudgetProvider.CONTENT_URI_OPERATIONS, null, null, null,
					OperationsTable.DATE + " DESC");
		} else {
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == OPERATIONS_LOADER_ID) {
			mAdapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == OPERATIONS_LOADER_ID) {
			mAdapter.changeCursor(null);
		}
	}
}
