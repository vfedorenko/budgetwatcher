package by.vfedorenko.budgetwatcher.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.vfedorenko.budgetwatcher.BR;
import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.activities.OperationDetailActivity;
import by.vfedorenko.budgetwatcher.databinding.ItemOperationBinding;
import by.vfedorenko.budgetwatcher.presenters.OperationsViewPresenter;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.viewmodels.OperationViewModel;

/**
 * An activity representing a list of Operations. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OperationDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OperationListFragment extends Fragment {
    public interface DataChangeListener {
        void onDataChanged(List<Operation> data, double balance);
    }

    public interface OnOperationClickListener {
        void onOperationClick(long date);
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_operation_list, container, false);

        final TextView amount = (TextView) v.findViewById(R.id.amount_text_view);
        final RecyclerView operationsListView = (RecyclerView) v.findViewById(R.id.operations_recycler_view);
        operationsListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DataChangeListener view = new DataChangeListener() {
            @Override
            public void onDataChanged(List<Operation> data, double balance) {
                OperationsAdapter adapter = new OperationsAdapter(data);
                operationsListView.setAdapter(adapter);

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

        if (v.findViewById(R.id.operation_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        return v;
    }

    public class OperationsAdapter extends RecyclerView.Adapter<OperationsAdapter.BindingHolder> {
        public class BindingHolder extends RecyclerView.ViewHolder {
            private ItemOperationBinding binding;

            public BindingHolder(View rowView) {
                super(rowView);
                binding = DataBindingUtil.bind(rowView);
            }

            public ViewDataBinding getBinding() {
                return binding;
            }
        }

        private List<Operation> mOperations;

        public OperationsAdapter(List<Operation> data) {
            mOperations = data;
        }

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation, parent, false);
            return new BindingHolder(v);
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            Operation operation = mOperations.get(position);

            OnOperationClickListener listener = new OnOperationClickListener() {
                @Override
                public void onOperationClick(long date) {
                    if (mTwoPane) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.operation_detail_container, OperationDetailFragment.buildFragment(date))
                                .commit();
                    } else {
                        startActivity(OperationDetailActivity.buildIntent(getActivity(), date));
                    }
                }
            };

            holder.getBinding().setVariable(BR.operation, new OperationViewModel(operation, listener));
            holder.getBinding().executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return mOperations.size();
        }

        // Clean all elements of the recycler
        public void clear() {
            mOperations.clear();
            notifyDataSetChanged();
        }

        // Add a list of items
        public void addAll(List<Operation> list) {
            mOperations.addAll(list);
            notifyDataSetChanged();
        }
    }
}
