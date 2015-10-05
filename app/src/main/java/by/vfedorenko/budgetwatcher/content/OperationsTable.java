package by.vfedorenko.budgetwatcher.content;

import android.content.ContentValues;

public class OperationsTable {
	public static final int TYPE_INCOMING = 1;
	public static final int TYPE_OUTGOING = 0;

	public static final String TABLE_NAME = "budgets";

	public static final String ID = "_id";
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String DATE = "date";

	public static Builder getBuilder() {
		return Builder.getInstance();
	}

	public static final class Builder {
		private final ContentValues values = new ContentValues();

		private static Builder sBuilder;

		private Builder() {
		}

		public static Builder getInstance() {
			if (sBuilder == null) {
				sBuilder = new Builder();
			}
			return sBuilder;
		}

		public Builder id(long id) {
			values.put(ID, id);
			return this;
		}

		public Builder type(int type) {
			if (type != TYPE_INCOMING && type != TYPE_OUTGOING) {
				throw new IllegalArgumentException("type != TYPE_INCOMING and type != TYPE_OUTGOING");
			}
			values.put(TYPE, type);
			return this;
		}

		public Builder amount(double amount) {
			values.put(AMOUNT, amount);
			return this;
		}

		public Builder date(long date) {
			values.put(DATE, date);
			return this;
		}

		public ContentValues build() {
			return values; // TODO defensive copy?
		}
	}
}
