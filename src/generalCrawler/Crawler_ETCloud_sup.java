// Great Idea #6: use Jsoup to create its own Document, use a browser to read that Document, get CSS element selector, 
// put into Crawler_ETCloud4 der Jsoup.

package generalCrawler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler_ETCloud_sup {

	public Crawler_ETCloud_sup() {}
	
	static List<String>urlListPool = new ArrayList<String>();
	public static void getDocument(){
		urlListPool.add("http://www.ettoday.net/news/20140822/392825.htm");
//		urlListPool.add("http://www.ettoday.net/news/news-list-2013-2-5-7.htm");
		for (int urls=0; urls< urlListPool.size(); urls++){
			String firstOfEachDay = urlListPool.get(urls);
			try {
				Document doc = Jsoup.connect(firstOfEachDay).get();
//				System.out.println(doc);									// 整篇文章的html
				
//				 .story > p:nth-child(1)									// 日期時間   這個請明瑾寫regex從網址抓應該比較好處理
				Elements datetime = doc.select(".story > p:nth-child(1)");
				for (Element dt : datetime){
					System.out.println(dt.ownText());
				}
				sepee("新聞內文");
//				 .story > p:nth-child(3)									// 新聞內文
				// TODO: n+x depends on whether there's an image at start of news.  ? n+5 : n+3
				// 可能也要寫regex處理
				Elements dailyNews = doc.select(".story > p:nth-child(n+3)");
				String newsText = "";
				for (Element news : dailyNews){
					newsText = news.ownText();
					if(checkText(newsText)){
						System.out.println(newsText);
					}
				}
				sepee("特定新聞聯結列表");
//				.listtxt_1 > h3:nth-child(1) > a:nth-child(1)				// 特定新聞聯結列表
				Elements links = doc.select(".listtxt_1 > h3 > a[href]");
				String et_prefix = "http://www.ettoday.net";
				for (Element link : links){
					System.out.println(et_prefix + link.attr("href"));
				}
				sepee("每天的其他新聞的聯結");
//				.menu_page > a:nth-child(3)									// 每天的其他新聞的聯結
				Elements additionalNewsList = doc.select(".menu_page > a[href]");
				for (Element link : additionalNewsList){
					System.out.println("0000");
					System.out.println(et_prefix + link.attr("href"));
				}
				sepee("分類");
				//.channel          										// 分類
				Elements cats = doc.select(".channel");
				for (Element cc : cats){
					System.out.println(cc.ownText());
				}
				sepee();
				//.menu_keyword > a:nth-child(1) > strong:nth-child(1)		// 關鍵字
				Elements keywords = doc.select(".menu_keyword > a:nth-child(n) > strong");
				for (Element key : keywords){
					System.out.println(key.ownText());
				}
				sepee();
				//.story > p:nth-child(2) > img:nth-child(1)				// 圖
				Elements pictures = doc.select(".story > p > img");
				if(pictures.first()!=null) {								// 這邊我們只抓第一張
					Element pic = pictures.first();
					System.out.println(pic.attr("src"));					// 圖之後要直接寫進db
					BufferedImage image = ImageIO.read(new URL(pic.attr("src")));
					ImageIO.write(image, "jpg",new File("website/out.jpg"));
					//http://stackoverflow.com/questions/21500339/storing-image-in-data-base-using-java-in-binary-format
				}
				sepee();
				//.contents_3 > h2:nth-child(2)								// 標題
				Elements titles = doc.select(".contents_3 > h2:nth-child(2)");
				if(titles.first()!=null){
					System.out.println(titles.first().ownText());
				}
				sepee();
				testGetDoc(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("url of this document is " + firstOfEachDay);
		} // end for
	}
	
	public static void main(String chickens[]){
		getDocument();
	}
	
	private static void sepee(){
		System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
	}
	
	private static void sepee(String place){
		System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=" + place);
	}
	
	private static Boolean checkText(String newsText){
		// patternArrows removes picture comments in ETtoday.  hopefully.
		// two each for right and left, one each for up and down
		// probably not needed with etcloud crawler v4, leaving it in just in case.
		String[] patterns = new String[4];
		patterns[0] = "[\u25BA\u25B6\u25C0\u25C4\u25BC\u25B2]";
		patterns[1] = "^(地方中心).*$";
		patterns[2] = "^(記者).*$";
		patterns[3] = "^.*(報導)$";
		if(newsText.matches(patterns[0]))
			return false;
		if(newsText.startsWith("地方中心"))
			System.err.println("===");
		if((newsText.matches(patterns[1]) || newsText.matches(patterns[2])) && newsText.matches(patterns[3]))
			return false;	// String.startsWith and String.endsWith just seems way less cooler.. goodbye efficiency~
		return true;
	}
	
	private static void testGetDoc(Document doc){
		System.out.println("========START");
		Elements dailyNews = doc.select(".story > p:nth-child(n+3)");
		String newsText = "";
		for (Element news : dailyNews){
			newsText = news.ownText();
			if(checkText(newsText)){
				System.out.println(newsText);
			}
		}
		System.out.println("========END");
	}
}
