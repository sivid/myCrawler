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
	static String [] getDay;
	static String day;
	public static void getDocument(){
		urlListPool.add("http://www.ettoday.net/news/20140822/392825.htm");
		urlListPool.add("http://www.ettoday.net/news/20140826/393865.htm");
//		urlListPool.add("http://www.ettoday.net/news/news-list-2013-2-5-7.htm");
		for (int urls=0; urls< urlListPool.size(); urls++){
//			getDay = urlListPool.get(urls).toString().split("/");
//			System.out.println(getDay[4]); // method one
			day=urlListPool.get(urls).toString().substring(28, 36);
			System.out.println(day); // method two
			
			String firstOfEachDay = urlListPool.get(urls);
			try {
				Document doc = Jsoup.connect(firstOfEachDay).get();
//				System.out.println(doc);									
				
//				 .story > p:nth-child(1)									
				Elements datetime = doc.select(".story > p:nth-child(1)");
				for (Element dt : datetime){
					System.out.println(dt.ownText());
				}
				sepee();
//				 .story > p:nth-child(3)									
				// TODO: n+x depends on whether there's an image at start of news.  ? n+5 : n+3
				
				Elements dailyNews = doc.select(".story > p:nth-child(n+3)");
				String newsText = "";
				for (Element news : dailyNews){
					newsText = news.ownText();
					if(checkText(newsText)){
						System.out.println(newsText);
					}
				}
				sepee("");
//				.listtxt_1 > h3:nth-child(1) > a:nth-child(1)				// 
				Elements links = doc.select(".listtxt_1 > h3 > a[href]");
				String et_prefix = "http://www.ettoday.net";
				for (Element link : links){
					System.out.println(et_prefix + link.attr("href"));
				}
				sepee("");
//				.menu_page > a:nth-child(3)									//
				Elements additionalNewsList = doc.select(".menu_page > a[href]");
				for (Element link : additionalNewsList){
					System.out.println("0000");
					System.out.println(et_prefix + link.attr("href"));
				}
				sepee("");
				//.channel          										// 
				Elements cats = doc.select(".channel");
				for (Element cc : cats){
					System.out.println(cc.ownText());
				}
				sepee();
				//.menu_keyword > a:nth-child(1) > strong:nth-child(1)		// 
				Elements keywords = doc.select(".menu_keyword > a:nth-child(n) > strong");
				for (Element key : keywords){
					System.out.println(key.ownText());
				}
				sepee();
				//.story > p:nth-child(2) > img:nth-child(1)				// 
				Elements pictures = doc.select(".story > p > img");
				if(pictures.first()!=null) {								// 
					Element pic = pictures.first();
					System.out.println(pic.attr("src"));					// 
					BufferedImage image = ImageIO.read(new URL(pic.attr("src")));
					ImageIO.write(image, "jpg",new File("website/out.jpg"));
					//http://stackoverflow.com/questions/21500339/storing-image-in-data-base-using-java-in-binary-format
				}
				sepee();
				//.contents_3 > h2:nth-child(2)								// 
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
//		if(newsText.startsWith("�a�褤��"))
//			System.err.println("===");
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
