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
 * 1.1 decide what metadata we should put in the db				DONE
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

public class Crawler_ETCloud4{
	private int year = -1;
	private int month = -1;
	private int cat = -1;
	
	List<String>urlListPool = new ArrayList<String>();			// firstOfEachDay
	//List<String>urlListPool2 = new ArrayList<String>();		// theRestOfEachDay
	//http://www.ettoday.net/news/news-list-2013-2-5-7.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-01.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-2.htm
	final String ET_base = "http://www.ettoday.net";
	
	public Crawler_ETCloud4(int year, int month, int cat){
		this.year = year;
		this.month = month;
		this.cat = cat;
	}
	
	public Crawler_ETCloud4(int year, int cat){
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
		// now we test to see if any firstOfEachDay links have more pages.
		// they almost all do.
		InitList();
		System.out.println("IN");
		String pagelink = ""; 
		for (int urls=0; urls< urlListPool.size(); urls++){
			String firstOfEachDay = urlListPool.get(urls);
			try {
				Document doc = Jsoup.connect(firstOfEachDay).get();
				Elements additionalNewsList = doc.select(".menu_page > a[href]");
				for (Element link : additionalNewsList){
					pagelink = ET_base + link.attr("href");
					if (checkLists(pagelink)){
						urlListPool.add(pagelink);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} // end for.  Now we should have a list of all news links of that day.
		for(String str : urlListPool){
			System.out.println(str);
		}
		return;
	}
	
	List<String>urlPool = new ArrayList<String>();		// news links in a news list page
	public void getNewsLinks(){
//		TODO: will probably rewrite this once we get a working db
//		String tt = "http://www.ettoday.net/news/news-list-2013-2-5-7.htm";
		String newslink = "";
		try{
			for(String testurl : urlListPool){
				Document doc = Jsoup.connect(testurl).get();
				Elements newsLinks = doc.select(".listtxt_1 > h3 > a[href]");
				for (Element link : newsLinks){
					newslink = ET_base + link.attr("href");
					if(checkItems(newslink)){
						urlPool.add(newslink);
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		//System.out.println("test complete");
		for (String tested : urlPool)
			System.out.println(tested);
	}
	
	public void getNewsText(){
		System.out.println("Fetching ...");
		String newsOwnText;
		try {
			for (String url : urlPool){
				Document doc = Jsoup.connect(url).get();
				Elements news_content = doc.select(".story > p:nth-child(n+3)");
				FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);
				fwContent.write(url + "\r\n");
				for (Element news : news_content){
					newsOwnText = news.ownText();
					if(checkText(newsOwnText)){
						//System.out.println(newsOwnText);
						fwContent.write(newsOwnText);
					}
				}
				Thread.sleep(500);  // internet courtesy and stuff
				fwContent.write("\r\n=====================SeparatorText======================\r\n");
				fwContent.flush();
				fwContent.close();
				System.out.println("URL: " + url + " ...done");
				System.out.println("來源網站是" + "東森新聞雲");
			} // end foreach urlPool
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
	
	private Boolean checkText(String newsText){
		// patterns[0] removes picture comments in ETtoday.  hopefully.
		// two each for right and left, one each for up and down
		// probably not needed with etcloud crawler v4, leaving it in just in case.
		String[] patterns = new String[4];
		patterns[0] = "[\u25BA\u25B6\u25C0\u25C4\u25BC\u25B2]";
		patterns[1] = "^(地方中心).*$";
		patterns[2] = "^(記者).*$";
		patterns[3] = "^.*(報導)$";
		if(newsText.matches(patterns[0]))
			return false;
		if((newsText.matches(patterns[1]) || newsText.matches(patterns[2])) && newsText.matches(patterns[3]))
			return false;	// String.startsWith() and String.endsWith() just seems way less cooler somehow. efficiency byebye
		return true;
	}

}
