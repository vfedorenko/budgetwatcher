package by.vfedorenko.budgetwatcher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BalanceUtils {
	public static enum OperationType {
		INCREASE, DECREASE
	}

	private static final String KEY_BALANCE = "balance";

	public static long getCurrentBalance(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_BALANCE, 0);
	}

	public static void saveCurrentBalance(Context context, long balance) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putLong(KEY_BALANCE, balance);
		editor.apply();
	}

	public static void changeCurrentBalance(Context context, long delta,  OperationType type) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		long balance = prefs.getLong(KEY_BALANCE, 0);

		switch (type) {
			case INCREASE:
				balance += delta;
				break;
			case DECREASE:
				balance -= delta;
		}

		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(KEY_BALANCE, balance);
		editor.apply();
	}
}
