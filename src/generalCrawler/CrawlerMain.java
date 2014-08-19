/* Full rewrite.
 */

package generalCrawler;

public class CrawlerMain {

	public CrawlerMain() {}

	public static void main(String[] args) {
		// year, month, category
		//LC_ETCloud et_list = new LC_ETCloud(2013, 4, 7);
		// year, category.  Please see LC_ETCloud.java for category listing.
		LC_ETCloud et_list = new LC_ETCloud(2013, 7);
		et_list.CreateList();
		et_list.getNewsLinks(2013);
		et_list.getNewsItems();
		System.out.println("all done");
	}

}
