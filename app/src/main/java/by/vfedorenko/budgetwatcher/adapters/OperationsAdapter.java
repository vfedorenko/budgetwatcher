package by.vfedorenko.budgetwatcher.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observer;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.content.DatabaseManager;
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

		int type = cursor.getInt(cursor.getColumnIndex(DatabaseManager.OperationsTable.TYPE));
		long amount = cursor.getLong(cursor.getColumnIndex(DatabaseManager.OperationsTable.AMOUNT));
		long date = cursor.getLong(cursor.getColumnIndex(DatabaseManager.OperationsTable.DATE));

		if (type == DatabaseManager.OperationsTable.TYPE_INCOMING) {
			holder.icon.setBackgroundResource(R.drawable.operation_icon_positive);
		} else if (type == DatabaseManager.OperationsTable.TYPE_OUTGOING) {
			holder.icon.setBackgroundResource(R.drawable.operation_icon_negative);
		}

		holder.amount.setText(String.valueOf(amount));
		holder.date.setText(TimeUtils.formatOperationTime(date));
	}
}
