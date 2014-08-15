// the first two were CrawlerExample_01.java and QuickStart.java, if you're interested.
// this package is number 02, because JSoup
package testing02;
// ttttt
import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler_03_ettoday_get_list {
	static List<String>urlListPool = new ArrayList<String>();
	
	public Crawler_03_ettoday_get_list() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		//String url = "http://www.ettoday.net/news/news-list-2013-2-5-7-2.htm";
		// TODO: if we give ettoday a date of e.g. http://www.ettoday.net/news/news-list-2013-2-30-7.htm
		// the website will give us http://www.ettoday.net/news/news-list-2013-3-2-7.htm
		
		String year = "2013";
		String month = "2";
		String day = "5";
		String url_1 = "http://www.ettoday.net/news/news-list-" + year + "-" + month + "-" + day + "-7.htm";	// first url of that day
		urlListPool.add(url_1);

		try {
			for(String url: urlListPool){
				Document doc = Jsoup.connect(url).get();
				System.out.println(url);
				//Elements newslink_this_page = doc.select("#all-news-list > h3 > a[href]");
				Elements newslink_this_page = doc.select("a[href]");
				for (Element link : newslink_this_page){
					//System.out.println(link.text());
					System.out.println(link.attr("href"));		// TODO
					//System.out.println(link);
					/*
					if (link.toString().contains(matching)){
						news_links[i] = link.attr("abs:href");
						i++;
					}
					*/
					//print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
				}
				//System.out.println(doc.body().text());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
