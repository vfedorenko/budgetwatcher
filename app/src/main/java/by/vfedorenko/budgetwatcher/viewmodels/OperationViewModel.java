package by.vfedorenko.budgetwatcher.viewmodels;

import android.graphics.Color;

import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.utils.TimeUtils;

public class OperationViewModel {
	public static final int TYPE_INCOMING = 1;
	public static final int TYPE_OUTGOING = 0;

	private Operation mOperation;

	public OperationViewModel(Operation operation) {
		mOperation = operation;
	}

	public String getAmount() {
		String format = "%.2f";
		if (mOperation.getAmount() % 1 == 0) {
			format = "%.0f";
		}
		return String.format(format, mOperation.getAmount());
	}

	public int getTypeColor() {
		if (mOperation.getType() == TYPE_INCOMING) {
			return Color.GREEN;
		} else {
			return Color.RED;
		}
	}

	public String getDateString() {
		return TimeUtils.formatOperationTime(mOperation.getDate());
	}
}
