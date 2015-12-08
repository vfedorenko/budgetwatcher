package by.vfedorenko.budgetwatcher.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.activities.OperationDetailActivity;
import by.vfedorenko.budgetwatcher.databinding.OperationDetailBinding;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.viewmodels.OperationViewModel;
import io.realm.Realm;

/**
 * A fragment representing a single Operation detail screen.
 * This fragment is either contained in a {@link OperationListFragment}
 * in two-pane mode (on tablets) or a {@link OperationDetailActivity}
 * on handsets.
 */
public class OperationDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_OPERATION_DATE = "ARG_OPERATION_DATE";

    /**
     * The dummy content this fragment is presenting.
     */
    private Operation mOperation;

    public static OperationDetailFragment buildFragment(long date) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_OPERATION_DATE, date);
        OperationDetailFragment fragment = new OperationDetailFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OperationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long date = getArguments().getLong(ARG_OPERATION_DATE);
        mOperation = Realm.getDefaultInstance().where(Operation.class).equalTo(Operation.FIELD_DATE, date).findFirst();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OperationDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.operation_detail, container, false);
        OperationViewModel viewModel = new OperationViewModel(mOperation, null);
        binding.setOperation(viewModel);

        //binding.tagsListView

        return binding.getRoot();
    }
}
