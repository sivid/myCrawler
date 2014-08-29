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
 * �F�v	1
 * �]�g	17
 * �׾�	13
 * ���	2
 * �j��	3
 * ���|	6
 * �a��	7
 * �ͬ�	5
 * �v�@	9
 * ��|	10
 * �ȹC	11
 * 3C	20
 */

/* TODO
 * 1. add support for whatever db we choose to use				DONE
 * 1.1 decide what metadata we should put in the db				DONE
 * 2. implement a better redundancy checker (not required)
 * 3. multithreading? (not required)
 */

package generalCrawler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

// NOTE: Grabbing monthly news items is not advisable.  see above.

public class Crawler_ETCloud5{
	private int year = -1;
	private int month = -1;
	private int cat = -1;
	
	List<String>urlListPool = new ArrayList<String>();			// firstOfEachDay
	//List<String>urlListPool2 = new ArrayList<String>();		// theRestOfEachDay
	//http://www.ettoday.net/news/news-list-2013-2-5-7.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-01.htm
	//http://www.ettoday.net/news/news-list-2013-02-05-7-2.htm
	final String ET_base = "http://www.ettoday.net";
	
	public Crawler_ETCloud5(int year, int month, int cat){
		this.year = year;
		this.month = month;
		this.cat = cat;
	}
	
	public Crawler_ETCloud5(int year, int cat){
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
		System.out.println("test complete");
//		for (String tested : urlPool)
//			System.out.println(tested);
	}
	
	public void getNewsAll(){
		System.out.println("Fetching ...");
		MongoClient mongoClient;
		String newsID = "";
		final String source = "ETToday";
		try {
			mongoClient = new MongoClient("10.120.25.119", 27017);
			for (String url : urlPool){
				Document doc = Jsoup.connect(url).get();
				DB db = mongoClient.getDB("mydb");
				DBCollection coll = db.getCollection("inventory");
				//http://www.ettoday.net/news/ 20140822 / 392825 .htm
				newsID="et" + url.substring(28, 36) + url.substring(37,43);	// this also has the nice side effect of checking for 
				BasicDBObject newsdoc = new BasicDBObject("_id", newsID)	// duplicate news, again, on MongoDB
								.append("Category", getCategory(doc))
								.append("NewsText", getNewsText(doc))
								.append("DateTime", getDateTime(url))
								.append("Title", getTitle(doc))
								.append("Source", source)
								.append("link", url)
								.append("img", getImage(doc))
								.append("keywords", getKeywords(doc));
				coll.insert(newsdoc);
				
				System.out.println("URL: " + url + " ...done.  Sleeping 500ms");
				Thread.sleep(500);  // internet courtesy and stuff
			} // end foreach urlPool
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getCategory(Document doc){
		Elements cats = doc.select(".channel");
		return cats.first().ownText();
	}
	private String getNewsText(Document doc){
		String newsOwnText = "";
		String newsFinalText = "";
		Elements news_content = doc.select(".story > p:nth-child(n+3)");			// variable.. +3 +4 +5
		for (Element news : news_content){
			newsOwnText = news.ownText();
			if(checkText(newsOwnText)){
				newsFinalText += newsOwnText;		// where do i put in a newline somewhere..
			}
		}
		return newsFinalText;
	}
	private Date getDateTime(String url) throws ParseException{
		String tempdate=url.substring(28, 36);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date datetime = formatter.parse(tempdate);
        return datetime;
	}	
	private String getTitle(Document doc){
		Elements titles = doc.select(".contents_3 > h2:nth-child(2)");
		if(titles.first()!=null){
			return titles.first().ownText();
		}else
			return "新聞標題";
	}
	private byte[] getImage(Document doc) throws MalformedURLException, IOException{
		Elements pictures = doc.select(".story > p > img");
		BufferedImage imm = null;
		byte[] immAsBytes = null;
		if(pictures.first()!=null) {								// 
			Element pic = pictures.first(); 
			imm = ImageIO.read(new URL(pic.attr("src")));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imm, "jpg", baos );
			baos.flush();
			immAsBytes = baos.toByteArray();
			baos.close();
			//http://stackoverflow.com/questions/21500339/storing-image-in-data-base-using-java-in-binary-format
		}else{
			imm = ImageIO.read(new File("website/ch.jpg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imm, "jpg", baos );
			baos.flush();
			immAsBytes = baos.toByteArray();
			baos.close();
		}
		return immAsBytes;
	}
	private List<String> getKeywords(Document doc){
		List<String> list =new ArrayList<String>();
		Elements keywords = doc.select(".menu_keyword > a:nth-child(n) > strong");
		for (Element key : keywords){
			list.add(key.ownText());
		}
		return list; 
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
		else if((newsText.matches(patterns[1]) || newsText.matches(patterns[2])) && newsText.matches(patterns[3]))
			return false;	// String.startsWith() and String.endsWith() just seems way less cooler somehow. efficiency byebye
		else if(!(newsText.trim().length() > 0))
			return false;
		return true;
	}

}
