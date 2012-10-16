package local.wow.auc;

import java.util.Date;

public final class FilesJson {
	private final String url;
	private final long lastModified;
	private final Date lastModifiedDate;
	
	public FilesJson(String url, long lastModified) {
		this.url = url;
		this.lastModified = lastModified;
		this.lastModifiedDate = new java.util.Date(lastModified);
//		System.out.println(this.url);
//		System.out.println(this.lastModifiedDate);
	}

	public long getLastModified() {
		return lastModified;
	}

	public String getUrl() {
		return url;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
}
