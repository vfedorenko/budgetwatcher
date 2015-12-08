package by.vfedorenko.budgetwatcher.presenters;

import android.content.Context;

import by.vfedorenko.budgetwatcher.fragments.OperationListFragment;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.utils.BalanceUtils;
import by.vfedorenko.budgetwatcher.utils.SmsSyncronizer;
import io.realm.Realm;
import io.realm.RealmResults;

public class OperationsViewPresenter {
	private OperationListFragment.DataChangeListener mDataListener;

	public OperationsViewPresenter(OperationListFragment.DataChangeListener listener) {
		mDataListener = listener;
	}

	public void init(Context context) {
		mDataListener.onDataChanged(queryOperations(), BalanceUtils.getCurrentBalance(context));
	}

	public void syncSms(final Context context) {
		SmsSyncronizer.syncSms(context, new SmsSyncronizer.SmsSyncCallback() {
			@Override
			public void onFinished() {
				mDataListener.onDataChanged(queryOperations(), BalanceUtils.getCurrentBalance(context));
			}
		});
	}

	private RealmResults<Operation> queryOperations() {
		return Realm.getDefaultInstance().allObjectsSorted(Operation.class, "date", false);
	}
}
