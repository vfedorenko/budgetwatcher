package by.vfedorenko.budgetwatcher.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
	private static final SimpleDateFormat OPERATION_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy'\n'HH:mm", Locale.getDefault());

	public static String formatOperationTime(long time) {
		return OPERATION_TIME_FORMAT.format(new Date(time));
	}
}
