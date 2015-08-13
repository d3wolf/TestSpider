package spider.bean;

import java.util.Map;

public class InfoBean {

	private String url;
	private String title;
	private Map<String,String> picMap;

	public Map<String, String> getPicMap() {
		return picMap;
	}

	public void setPicMap(Map<String, String> picMap) {
		this.picMap = picMap;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString(){
		return title + "\n" + url;
	}
}
