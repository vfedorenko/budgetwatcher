package by.vfedorenko.budgetwatcher.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Operation extends RealmObject {
	public static final String FIELD_AMOUNT = "amount";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_TAGS = "tags";

	private float amount;
	@PrimaryKey
	private long date;
	private int type;
	private RealmList<OperationTag> tags;

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public RealmList<OperationTag> getTags() {
		return tags;
	}

	public void setTags(RealmList<OperationTag> tags) {
		this.tags = tags;
	}
}
