package local.wow.auc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuctionsJson implements Comparable<AuctionsJson> {
	// {"auc":2065272742,"item":45736,"owner":"Рикки","bid":400150,"buyout":400400,"quantity":1,"timeLeft":"MEDIUM"},
	protected long auc;
	protected long item;
	protected String owner;
	protected long bid;
	protected long buyout;
	protected long buyoutPerItem;
	protected long quantity;
	protected String timeLeft;
	protected String itemCaption;
	protected long profitBuyout;
	protected long profitBid;
	// распыление руды
	private static final Set<Long> ORES = new HashSet<Long>(
			Arrays.asList(new Long[] { 72092L, 72093L }));
	// распыление травы
	private static final Set<Long> LEAFS = new HashSet<Long>(
			Arrays.asList(new Long[] { 72234L, 89639L, 79010L, 79011L, 72237L,
					72235L }));

	private static final Map<Long, String> ITEM_NAMES;
	static {
		Map<Long, String> aMap = new HashMap<Long, String>();
		aMap.put(72092L, "Призрачная железная руда");
		aMap.put(72093L, "Кипарит");
		aMap.put(72234L, "Листья зеленого чая");
		aMap.put(89639L, "Оскверненная трава");
		aMap.put(79010L, "Снежная лилия");
		aMap.put(79011L, "Дурногриб");
		aMap.put(72237L, "Дождевой мак");
		aMap.put(72235L, "Ваточник");
		aMap.put(79254L, "Чернила снова");
		aMap.put(79251L, "Теневой краситель");
		ITEM_NAMES = Collections.unmodifiableMap(aMap);
	}

	public static String toGSC(long val) {
		long gold;
		long silver;
		long copper;
		StringBuilder result = new StringBuilder();

		gold = val / 10000;
		silver = (long) (Math.floor(val / 100) - gold * 100);
		copper = val - gold * 10000 - silver * 100;

		if (gold != 0) {
			result.append(gold).append("g");
		}
		if (silver != 0) {
			result.append(Math.abs(silver)).append("s");
		}
		if (copper != 0) {
			result.append(Math.abs(copper)).append("c");
		}
		if (result.toString().isEmpty()) {
			result.append("0");
		}

		return result.toString();
	}

	public long getBuyoutPerItem() {
		return buyoutPerItem;
	}

	private void refreshBuyoutPerItem() {
		if (quantity != 0) {
			buyoutPerItem = buyout / quantity;
		} else {
			buyoutPerItem = 0;
		}
	}

	public long getAuc() {
		return auc;
	}

	public void setAuc(String auc) {
		this.auc = Long.valueOf(auc);
	}

	public String getItemCaption() {
		return itemCaption;
	}

	private void setItemCaption() {
		this.itemCaption = ITEM_NAMES.get(item);
	}

	public long getItem() {
		return item;
	}

	public void setItem(String item) {
		setItem(Long.valueOf(item));
	}

	public void setItem(long item) {
		this.item = item;
		setItemCaption();
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
		refreshProfitBuyout();
	}

	private void refreshProfitBuyout() {
		final long ENCHANT_COST = 187500;
		
		if (quantity == 0 || buyout == 0) {
			profitBuyout = 0;
			return;
		}
		if (LEAFS.contains(item)) {
			profitBuyout = (long) (ENCHANT_COST * Math
					.floor((2.5 * quantity / 5) / 2 / 3) - buyout);
		}
		// "Чернила снова"
		else if (item == 79254) {
			profitBuyout = (long) ((ENCHANT_COST * Math.floor(quantity / 3)) - buyout);
		}
		// "Теневой краситель"
		else if (item == 79251) {
			profitBuyout = (long) ((ENCHANT_COST * Math.floor(quantity / 2 / 3)) - buyout);
		}
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
		refreshProfitBuyout();
	}

	public void setQuantity(String quantity) {
		setQuantity(Long.valueOf(quantity));
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
		return new StringBuilder().append("item = ").append(item)
				.append(", qty = ").append(quantity).append(", buyout = ")
				.append(toGSC(buyout)).append(", buyoutPerItem = ")
				.append(toGSC(buyoutPerItem)).append(", bid = ")
				.append(toGSC(bid)).append(", itemCaption = ")
				.append(itemCaption).append(", profitBuyout = ")
				.append(toGSC(profitBuyout)).toString();
	}
}
