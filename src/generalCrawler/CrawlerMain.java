/* Full rewrite.
 * 
 * ETCloud, AppleDaily, NOWNews, LTN, 
 */

package generalCrawler;

public class CrawlerMain {

	public CrawlerMain() {}

	public static void main(String[] args) {
//		Please see LC_ETCloud4.java for category listing.
//		Crawler_ETCloud4(year, month, category);
//		Crawler_ETCloud4(year, category);
		Crawler_ETCloud4 crawl_et4 = new Crawler_ETCloud4(2013, 2, 20);			// use month only for testing
		crawl_et4.CreateList();
		crawl_et4.getNewsLinks();
		crawl_et4.getNewsText();
		
		System.out.println("all done");
	}

}
