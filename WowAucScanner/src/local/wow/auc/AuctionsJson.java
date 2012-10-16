package local.wow.auc;

public class AuctionsJson implements Comparable<AuctionsJson> {
	// {"auc":2065272742,"item":45736,"owner":"Ðèêêè","bid":400150,"buyout":400400,"quantity":1,"timeLeft":"MEDIUM"},
	protected long auc;
	protected long item;
	protected String owner;
	protected long bid;
	protected long buyout;
	protected long buyoutPerItem;
	protected long quantity;
	protected String timeLeft;

	public static String toGSC(long val) {
		long gold;
		long silver;
		long copper;

		gold = val / 10000;
		silver = (long) (Math.floor(val / 100) - gold * 100);
		copper = val - gold * 10000 - silver * 100;

		return "g" + gold + "s" + silver + "c" + copper;
	}

	public long getBuyoutPerItem() {
		return buyoutPerItem;
	}
	private void refreshBuyoutPerItem() {
		if (buyout == 0 || quantity == 0) {
			buyoutPerItem = 0;
		} else {
			buyoutPerItem = buyout / quantity;
		}
	}

	public long getAuc() {
		return auc;
	}

	public void setAuc(String auc) {
		this.auc = Long.valueOf(auc);
	}

	public long getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = Long.valueOf(item);
	}


	public void setItem(long item) {
		this.item =item;		
	}
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner.toUpperCase();
	}

	public long getBuyout() {
		return buyout;
	}

	public void setBuyout(String buyout) {
		this.buyout = Long.valueOf(buyout);
		refreshBuyoutPerItem();
	}

	public void setBuyout(long buyout) {
		this.buyout = buyout;
		refreshBuyoutPerItem();
	}

	public String getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(String timeLeft) {
		this.timeLeft = timeLeft.toUpperCase();
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
		refreshBuyoutPerItem();
	}

	public void setQuantity(String quantity) {
		this.quantity = Long.valueOf(quantity);
		refreshBuyoutPerItem();
	}

	public long getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = Long.valueOf(bid);
	}

	@Override
	public int compareTo(AuctionsJson aThat) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		// this optimization is usually worthwhile, and can
		// always be added
		if (this == aThat)
			return EQUAL;

		// primitive numbers follow this form

		if (this.getItem() < aThat.getItem())
			return BEFORE;
		if (this.getItem() > aThat.getItem())
			return AFTER;

		if (this.getBuyout() / this.getQuantity() < aThat.getBuyout()
				/ aThat.getQuantity())
			return BEFORE;
		if (this.getBuyout() / this.getQuantity() > aThat.getBuyout()
				/ aThat.getQuantity())
			return AFTER;

		if (this.getBid() / this.getQuantity() < aThat.getBid()
				/ aThat.getQuantity())
			return BEFORE;
		if (this.getBid() / this.getQuantity() > aThat.getBid()
				/ aThat.getQuantity())
			return AFTER;

		if (this.getQuantity() < aThat.getQuantity())
			return BEFORE;
		if (this.getQuantity() > aThat.getQuantity())
			return AFTER;

		if (this.getAuc() < aThat.getAuc())
			return BEFORE;
		if (this.getAuc() > aThat.getAuc())
			return AFTER;

		// objects, including type-safe enums, follow this form
		// note that null objects will throw an exception here
		int comparison = this.getOwner().compareTo(aThat.getOwner());
		if (comparison != EQUAL)
			return comparison;

		comparison = this.getTimeLeft().compareTo(aThat.getTimeLeft());
		if (comparison != EQUAL)
			return comparison;

		// all comparisons have yielded equality
		// verify that compareTo is consistent with equals (optional)
		assert this.equals(aThat) : "compareTo inconsistent with equals.";

		return EQUAL;
	}

	/**
	 * Define equality of state.
	 */
	@Override
	public boolean equals(Object aThat) {
		if (this == aThat)
			return true;
		if (!(aThat instanceof AuctionsJson))
			return false;

		AuctionsJson that = (AuctionsJson) aThat;
		return (this.getItem() == that.getItem())
				&& (this.getAuc() == that.getAuc())
				&& (this.getBid() == that.getBid())
				&& (this.getBuyout() == that.getBuyout())
				&& (this.getQuantity() == that.getQuantity())
				&& (this.getOwner().equals(that.getOwner()))
				&& (this.getTimeLeft().equals(that.getTimeLeft()));
	}

	/**
	 * A class that overrides equals must also override hashCode.
	 */
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, item);
		result = HashCodeUtil.hash(result, auc);
		result = HashCodeUtil.hash(result, bid);
		result = HashCodeUtil.hash(result, buyout);
		result = HashCodeUtil.hash(result, quantity);
		result = HashCodeUtil.hash(result, owner);
		result = HashCodeUtil.hash(result, timeLeft);
		return result;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("item = ").append(item)
				.append(", qty = ").append(quantity)
				.append(", buyout = ").append(toGSC(buyout))
				.append(", buyoutPerItem = ").append(toGSC(buyoutPerItem))
				.append(", bid = ").append(toGSC(bid))
				.toString();
	}
}
