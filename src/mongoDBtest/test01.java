package mongoDBtest;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

public class test01 {

	public test01() {}

	public static void main(String[] args) {
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient("10.120.25.119", 27017);
//			mongoClient = new MongoClient("10.120.25.120", 27017);
			DB db = mongoClient.getDB("mydb");
//			Set<String> colls = db.getCollectionNames();
//			for (String s : colls) {
//				System.out.println(s);
//			}
			
			DBCollection coll = db.getCollection("testCollection");
			BasicDBObject doc = new BasicDBObject("name", "MongoDB")
				.append("type", "database")
				.append("count", 1)
				.append("info", new BasicDBObject("x", 203).append("y", 102));
//			coll.insert(doc);
//			System.out.println(coll.getCount());
//			DBObject myDoc = coll.findOne();
//			System.out.println(myDoc);
//			for (int i=0; i<100; i++){
//				coll.insert(new BasicDBObject("i", i));
//			}
			System.out.println("coll.getCount() = " + coll.getCount());
			DBCursor cursor = coll.find();
//			try{											// so many lines, for one functionality
//				while(cursor.hasNext()){
//					System.out.println(cursor.next());
//				}
//			} finally{
//				cursor.close();
//			}
//			BasicDBObject query = new BasicDBObject("i",71);
			BasicDBObject query = new BasicDBObject("name","MongoDB");
			cursor = coll.find(query);
			try{
				while(cursor.hasNext()){
					System.out.println(cursor.next());
				}
			} finally{
				cursor.close();
			}
			
			// db.things.find({j: {$ne:3}, k: {$gt:10}});
			// db.things.find({i: {$gt:50}});
//			System.out.println("========================");				// System.err會有multi-threading的問題
//			query = new BasicDBObject("i", new BasicDBObject("$gt",50));
//			query = new BasicDBObject("i", new BasicDBObject("$gt", 20).append("$lte", 30));
//			cursor = coll.find(query);
//			try{
//				while(cursor.hasNext()){
//					System.out.println(cursor.next());
//				}
//			} finally{
//				cursor.close();
//			}
//			
//			for (String s : mongoClient.getDatabaseNames())
//				System.out.println(s);
//			mongoClient.dropDatabase("mydb");		// 沒有mydb也不會丟exception
//			System.out.println("====");
//			for (String s : mongoClient.getDatabaseNames())
//				System.out.println(s);
//			for (String s : db.getCollectionNames())
//				System.out.println(s);
//			System.out.println("====");
//			System.out.println(db.getCollectionNames());
//			DBCollection testCollection = db.getCollection("testCollection");	// 同上, 不會有exception
//			testCollection.drop();
//			System.out.println(db.getCollectionNames());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
