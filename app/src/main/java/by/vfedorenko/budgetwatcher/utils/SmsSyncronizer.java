package by.vfedorenko.budgetwatcher.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import by.vfedorenko.budgetwatcher.content.BudgetProvider;
import by.vfedorenko.budgetwatcher.content.OperationsTable;

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

				Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, Telephony.Sms.Inbox.DATE);

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
		boolean hasBalance = false;

		String balance = parseValue(body, SettingsManager.getBalancePrefix(context).split(SEPARATOR));
		if (balance != null) {
			hasBalance = true;
			BalanceUtils.saveCurrentBalance(context, Long.valueOf(balance.trim()));
		}

		String incoming = parseValue(body, SettingsManager.getIncomingPrefixes(context).split(SEPARATOR));
		if (incoming != null) {
			OperationsTable.Builder builder = OperationsTable.getBuilder();
			builder.amount(Long.valueOf(incoming)).date(date).type(OperationsTable.TYPE_INCOMING);

			context.getContentResolver().insert(BudgetProvider.CONTENT_URI_OPERATIONS, builder.build());

			if (!hasBalance) {
				BalanceUtils.changeCurrentBalance(context, Long.valueOf(incoming), BalanceUtils.OperationType.INCREASE);
			}
			return;
		}

		String outgoing = parseValue(body, SettingsManager.getOutgoingPrefixes(context).split(SEPARATOR));
		if (outgoing != null) {
			OperationsTable.Builder builder = OperationsTable.getBuilder();
			builder.amount(Double.valueOf(outgoing)).date(date).type(OperationsTable.TYPE_OUTGOING);

			context.getContentResolver().insert(BudgetProvider.CONTENT_URI_OPERATIONS, builder.build());

			if (!hasBalance) {
				BalanceUtils.changeCurrentBalance(context, Long.valueOf(outgoing), BalanceUtils.OperationType.DECREASE);
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
