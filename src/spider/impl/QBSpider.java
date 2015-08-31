package spider.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import spider.util.SQLite3Util;

/**
 * 糗事百科蜘蛛
 * 
 * @author Wolf
 *
 */
public class QBSpider {

	private String url;

	public QBSpider(String url) {
		this.url = url;
	}

	public Document getDocumentFromUrl() throws IOException {
		Document doc = Jsoup.connect(this.url).data("query", "Java") // 请求参数
				.userAgent("I ’ m jsoup") // 设置 User-Agent
				.cookie("auth", "token") // 设置 cookie
				.timeout(3000) // 设置连接超时时间
				.get(); // 使用 POST 方法访问 URL

		return doc;
	}

	public Document getDocumentFromUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).data("query", "Java") // 请求参数
				.userAgent("I ’ m jsoup") // 设置 User-Agent
				.cookie("auth", "token") // 设置 cookie
				.timeout(3000) // 设置连接超时时间
				.get(); // 使用 POST 方法访问 URL

		return doc;
	}

	public Elements parseText() throws IOException {
		Document doc = getDocumentFromUrl();
		Elements contents = doc.select("div.content");
		for (Element content : contents) {
			System.out.println(content.text());
			System.out.println("-----------------------------------------");
		}
		return contents;
	}

	public Elements parseText(Document doc) throws IOException {
		Elements content = doc.select("div.content");
		return content;
	}

	public Elements findMoreContents() throws IOException {
		Elements contents = new Elements(); 
		Document doc = getDocumentFromUrl(this.url);
		Elements pages = doc.select("div.pagenumber");
		
		for (Element page : pages) {
			Elements hrefs = page.select("a[href]");
			
			for (Element href : hrefs) {
			//	 System.out.println(href.baseUri());
				String moreUrl = /*this.baseUrl + */href.absUrl("href");
			//	 System.out.println(moreUrl);
			//	 System.out.println("-------------------------------------");
				contents.addAll(parseText(getDocumentFromUrl(moreUrl)));
			}
		}
		return contents;
	}
	
	/**
	 * 获取注释里的id
	 * @param content
	 * @return
	 */
	private String getContentId(Element content){
		String id = "";
		String str = content.toString();
		Pattern p = Pattern.compile("\\<!--(.+)--\\>");
		Matcher m = p.matcher(str);
		while (m.find()) {
			id = m.group(1);
		}
		
		return id;
	}
	
	public void addToDoc(Elements contents) {
		XWPFDocument doc = new XWPFDocument();

		for (Element content : contents) {
			XWPFParagraph paragraph = doc.createParagraph();
			paragraph.setWordWrap(true);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingLineRule(LineSpacingRule.EXACT);
			paragraph.setIndentationFirstLine(600);
			
			paragraph.setBorderBottom(Borders.DOUBLE);
			
			XWPFRun run = paragraph.createRun();
			run.setTextPosition(20);
			
			run.setText(content.text() + "\n");
			
			try {
				InputStream picIn = new	FileInputStream("g:\\itext2.jpg");
				run.addPicture(picIn, 0, "itext2.jpg", 200, 300);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			FileOutputStream out = new FileOutputStream("G:\\QBsimple.docx");
			doc.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToDB(Elements contents) throws ClassNotFoundException, SQLException{
		Connection conn = SQLite3Util.getConnection();

		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists QB_CONTENT;");
		stat.executeUpdate("create table QB_CONTENT (number, CONTENT);");
		
		PreparedStatement prep = conn.prepareStatement("insert into QB_CONTENT values (?, ?);");
		
		List<String> list = new ArrayList<String>();
		for (Element content : contents) {

			String id = getContentId(content);
			String text = content.text();
			
			prep.setString(1, id);
			prep.setString(2, text);
			
			prep.addBatch();
			
			list.add(id);
			if(list.contains(id)){
				System.out.println(id);
			}
	//		System.out.println("to db:"+id);
	//		System.out.println(text);
		}

		conn.setAutoCommit(false);
		prep.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	
		conn.close();
	}

	public static void main(String[] args) throws Exception {
		String baseUrl = "http://www.qiushibaike.com";
		String url = baseUrl + "/text";
		QBSpider spider = new QBSpider(url);
		Elements contents = spider.findMoreContents();
		spider.writeToDB(contents);
	}

}
