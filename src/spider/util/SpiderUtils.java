package spider.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SpiderUtils {
	
	public static Document getDocumentFromUrl(String url) throws IOException{
		 Document doc = Jsoup.connect(url) 
				  .data("query", "Java")   // 请求参数
				  .userAgent("Mozilla") // 设置 User-Agent 
				  .cookie("auth", "token") // 设置 cookie 
				  .timeout(7000)           // 设置连接超时时间
				  .get();                 // 使用 POST 方法访问 URL 
	
		 return doc;
	}
}
