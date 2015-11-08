package by.vfedorenko.budgetwatcher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BalanceUtils {
	public enum OperationType {
		INCREASE, DECREASE
	}

	private static final String KEY_BALANCE = "balance";

	public static float getCurrentBalance(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getFloat(KEY_BALANCE, 0);
	}

	public static void saveCurrentBalance(Context context, float balance) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putFloat(KEY_BALANCE, balance);
		editor.apply();
	}

	public static void changeCurrentBalance(Context context, float delta,  OperationType type) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		float balance = prefs.getFloat(KEY_BALANCE, 0);

		switch (type) {
			case INCREASE:
				balance += delta;
				break;
			case DECREASE:
				balance -= delta;
		}

		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(KEY_BALANCE, balance);
		editor.apply();
	}
}
