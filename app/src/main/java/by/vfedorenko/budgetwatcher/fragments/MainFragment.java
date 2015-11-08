package by.vfedorenko.budgetwatcher.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.adapters.OperationsAdapter;
import by.vfedorenko.budgetwatcher.presenters.OperationsViewPresenter;
import by.vfedorenko.budgetwatcher.realm.Operation;

public class MainFragment extends Fragment {
	public interface OperationsView {
		void onDataChanged(List<Operation> data);
		void onBalanceChanged(double balance);
	}

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		final TextView amount = (TextView) v.findViewById(R.id.amount_text_view);
		final RecyclerView operationsListView = (RecyclerView) v.findViewById(R.id.operations_recycler_view);
		operationsListView.setLayoutManager(new LinearLayoutManager(getActivity()));

		OperationsView view = new OperationsView() {
			@Override
			public void onDataChanged(List<Operation> data) {
				OperationsAdapter adapter = new OperationsAdapter(data);
				operationsListView.setAdapter(adapter);
			}

			@Override
			public void onBalanceChanged(double balance) {
				String format = "%.2f";
				if (balance % 1 == 0) {
					format = "%.0f";
				}

				amount.setText(String.format(format, balance));
			}
		};

		final OperationsViewPresenter presenter = new OperationsViewPresenter(view);
		presenter.init(getActivity());

		final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				presenter.syncSms(getActivity());
				swipeContainer.setRefreshing(false);
			}
		});

		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		return v;
	}
}
