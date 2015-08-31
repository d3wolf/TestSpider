package spider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spider.bean.InfoBean;
import spider.impl.CLSpider;
import spider.util.MultiDownloader;

public class CLSpiderInvoker {

	private static void downloadByPage(String baseUrl, String url, String savePath) throws IOException, InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(5);

		CLSpider parser = new CLSpider(baseUrl,url);
		List<InfoBean> infoBeans = parser.getInfoBeans();

		for (InfoBean bean : infoBeans) {

			parser.getLinkPics(bean);

			MultiDownloader run = new MultiDownloader(bean, savePath);
			Thread thread = new Thread(run);
			pool.execute(thread);
		}

		pool.shutdown();
		if (!pool.awaitTermination(7000, TimeUnit.MILLISECONDS)) {
			// 超时的时候向线程池中所有的线程发出中断(interrupted)。
			pool.shutdownNow();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String baseUrl = "http://cl.dzcl.pw/";
		String savePath = "g:\\clpics\\";
		for (int i = 5; i < 8; i++) {
			String url = baseUrl + "thread0806.php?fid=16&search=&page=" + i;
			System.out.println("#######download page: " + i);
			downloadByPage(baseUrl, url, savePath);
		}
	}
}
