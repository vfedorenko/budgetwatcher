package by.vfedorenko.budgetwatcher.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import by.vfedorenko.budgetwatcher.R;

public class SettingsManager {
	public static boolean isAutoParsingEnabled(Context context) {
		String key = context.getString(R.string.key_parsing_enabled);
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true);
	}

	public static String getPhoneNumber(Context context) {
		String key = context.getString(R.string.key_phone);
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
	}

	public static String getIncomingPrefixes(Context context) {
		String key = context.getString(R.string.key_incoming_prefix);
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
	}

	public static String getOutgoingPrefixes(Context context) {
		String key = context.getString(R.string.key_outgoing_prefix);
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
	}

	public static String getBalancePrefix(Context context) {
		String key = context.getString(R.string.key_balance_prefix);
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
	}
}
