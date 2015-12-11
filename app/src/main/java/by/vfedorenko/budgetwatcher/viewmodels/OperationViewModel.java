package by.vfedorenko.budgetwatcher.viewmodels;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import by.vfedorenko.budgetwatcher.activities.AddTagsActivity;
import by.vfedorenko.budgetwatcher.fragments.OperationListFragment;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.realm.OperationTag;
import by.vfedorenko.budgetwatcher.utils.BalanceUtils;
import by.vfedorenko.budgetwatcher.utils.TimeUtils;

public class OperationViewModel {
	public static final int TYPE_INCOMING = 1;
	public static final int TYPE_OUTGOING = 0;

	private Operation mOperation;
	private OperationListFragment.OnOperationClickListener mListener;

	public OperationViewModel(Operation operation, OperationListFragment.OnOperationClickListener listener) {
		mOperation = operation;
		mListener = listener;
	}

	public String getAmount() {
		return BalanceUtils.formatMoney(mOperation.getAmount());
	}

	public int getTypeColor() {
		if (mOperation.getType() == TYPE_INCOMING) {
			return Color.GREEN;
		} else {
			return Color.RED;
		}
	}

	public String getTypeString() {
		if (mOperation.getType() == TYPE_INCOMING) {
			return "+";
		} else {
			return "-";
		}
	}

	public String getDateString() {
		return TimeUtils.formatOperationTime(mOperation.getDate());
	}

	public String getTags() {
		StringBuilder tags = new StringBuilder();

		for (OperationTag tag : mOperation.getTags()) {
			if (tags.length() > 0) {
				tags.append("; ");
			}

			if (tag.getTag() != null) {
				tags.append(tag.getTag().getName());
			} else {
				tags.append("null");
			}
		}

		return tags.toString();
	}

	public void onOperationClick(View v) {
		if (mListener != null) {
			mListener.onOperationClick(mOperation.getDate());
		}
	}

	public void onAddTagClick(View v) {
		Context context = v.getContext();
		context.startActivity(AddTagsActivity.createIntent(context, mOperation.getDate()));
	}
}
