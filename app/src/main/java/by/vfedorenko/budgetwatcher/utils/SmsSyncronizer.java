package by.vfedorenko.budgetwatcher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import by.vfedorenko.budgetwatcher.content.BudgetProvider;
import by.vfedorenko.budgetwatcher.content.DatabaseManager;

public class SmsSyncronizer {
	private static final String SEPARATOR = ",";
	private static final String WHITESPACE = " ";

	public static void syncSms(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				context.getContentResolver().delete(BudgetProvider.CONTENT_URI_OPERATIONS, null, null);

				Uri uri = Uri.parse("content://sms/inbox");
				String[] projection = new String[] {Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE};
				String selection = Telephony.Sms.Inbox.ADDRESS + " = ?";
				String[] selectionArgs = {SettingsManager.getPhoneNumber(context)};

				Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

				if (cursor.moveToFirst()) {
					do {
						String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.BODY));
						long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.Inbox.DATE));

						parseSmsToDatabase(context, body, date);
					} while (cursor.moveToNext());

					cursor.close();
				}
			}
		}).start();
	}

	public static void parseSmsToDatabase(Context context, String body, long date) {
		String balance = parseValue(body, SettingsManager.getBalancePrefix(context).split(SEPARATOR));

		String incoming = parseValue(body, SettingsManager.getIncomingPrefixes(context).split(SEPARATOR));
		if (incoming != null) {
			ContentValues cv = new ContentValues();
			cv.put(DatabaseManager.OperationsTable.BALANCE, balance);
			cv.put(DatabaseManager.OperationsTable.AMOUNT, incoming);
			cv.put(DatabaseManager.OperationsTable.TYPE, DatabaseManager.OperationsTable.TYPE_INCOMING);
			cv.put(DatabaseManager.OperationsTable.DATE, date);

			context.getContentResolver().insert(BudgetProvider.CONTENT_URI_OPERATIONS, cv);
			return;
		}

		String outgoing = parseValue(body, SettingsManager.getOutgoingPrefixes(context).split(SEPARATOR));
		if (outgoing != null) {
			ContentValues cv = new ContentValues();
			cv.put(DatabaseManager.OperationsTable.BALANCE, balance);
			cv.put(DatabaseManager.OperationsTable.AMOUNT, outgoing);
			cv.put(DatabaseManager.OperationsTable.TYPE, DatabaseManager.OperationsTable.TYPE_OUTGOING);
			cv.put(DatabaseManager.OperationsTable.DATE, date);

			context.getContentResolver().insert(BudgetProvider.CONTENT_URI_OPERATIONS, cv);
		}
	}

	private static String parseValue(String data, String[] prefixes) {
		String value = null;

		for (String prefix : prefixes) {
			prefix.trim();

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
