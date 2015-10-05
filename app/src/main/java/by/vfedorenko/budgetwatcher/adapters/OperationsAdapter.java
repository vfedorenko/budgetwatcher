package by.vfedorenko.budgetwatcher.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.content.Db;
import by.vfedorenko.budgetwatcher.content.OperationsTable;
import by.vfedorenko.budgetwatcher.utils.TimeUtils;

public class OperationsAdapter extends CursorAdapter {
	private class ViewHolder {
		protected View icon;
		protected TextView amount;
		protected TextView date;
	}

	private LayoutInflater inflater_;

	public OperationsAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater_ = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater_.inflate(R.layout.item_budget, null);

		ViewHolder holder = new ViewHolder();
		holder.icon = v.findViewById(R.id.icon);
		holder.amount = (TextView) v.findViewById(R.id.amount);
		holder.date = (TextView) v.findViewById(R.id.date);

		v.setTag(holder);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		int type = Db.getInt(cursor, OperationsTable.TYPE);

		double amount = Db.getDouble(cursor, OperationsTable.AMOUNT);
		long date = Db.getLong(cursor, OperationsTable.DATE);

		if (type == OperationsTable.TYPE_INCOMING) {
			holder.icon.setBackgroundResource(R.drawable.operation_icon_positive);
		} else if (type == OperationsTable.TYPE_OUTGOING) {
			holder.icon.setBackgroundResource(R.drawable.operation_icon_negative);
		}

		String format = "%.2f";
		if (amount % 1 == 0) {
			format = "%.0f";
		}
		holder.amount.setText(String.format(format, amount));
		holder.date.setText(TimeUtils.formatOperationTime(date));
	}
}
