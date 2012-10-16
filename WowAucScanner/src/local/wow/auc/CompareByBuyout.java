package local.wow.auc;

import java.util.Comparator;

public class CompareByBuyout  implements Comparator<AuctionsJson>{

	@Override
	public int compare(AuctionsJson o1, AuctionsJson o2) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		// this optimization is usually worthwhile, and can
		// always be added
		if (o1 == o2)
			return EQUAL;

		// primitive numbers follow o1 form
		if (o1.getBuyout()/o1.getQuantity() < o2.getBuyout()/o2.getQuantity())
			return BEFORE;
		if (o1.getBuyout()/o1.getQuantity() > o2.getBuyout()/o2.getQuantity())
			return AFTER;

		if (o1.getItem() < o2.getItem())
			return BEFORE;
		if (o1.getItem() > o2.getItem())
			return AFTER;


		// all comparisons have yielded equality
		// verify that compareTo is consistent with equals (optional)
		assert o1.equals(o2) : "compareTo inconsistent with equals.";

		return EQUAL;
	}

}
