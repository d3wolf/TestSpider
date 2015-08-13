package spider.demo;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupDemo {

	public void loadHtmlObject() throws IOException {
		// 直接从字符串中输入 HTML 文档
		String html = "<html><head><title> 开源中国社区 </title></head>" + "<body><p> 这里是 jsoup 项目的相关文章 </p></body></html>";
		Document doc1 = Jsoup.parse(html);
		
		// 从 URL 直接加载 HTML 文档
		 Document doc2 = Jsoup.connect("http://www.oschina.net/").get(); 
		 String title = doc2.title(); 

		 
		 Document doc3 = Jsoup.connect("http://www.oschina.net/") 
				  .data("query", "Java")   // 请求参数
				  .userAgent("I ’ m jsoup") // 设置 User-Agent 
				  .cookie("auth", "token") // 设置 cookie 
				  .timeout(3000)           // 设置连接超时时间
				  .post();                 // 使用 POST 方法访问 URL 
		 

		 // 从文件中加载 HTML 文档
		 File input = new File("D:/test.html"); 
		 //当 HTML 文档使用相对路径方式引用外部文件时，jsoup 会自动为这些 URL 加上一个前缀，也就是这个 baseURL
		 Document doc = Jsoup.parse(input,"UTF-8","http://www.oschina.net/");
	}
	
	/**
	 *  DOM 方式的元素解析
	 * @param doc
	 */
	public void parseHtmlElementByDOM(Document doc){
		 Element content = doc.getElementById("content"); 
		 Elements links = content.getElementsByTag("a"); 
		 for (Element link : links) { 
		  String linkHref = link.attr("href"); 
		  String linkText = link.text(); 
		 }
	}
	
	/**
	 * 选择器
	 * @param doc
	 */
	public void parseHtmlElementBySelector(Document doc) {
		Elements links = doc.select("a[href]"); // 具有 href 属性的链接
		Elements pngs = doc.select("img[src$=.png]");// 所有引用 png 图片的元素

		// 找出定义了 class=masthead 的元素
		Element masthead = doc.select("div.masthead").first();

		Elements resultLinks = doc.select("h3.r > a"); // direct a after h3
	}
	
	/**
	 * 处理URL
	 * @param doc
	 */
	public void parseUrl(Document doc){
		Element link = doc.select("a").first();
		String relHref = link.attr("href"); // == "/"
		/**
		 * 假如你需要取得一个绝对路径，需要在属性名前加 abs: 前缀。
		 * 这样就可以返回包含根路径的URL地址attr("abs:href")
		 */
		String absHref = link.attr("abs:href"); // "http://www.open-open.com/"
		
		absHref = link.absUrl("href");
	}
	
	public void moidfyElement(Document doc) {
		doc.select("div.comments a").attr("rel", "nofollow");
		// 为所有链接增加 rel=nofollow 属性
		doc.select("div.comments a").addClass("mylinkclass");
		// 为所有链接增加 class=mylinkclass 属性
		doc.select("img").removeAttr("onclick"); // 删除所有图片的 onclick 属性
		doc.select("input[type=text]").val(""); // 清空所有文本输入框中的文本
	}
}
