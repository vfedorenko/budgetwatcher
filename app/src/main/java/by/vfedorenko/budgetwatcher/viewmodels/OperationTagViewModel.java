package by.vfedorenko.budgetwatcher.viewmodels;

import by.vfedorenko.budgetwatcher.realm.OperationTag;
import by.vfedorenko.budgetwatcher.utils.BalanceUtils;

public class OperationTagViewModel {
	private OperationTag mOperationTag;

	public OperationTagViewModel(OperationTag tag) {
		mOperationTag = tag;
	}

	public String getAmount() {
		return BalanceUtils.formatMoney(0);
	}

	public String getName() {
		return mOperationTag.getTag().getName();
	}

	public String getPercent() {
		return mOperationTag.getPercent() + "%";
	}
}
