// http://orajavasolutions.wordpress.com/2014/07/02/storing-images-in-a-collection-in-mongodb/

package mongoDBtest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class TestReadImage {

	public TestReadImage() {}

	public static void main(String[] args) {
		MongoClient mongoClient;
		byte[] immOut = null;
		String filename = "website/immOut.jpg";
		try {
			mongoClient = new MongoClient("10.120.25.119", 27017);
			DB db = mongoClient.getDB("mydb");
			DBCollection coll = db.getCollection("inventory");
			
			DBObject obj = coll.findOne(new BasicDBObject("_id", "et20020508"));
			immOut = (byte[])obj.get("img");
			FileOutputStream fout = new FileOutputStream(filename);
            fout.write(immOut);
            fout.flush();
            System.out.println("Photo retrieved and stored at "+filename);
            fout.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
