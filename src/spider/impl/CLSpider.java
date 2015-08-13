package spider.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spider.bean.InfoBean;

public class CLSpider {
	private String baseUrl;
	private String url;
	
	public CLSpider(String baseUrl, String url){
		this.baseUrl = baseUrl;
		this.url = url;
	}

	public Document getDocumentFromUrl(String url) throws IOException{
		 Document doc = Jsoup.connect(url) 
				  .data("query", "Java")   // 请求参数
				  .userAgent("Mozilla") // 设置 User-Agent 
				  .cookie("auth", "token") // 设置 cookie 
				  .timeout(7000)           // 设置连接超时时间
				  .get();                 // 使用 POST 方法访问 URL 
	
		 return doc;
	}
	
	public List<InfoBean> getDaguerreItems(Document doc) throws UnsupportedEncodingException{
		List<InfoBean> infoBeans = new ArrayList<InfoBean>();
		
		Elements tableItems = doc.select("tr.tr3.t_one");//<tr align="middle" class="tr3 t_one">
		for(Element tableItem : tableItems){
			Elements links = tableItem.getElementsByTag("h3").select("a[href]"); 
			InfoBean bean = new InfoBean();
			bean.setTitle(links.text());
			bean.setUrl(baseUrl + links.attr("href"));

			infoBeans.add(bean);
		}
		
		return infoBeans;
	}
	
	public void getLinkPics(InfoBean bean){
		try {
			Document doc = getDocumentFromUrl(bean.getUrl());
			Elements picEles = doc.select("input[type=image]");// <input type="image" src="url">
			
			int i = 0;
			Map<String, String> picMap = new HashMap<String, String>();
			for(Element picEle : picEles){
				String picUrl = picEle.attr("src");
				String extName = picUrl.substring(picUrl.lastIndexOf("."));
				String key = (i++) + extName;
				picMap.put(key, picUrl);
			}
			
			bean.setPicMap(picMap);
		} catch (IOException e) {
			
		}
	}
	
	public List<InfoBean> getInfoBeans() throws IOException{
		Document doc = getDocumentFromUrl(url);
		List<InfoBean> infoBeans = getDaguerreItems(doc);
		
		return infoBeans;
	}
	
	
	public static void main(String[] args) throws Exception {}
}
