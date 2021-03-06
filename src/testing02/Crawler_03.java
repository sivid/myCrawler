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

public class Crawler_03 {

	public Crawler_03() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String url = "http://www.nownews.com/cat/society/r";
		print("Fetching %s...\n", url);
		String matching = "www.nownews.com";
		String[] news_links = new String[20];	// there's 20 in each page, set to 50 to be safe .... or not.
		try {
			Document doc = Jsoup.connect(url).get();
			Elements h2 = doc.select("h2 > a[href]");
			// print("\nnumber of h2: (%d)", h2.size());
			int i = 0;
			for (Element link : h2){
				//System.out.println(link.attr("abs:href"));
				if (link.toString().contains(matching)){
					news_links[i] = link.attr("abs:href");
					i++;
				}
				//print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
			}
			for (String link : news_links){
				print(link);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

}
