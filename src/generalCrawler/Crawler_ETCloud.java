 /* do a for loop for year, month, day
 * create and store ArrayList<String> for a full year, grab those links, check for redundancy, grab news content
 * 
 * NOTE: it seems pages in one day could appear in another day. damn.
 * Accordingly I have included a checker for each urlListPool add.
 * 
 * However it still doesn't stop us from collecting redundant news items across sessions. 
 */

/* News categories
 * Not all categories are listed here.
 * 政治	1
 * 財經	17
 * 論壇	13
 * 國際	2
 * 大陸	3
 * 社會	6
 * 地方	7
 * 生活	5
 * 影劇	9
 * 體育	10
 * 旅遊	11
 * 3C	20
 */

/* TODO
 * 1. add support for whatever db we choose to use
 * 1.1 decide what metadata we should put in the db
 * 2. implement a better redundancy checker (not required)
 * 3. multithreading? (not required)
 */

package generalCrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// NOTE: Grabbing monthly news items is not advisable.  see above.

public class Crawler_ETCloud{
	private int year = -1;
	private int month = -1;
	private int cat = -1;
	
	List<String>urlListPool = new ArrayList<String>();			// firstOfEachDay
	//List<String>urlListPool2 = new ArrayList<String>();		// theRestOfEachDay
	//http://www.ettoday.net/news/news-list-2013-2-5-7.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-01.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-2.htm
	
	public Crawler_ETCloud(int year, int month, int cat){
		this.year = year;
		this.month = month;
		this.cat = cat;
	}
	
	public Crawler_ETCloud(int year, int cat){
		this.year = year;
		this.cat = cat;
	}
	
	private void InitList(){
		if(year == -1){
			System.err.println("did not assign year");
		}
		if(cat == -1)
			System.err.println("did not assign category");
		String y = Integer.toString(year);
		if (month != -1){
			String m = Integer.toString(month);
			for(int d=1;d<31;d++){		// first url of that day
				String url = "http://www.ettoday.net/news/news-list-" + y + "-" + m + 
						"-" + Integer.toString(d) + "-" + Integer.toString(cat) + ".htm";
				urlListPool.add(url);
			}
		}else{
			for(int m=1; m<12; m++){
				for(int d=1;d<31;d++){		// first url of that day
					String url = "http://www.ettoday.net/news/news-list-" + y + "-" + Integer.toString(m) + 
							"-" + Integer.toString(d) + "-" + Integer.toString(cat) + ".htm";
					urlListPool.add(url);
				}
			}
		}
	}
	
	public void CreateList(){
		InitList();
		System.out.println("IN");
		// now we test to see if any firstOfEachDay links have more pages.
		// they almost all do.
		for (int urls=0; urls< urlListPool.size(); urls++){
			String firstOfEachDay = urlListPool.get(urls);
			String matching = "news-list-";
			//.menu_page > a:nth-child(4)
			try {
				Document doc = Jsoup.connect(firstOfEachDay).get();
				//Elements dailyNews = doc.select(".menu_page > a[href]");
				Elements dailyNews = doc.select("a[href]");
				for (Element link : dailyNews){
					if (link.toString().contains(matching)){
						String newslink = link.attr("abs:href");
						if (checkLists(newslink)){
							urlListPool.add(link.attr("abs:href"));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // end for.  Now we should have a list of all news links of that month.
		for(String str : urlListPool){
			System.out.println(str);
		}
		/*for(String str : urlListPool2){
			System.out.println(str);
		}*/
		return;
	}
	
	List<String>urlPool = new ArrayList<String>();		// news links in a news list page
	public void getNewsLinks(int year){
		//String testurls = "http://www.ettoday.net/news/news-list-2013-2-5-7.htm";
		String check_year = Integer.toString(year);
		try{
			//urlListPool.addAll(urlListPool2);
			for(String testurl : urlListPool){
				Document doc = Jsoup.connect(testurl).get();
				Elements newsLinks = doc.select("a[href]");
				//Elements newsLinks = doc.select("#all-news-list > h3 > a");		// why can't I use this.. dang..
				String matching = "news/" + check_year;
				for (Element link : newsLinks){
					if (link.toString().contains(matching)){
						String newslink = link.attr("abs:href");
						//System.out.println("Found " + newslink);
						if(checkItems(newslink)){
							urlPool.add(newslink);
						}
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println("test complete");
		for (String tested : urlPool)
			System.out.println(tested);
	}
	
	public void getNewsItems(){
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
				// OTOH, we'll probably put those in a db, so..
				FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);	
				for (Element news : news_content){
					newsOwnText = news.ownText(); 		// repeated String object initialization ftw.
					if(!newsOwnText.matches(patternArrows)){
						//System.out.println(newsOwnText);
						fwContent.write(newsOwnText + "\r\n");
					}
				}
				Thread.sleep(500);  // internet courtesy and stuff
				fwContent.write("\r\n=====================SeparatorText======================\r\n");
				fwContent.flush();
				fwContent.close();
				System.out.println("URL: " + url + " ...done");
			} // end foreach urlPool
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	private Boolean checkItems(String link) {
		for(String testee : urlPool){
			if(link.equals(testee))
				return false;
		}
		return true;
	} // inefficient.  yes.  I know.
	
	private Boolean checkLists(String link) {
		for(String testee : urlListPool){
			if(link.equals(testee))
				return false;
		}
		return true;
	} // inefficient also.  yes.  I know. 

}
