package spider.util;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import spider.bean.InfoBean;
import util.FileUtils;

public class MultiDownloader implements Runnable {

	private String savePath;

	private InfoBean bean;

	public MultiDownloader(InfoBean bean, String savePath) {
		this.bean = bean;
		this.savePath = savePath;
		File saveFolder = new File(savePath);
		if(!saveFolder.exists()){
			saveFolder.mkdir();
		}
	}

	@Override
	public void run() {
		String subPath = bean.getTitle();
		
		String threadName = Thread.currentThread().getName();
		System.out.println(String.format("%s download: %s", new Object[]{threadName, bean.getTitle()}));
		
		Map<String, String> map = bean.getPicMap();

		String path = this.savePath + subPath;
	//	System.out.println(path);

		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		} 

		String picUrl = "";
		try {
			Iterator<String> keyit = map.keySet().iterator();
			while (keyit.hasNext()) {
				String key = keyit.next();
				picUrl = map.get(key);

				FileUtils.download(picUrl, key, path);
		
			}
		} catch (Exception e) {
			System.out.println("-download " + picUrl + " failed. " + e.getMessage());
		}

		System.out.println(String.format("-%s download ok: %s", new Object[]{threadName, bean.getTitle()}));
	}
}
