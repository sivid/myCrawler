/* because bin-hong-go wants to see
 * 
 */
package testing02;

import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Crawler_05 {

	public Crawler_05() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String url = "http://buy.sinyi.com.tw/house/72471G.html";
		print("Fetching %s...\n", url);
		// String matching = "www.nownews.com";
		// String[] news_links = new String[20];	// there's 20 in each page, set to 50 to be safe .... or not.
		try {
			//Document doc = Jsoup.connect(url).get();
			Document doc = Jsoup.connect("http://buy.sinyi.com.tw/house/72471G.html")
				      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				      .referrer("http://www.sinyi.com.tw/?mkwid=s18GKvaii&pcrid=45056686107&pkw=%E6%B0%B8%E6%85%B6%E6%88%BF%E5%B1%8B&pmt=b&pdv=c&gclid=Cj0KEQjwu_eeBRCL3_zm8aOtvvkBEiQApfIbGPnuRTasKa1sSiDr9ub1onZ4Cm05M5n8BhpHjuXXqzwaAhGx8P8HAQ#first")
				      .get();
			// Elements h2 = doc.select("h2 > a[href]");
			Elements news_content = doc.select("tbody");
			for (Element news : news_content){
				System.out.println(news.text());
			}
			
			// print("\nnumber of h2: (%d)", h2.size());
			
			/*
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
			*/
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
