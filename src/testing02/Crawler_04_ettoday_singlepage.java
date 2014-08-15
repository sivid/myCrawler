/* This program will demonstrate how to grab ettoday news pages.. I think.
 * 
 */
package testing02;

import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler_04_ettoday_singlepage {
	static List<String>urlPool = new ArrayList<String>();

	public Crawler_04_ettoday_singlepage() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args){ 
		// test pages, not sure if I've included every case.
		urlPool.add("http://www.ettoday.net/news/20140805/386334.htm");
		urlPool.add("http://www.ettoday.net/news/20130802/250478.htm");
		urlPool.add("http://www.ettoday.net/news/20120805/83618.htm");
		urlPool.add("http://www.ettoday.net/news/20140805/386165.htm");
		urlPool.add("http://www.ettoday.net/news/20120205/23024.htm");
		urlPool.add("http://www.ettoday.net/news/20130204/160307.htm");
		System.out.println("Fetching ...");
		// patternArrows removes picture comments in ETtoday.  hopefully.
		// two each for right and left, one each for up and down
		String patternArrows = "[\u25BA\u25B6\u25C0\u25C4\u25BC\u25B2]";
		String newsOwnText;
		try {
			for (String url : urlPool){
				Document doc = Jsoup.connect(url).get();
				Elements news_content = doc.select("div.story > p");
				// TODO: filename should be changed to indicate date, time, news category
				// motivation is to help us find out easily if these news has been scrapped or not
				FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);	
				for (Element news : news_content){
					newsOwnText = news.ownText(); 		// repeated String object initialization ftw.
					if(!newsOwnText.matches(patternArrows)){
						System.out.println(newsOwnText);
						fwContent.write(newsOwnText + "\r\n");
					}
				}
				Thread.sleep(1500);  // internet courtesy and stuff
				fwContent.write("\r\n=====================SeparatorText======================\r\n");
				fwContent.flush();
				fwContent.close();
			} // end foreach urlPool
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e){
			e.printStackTrace();
		}

	}
}
