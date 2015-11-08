package by.vfedorenko.budgetwatcher.presenters;

import android.content.Context;

import by.vfedorenko.budgetwatcher.fragments.MainFragment;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.utils.BalanceUtils;
import by.vfedorenko.budgetwatcher.utils.SmsSyncronizer;
import io.realm.Realm;
import io.realm.RealmResults;

public class OperationsViewPresenter {
	private MainFragment.OperationsView mView;

	public OperationsViewPresenter(MainFragment.OperationsView view) {
		mView = view;
	}

	public void init(Context context) {
		mView.onBalanceChanged(BalanceUtils.getCurrentBalance(context));
		mView.onDataChanged(queryOperations());
	}

	public void syncSms(final Context context) {
		SmsSyncronizer.syncSms(context, new SmsSyncronizer.SmsSyncCallback() {
			@Override
			public void onFinished() {
				mView.onDataChanged(queryOperations());
				mView.onBalanceChanged(BalanceUtils.getCurrentBalance(context));
			}
		});
	}

	private RealmResults<Operation> queryOperations() {
		return Realm.getDefaultInstance().allObjectsSorted(Operation.class, "date", false);
	}
}
