package hr.heisenbug.worxapp;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Viktor on 17/05/2015.
 */
public class BucketService {
    private final DB db;
    private final DBCollection collection;

    public BucketService(DB db) {
        this.db = db;
        this.collection = db.getCollection("buckets");
    }

    public List<Bucket> findAll() {
        List<Bucket> buckets = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            buckets.add(new Bucket((BasicDBObject) dbObject));
        }
        return buckets;
    }

    public void createNewBucket(String body) {
        Bucket bucket= new Gson().fromJson(body, Bucket.class);
        collection.insert(new BasicDBObject("title", bucket.getTitle())
                            .append("owner", bucket.getOwner())
                            .append("childModels", bucket.getChildModels())
                            .append("createdOn", new Date()));
    }

    public Bucket find(String id) {
        return new Bucket((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
//todo add update operation
//    public Bucket update(String bucketId, String body) {
//        Bucket bucket = new Gson().fromJson(body, Bucket.class);
//        collection.update(new BasicDBObject("_id", new ObjectId(bucketId)
//        return this.find(bucketId);
//    }
}
