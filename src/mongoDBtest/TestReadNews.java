package mongoDBtest;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class TestReadNews {

	public TestReadNews() {}

	public static void main(String[] args) {
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient("10.120.25.119", 27017);
			DB db = mongoClient.getDB("mydb");
			DBCollection coll = db.getCollection("inventory");
			System.out.println("coll.getCount() = " + coll.getCount());
//			DBObject newsOne = coll.findOne();
//			System.out.println(newsOne);
			
			DBCursor cursor = coll.find();
			try{											// so many lines, for one functionality
				while(cursor.hasNext()){
					System.out.println(cursor.next());
				}
			} finally{
				cursor.close();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
