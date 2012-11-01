package local.wow.auc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class AucScan {
	// item=72092/Ghost Iron Ore
	private static final Set<Long> ORES = new HashSet<Long>(
			Arrays.asList(new Long[] { 72092L, 72093L }));
	// распыление травы
	private static final Set<Long> LEAFS = new HashSet<Long>(
			Arrays.asList(new Long[] {72234L, 89639L, 79010L, 79011L, 72237L, 72235L}));

	public static void saveFile(InputStream inputStreamReader, File out)
			throws IOException {
		FileOutputStream fos = null;
		ReadableByteChannel rbc = null;
		try {
			rbc = Channels.newChannel(inputStreamReader);
			fos = new FileOutputStream(out);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (rbc != null) {
				rbc.close();
			}
		}
	}

	public static List<AuctionsJson> getAucData(InputStreamReader in) {
		JSONObject o = (JSONObject) JSONValue.parse(in);
		JSONArray lots = (JSONArray) ((JSONObject) o.get("alliance"))
				.get("auctions");
		List<AuctionsJson> list = new LinkedList<AuctionsJson>();
		// {"auc":2065272742,"item":45736,"owner":"Рикки","bid":400150,"buyout":400400,"quantity":1,"timeLeft":"MEDIUM"},
		Iterator<Object> iterator = lots.iterator();
		while (iterator.hasNext()) {
			JSONObject json = (JSONObject) iterator.next();
			// println(json);
			// println(json.get("auc"));
			AuctionsJson record = new AuctionsJson();
			record.setAuc(String.valueOf(json.get("auc")));
			record.setItem(String.valueOf(json.get("item")));
			record.setOwner(String.valueOf(json.get("owner")));
			record.setBid(String.valueOf(json.get("bid")));
			record.setBuyout(String.valueOf(json.get("buyout")));
			record.setQuantity(String.valueOf(json.get("quantity")));
			record.setTimeLeft(String.valueOf(json.get("timeLeft")));

			list.add(record);
		}
		return list;
	}

	public static FilesJson getUrlAndDate(InputStream in) {
		// {"files":[{"url":"http://eu.battle.net/auction-data/6e79a3a8025e9a71ee5eb3bd80df3d4e/auctions.json","lastModified":1350025690000}]}
		JSONObject o = (JSONObject) JSONValue.parse(in);
		JSONObject files = (JSONObject) ((JSONArray) o.get("files")).get(0);

		return new FilesJson((String) files.get("url"),
				(long) files.get("lastModified"));
	}

	public static boolean filter(AuctionsJson item) {
		// распыление руды
		if (ORES.contains(item.getItem()) && item.getBuyoutPerItem() < 8000 && item.getBuyout() > 0) {
			return true;
		}
		// распыление травы
		if (LEAFS.contains(item.getItem()) && item.getBuyoutPerItem() <= 15000 && item.getBuyout() > 0) {
			return true;
		}
		// "Чернила снова"
		if (item.getItem() == 79254 && item.getBuyoutPerItem() <= 59000 && item.getBuyout() > 0) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
	    Stopwatch stopwatch = new Stopwatch();
		URL filesUrl = new URL(
				"http://eu.battle.net/api/wow/auction/data/azuregos");
		boolean needUpdate = true;

		FilesJson newFiles = getUrlAndDate(filesUrl.openStream());
		File file = new File("files.json");
		File auctionsFile = new File("auctions.json");

		if (file.exists() && auctionsFile.exists()) {
			FilesJson oldFiles = getUrlAndDate(new FileInputStream(file));
			if (newFiles.getLastModified() == oldFiles.getLastModified()) {
				needUpdate = false;
			}
		}
		println("needUpdate = " + needUpdate + ", "
				+ newFiles.getLastModifiedDate());
		if (needUpdate) {
			saveFile(filesUrl.openStream(), file);

			URL aucUrl = new URL(newFiles.getUrl());
			println("downloading data");
		    stopwatch.start();
			saveFile(aucUrl.openStream(), auctionsFile);
		    stopwatch.stop();
			println("download complite in " + stopwatch);
		}
		println("loading data");
	    stopwatch.start();
		List<AuctionsJson> aucData = getAucData(new InputStreamReader(
				new FileInputStream(auctionsFile), StandardCharsets.UTF_8));
	    stopwatch.stop();
		println("loading data complete in " + stopwatch);

		Map<String, AuctionsJson> map = new HashMap<String, AuctionsJson>();
		// select item, buyout/qty, sum(buyout), sum(qty) from aucData group by item, buyout/qty
		for (AuctionsJson item : aucData) {
			if (filter(item)) {
				// println(item);
				AuctionsJson grpItem = map.get(item.getItem() + "|"
						+ item.getBuyoutPerItem());
				if (grpItem != null
						&& grpItem.getBuyoutPerItem() == item
								.getBuyoutPerItem()) {
					grpItem.setQuantity(grpItem.getQuantity()
							+ item.getQuantity());
					grpItem.setBuyout(grpItem.getBuyout() + item.getBuyout());
				} else {
					grpItem = new AuctionsJson();
					grpItem.setItem(item.getItem());
					grpItem.setBuyout(item.getBuyout());
					grpItem.setQuantity(item.getQuantity());
					map.put(item.getItem() + "|" + item.getBuyoutPerItem(),
							grpItem);
				}
			}
		}
		// println(aucData.size());
		aucData = new LinkedList<AuctionsJson>(map.values());
		Collections.sort(aucData, new CompareByBuyout());
		// println(aucData);

		int cnt = 0;
		for (AuctionsJson item : aucData) {
			println(item);
			++cnt;

			if (cnt > 9) {
				break;
			}
		}
		println("done.");
	}

	private static void println(Object object) {
		System.out.println(object);
	}
}
