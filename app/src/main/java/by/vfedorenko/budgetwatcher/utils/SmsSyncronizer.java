package by.vfedorenko.budgetwatcher.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;

import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.viewmodels.OperationViewModel;
import io.realm.Realm;

public class SmsSyncronizer {
	public interface SmsSyncCallback {
		void onFinished();
	}

	private static final String SEPARATOR = ",";
	private static final String WHITESPACE = " ";

	public static void syncSms(final Context context, final SmsSyncCallback callback) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Realm realm = Realm.getDefaultInstance();
				realm.beginTransaction();
				realm.clear(Operation.class);
				realm.commitTransaction();

				Uri uri = Uri.parse("content://sms/inbox");
				String[] projection = new String[] {Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE};
				String selection = Telephony.Sms.Inbox.ADDRESS + " = ?";
				String[] selectionArgs = {SettingsManager.getPhoneNumber(context)};

				Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, Telephony.Sms.Inbox.DATE);

				if (cursor.moveToFirst()) {
					do {
						String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.BODY));
						long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.Inbox.DATE));

						parseSmsToDatabase(context, body, date);
					} while (cursor.moveToNext());

					cursor.close();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				callback.onFinished();
			}
		}.execute();
	}

	public static void parseSmsToDatabase(Context context, String body, long date) {
		Realm realm = Realm.getDefaultInstance();
		boolean hasBalance = false;

		String balance = parseValue(body, SettingsManager.getBalancePrefix(context).split(SEPARATOR));
		if (balance != null) {
			hasBalance = true;
			BalanceUtils.saveCurrentBalance(context, Float.valueOf(balance.trim()));
		}

		String incoming = parseValue(body, SettingsManager.getIncomingPrefixes(context).split(SEPARATOR));
		if (incoming != null) {
			realm.beginTransaction();

			Operation operation = realm.createObject(Operation.class);
			operation.setAmount(Float.valueOf(incoming));
			operation.setDate(date);
			operation.setType(OperationViewModel.TYPE_INCOMING);

			realm.commitTransaction();

			if (!hasBalance) {
				BalanceUtils.changeCurrentBalance(context, Float.valueOf(incoming), BalanceUtils.OperationType.INCREASE);
			}
			return;
		}

		String outgoing = parseValue(body, SettingsManager.getOutgoingPrefixes(context).split(SEPARATOR));
		if (outgoing != null) {
			realm.beginTransaction();

			Operation operation = realm.createObject(Operation.class);
			operation.setAmount(Float.valueOf(outgoing));
			operation.setDate(date);
			operation.setType(OperationViewModel.TYPE_OUTGOING);

			realm.commitTransaction();

			if (!hasBalance) {
				BalanceUtils.changeCurrentBalance(context, Float.valueOf(outgoing), BalanceUtils.OperationType.DECREASE);
			}
		}
	}

	private static String parseValue(String data, String[] prefixes) {
		String value = null;

		for (String prefix : prefixes) {
			if (data.contains(prefix)) {
				String[] ss = data.split(prefix);
				ss = ss[1].split(WHITESPACE);

				value = ss[1];
				break;
			}
		}

		return value;
	}
}
