package cn.acyou.aries.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-15 下午 05:07]
 **/
public class MongoDbTester {
    public static void main(String[] args) {
        MongoCredential credential = MongoCredential.createScramSha1Credential("ark", "ark", "ark123".toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("192.168.1.188", 27017),
                Arrays.asList(credential));
        MongoDatabase database = mongoClient.getDatabase("ark");
        MongoCollection<Document> collection = database.getCollection("dbce00e1-efea-4a92-8491-e07b334635c2");
        System.out.println(collection.count());
    }
}
