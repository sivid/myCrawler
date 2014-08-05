// 03 04 combined by ming jing
// the first two were CrawlerExample_01.java and QuickStart.java, if you're interested.
// this package is number 02, because JSoup

package testing02;
// ttttt
import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler_03mixCrawler_04 {
	
	static List<String>urlPool = new ArrayList<String>();
	static List<String>content = new ArrayList<String>();	
	
	public Crawler_03mixCrawler_04() {
		urlPool.add("http://www.nownews.com/cat/society/r");
		for (int stratURL=0; stratURL< urlPool.size(); stratURL++){
			String url = urlPool.get(stratURL);
			print("Fetching %s...\n", url);
			String matching = "www.nownews.com";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements h2 = doc.select("h2 > a[href]");
				int i = 0;
				for (Element link : h2){
					if (link.toString().contains(matching)){
						String result=check(link.attr("abs:href"));
						if(result=="true"){
							urlPool.add(link.attr("abs:href"));
						}
						i++;
					}
				}
				url = urlPool.get(stratURL);
				writeNews(urlPool.get(stratURL));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Crawler_03mixCrawler_04 test = new Crawler_03mixCrawler_04();
	}



	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String check(String link) {
		String result="";
		for(int i=0; i<urlPool.size();i++){
			if(link.equals(urlPool.get(i))){
				result="faulse";
				break;
			}else{
				result="true";
				continue;
			}
		}		
		return result;
	} //
	
	private static void writeNews(String link) {
		String url = link;
		print("Fetching %s...\n", url);
		try {
			Document doc = Jsoup.connect(url).get();
			Elements news_content = doc.select("div.story_content > p");
			FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);
			for (Element news : news_content){
				System.out.println(news.text());
				fwContent.write(news.text()+"\r\n");
			}	
			fwContent.write("-----------------------\r\n");
			fwContent.flush();
			fwContent.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
