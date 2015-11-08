package by.vfedorenko.budgetwatcher.realm;

import io.realm.RealmObject;

public class Operation extends RealmObject {
	private float amount;
	private long date;
	private int type;

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
}
