package by.vfedorenko.budgetwatcher.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!SettingsManager.isAutoParsingEnabled(context)) {
			return;
		}

		String address = SettingsManager.getPhoneNumber(context);

		SmsMessage[] smsArray;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			smsArray = Telephony.Sms.Intents.getMessagesFromIntent(intent);
		} else {
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				String format = bundle.getString("format");
				Object[] pdusObj = (Object[]) bundle.get("pdus");

				smsArray = new SmsMessage[pdusObj.length];
				for (int i = 0; i < pdusObj.length; i++) {
					smsArray[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
				}
			} else {
				smsArray = new SmsMessage[] {};
			}
		}

		for (SmsMessage sms : smsArray) {
			if (sms.getOriginatingAddress().equals(address)) {
				String body = sms.getDisplayMessageBody();
				long date = sms.getTimestampMillis();

				SmsSyncronizer.parseSmsToDatabase(context, body, date);
			}
		}
	}
}
