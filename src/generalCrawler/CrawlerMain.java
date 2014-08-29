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
		Crawler_ETCloud5 crawl_et5 = new Crawler_ETCloud5(2013, 2, 20);			// use month only for testing
		crawl_et5.CreateList();
		crawl_et5.getNewsLinks();
		crawl_et5.getNewsAll();
		
		System.out.println("all done");
	}

}
