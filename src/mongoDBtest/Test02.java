package mongoDBtest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
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
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Test02 {

	public Test02() {}

	public static void main(String[] args) {
		List<String>urlListPool = new ArrayList<String>();
		urlListPool.add("http://www.ettoday.net/news/20140822/392825.htm");
		MongoClient mongoClient;
		BufferedImage imm = null;
		byte[] immAsBytes = null;
		
		for (int urls=0; urls< urlListPool.size(); urls++){
			String firstOfEachDay = urlListPool.get(urls);
			try {
				Document doc = Jsoup.connect(firstOfEachDay).get();
				Elements pictures = doc.select(".story > p > img");
				
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
				} // end if else
				
				
				mongoClient = new MongoClient("10.120.25.119", 27017);
				DB db = mongoClient.getDB("mydb");
				DBCollection coll = db.getCollection("inventory");
				List<String> keywords = new ArrayList<String>();
				keywords.add("熱門權證");
				keywords.add("權證");
				keywords.add("個股分析");
				keywords.add("毅嘉");
				keywords.add("營收");
				String tempdate = "20020510";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		        Date datetime = formatter.parse(tempdate);
		        String newstext = "軟板良率持續提升，" + "\r\n" + "加上手機及車用機" + "\\r\\n" + "構件漸入佳...";	// no newlines, not a big deal though
				BasicDBObject mongodoc = new BasicDBObject("_id", "et20020510")
										.append("Category", "財經")
										.append("NewsText", newstext)
										.append("DateTime", datetime)									//
										.append("Title", "Q3可望雙創新高　認購權證熱")
										.append("Source", "ETToday")									//
										.append("link", "http://www.ettoday.net/news/20140822/392825.htm")	//
										.append("img", "byte[] immAsBytes")
										.append("keywords", keywords);
				coll.insert(mongodoc);
				
				
				DBCursor cursor = coll.find();
				try{
					while(cursor.hasNext()){
						System.out.println(cursor.next());
					}
				} finally{
					cursor.close();
				}
				
				
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
