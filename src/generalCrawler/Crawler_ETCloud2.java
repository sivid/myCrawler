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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.SAXException;
/*
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
*/
// NOTE: Grabbing monthly news items is not advisable.  see above.

public class Crawler_ETCloud2{
	private int year = -1;
	private int month = -1;
	private int cat = -1;
	
	List<String>urlListPool = new ArrayList<String>();			// firstOfEachDay
	//List<String>urlListPool2 = new ArrayList<String>();		// theRestOfEachDay
	//http://www.ettoday.net/news/news-list-2013-2-5-7.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-01.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-2.htm
	
	public Crawler_ETCloud2(int year, int month, int cat){
		this.year = year;
		this.month = month;
		this.cat = cat;
	}
	
	public Crawler_ETCloud2(int year, int cat){
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
	
	// http://www.ettoday.net/news/20130205/161762.htm
	// .news-time
	
	public void getNewsItems(){
		urlPool.add("http://www.ettoday.net/news/20130205/161762.htm");
		
		System.out.println("Fetching ...");
		// patternArrows removes picture comments in ETtoday.  hopefully.
		// two each for right and left, one each for up and down
		String patternArrows = "[\u25BA\u25B6\u25C0\u25C4\u25BC\u25B2]";
		String newsOwnText;
		try {
			for (String url : urlPool){
				Document doc = Jsoup.connect(url).get();
//				System.out.println("================");
//				System.out.println(doc);
//				System.out.println("================");
				Elements news_content = doc.select("div.story > p");
				// TODO: filename should be changed to indicate date, time, news category
				// motivation is to help us find out easily if these news has been scrapped or not
				// OTOH, we'll probably put those in a db, so..
				FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);	
				for (Element news : news_content){
					newsOwnText = news.ownText(); 		// repeated String object initialization ftw.
					if(!newsOwnText.matches(patternArrows)){
						System.out.println(newsOwnText);
						//fwContent.write(newsOwnText + "\r\n");
					}
				}
				
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//				factory.setNamespaceAware(true); // never forget this!
//				DocumentBuilder builder = factory.newDocumentBuilder();
//				org.w3c.dom.Document doc1 = builder.("sample.xml");
				
				// html body#local.news-page div.wrapper_box div.wrapper div.container_box div.container div.r1.clearfix div.c1 div.menu_bread_crumb span.news-time
				//Elements datetimes = doc.select("span.news-time");
				String timedate = doc.select("div.story>p:first-of-type").first().ownText(); // 	MAGIC!! IT DOESN'T EXIST, BUT IT DOES!!
//				which gives me a Next Great Idea(tm).  see Crawler_ETCloud3.java
				System.out.println("timeanddateis " + timedate);
				
//				System.out.println("datetime is " + datetimes.first().ownText());
				Thread.sleep(500);  // internet courtesy and stuff
//				fwContent.write("\r\n=====================SeparatorText======================\r\n");
				fwContent.flush();
				fwContent.close();
				System.out.println("URL: " + url + " ...done");
			} // end foreach urlPool
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e){
			e.printStackTrace();
		} /*catch (ParserConfigurationException e){
			e.printStackTrace();
		} catch (SAXException e){
			e.printStackTrace();
		} */
	}
	
	public void JsoupSoCrap(){
		String patternArrows = "[\u25BA\u25B6\u25C0\u25C4\u25BC\u25B2]";
		File input = new File("website/t1.html");
		String newsOwnText = "";
		try {
			Document doc = Jsoup.parse(input, "UTF-8");
			Elements news_content = doc.select("div.story > p");
			for (Element news : news_content){
				newsOwnText = news.ownText();
				if(!newsOwnText.matches(patternArrows)){
//					System.out.println("====+++++++++====");
//					System.out.println(newsOwnText);
				}
			}
			System.out.println("==========");
			Elements timedate = doc.select("div.story>p:first-of-type");
			for (Element tt : timedate){
				newsOwnText = tt.ownText();
				if(!newsOwnText.matches(patternArrows)){
					System.out.println(newsOwnText);
				}
			}
		} catch (IOException e) {
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
