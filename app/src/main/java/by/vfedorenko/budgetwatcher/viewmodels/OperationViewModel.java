package by.vfedorenko.budgetwatcher.viewmodels;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import by.vfedorenko.budgetwatcher.activities.AddTagsActivity;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.realm.OperationTag;
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

	public void onAddTagClick(View v) {
		Context c = v.getContext();
		c.startActivity(AddTagsActivity.createIntent(c, mOperation.getDate()));
	}
}
