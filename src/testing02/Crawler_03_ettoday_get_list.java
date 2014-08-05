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

public class Crawler_03_ettoday_get_list {

	public Crawler_03_ettoday_get_list() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String url = "http://www.ettoday.net/news/news-list-2013-02-05-7-2.htm";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements newslink_this_page = doc.select("#all-news-list > h3 > a");
			// print("\nnumber of h2: (%d)", h2.size());
			// int i = 0;
			for (Element link : newslink_this_page){
				//System.out.println(link.attr("href"));
				System.out.println(link.attr("href"));		// TODO
				/*
				if (link.toString().contains(matching)){
					news_links[i] = link.attr("abs:href");
					i++;
				}
				*/
				//print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
			}
			//System.out.println(doc.body().text());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
