package hr.heisenbug.worxapp.services;

import com.google.gson.Gson;
import com.mongodb.*;
import hr.heisenbug.worxapp.models.Bucket;
import jdk.nashorn.internal.runtime.ScriptRuntime;
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
        Bucket bucket = new Gson().fromJson(body, Bucket.class);
        collection.insert(new BasicDBObject("title", bucket.getTitle())
                .append("owner", bucket.getOwner())
                .append("bucketKey", bucket.getBucketKey())
                .append("childModels", bucket.getChildModels())
                .append("createdOn", new Date()));
        System.out.println("last: " + latestId());
    }

    public Bucket find(String id) {
        return new Bucket((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }

    public String latestId() {
        List<String> result = new ArrayList<>();
        DBCursor cursor = db.getCollection(collection.getName()).find().limit(1).sort(new BasicDBObject("_id", -1));
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            result.add(obj.getString("_id"));
        }
        String id = "";
        id = result.get(0);
        return id;
    }

    /*public Bucket findByTitle(String title, Date createdOn){
        List<String> result = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        BasicDBObject field = new BasicDBObject();
        field.put("title",title);
        field.put("createdOn", createdOn);
        System.out.println("collection " + collection.getName());
        DBCursor cursor = db.getCollection(collection.getName()).find(query,field);
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            result.add(obj.getString("_id"));
        }
        return find(result.get(0));
    }*/

    //todo add update operation
//    public Bucket update(String bucketId, String body) {
//        //new bucket object from response
//        Bucket bucket = new Gson().fromJson(body, Bucket.class);
//
//        //current db entry
//        BasicDBObject selectQuery = new BasicDBObject("_id", new ObjectId(bucketId));
//
//        //new db entry for update
//        BasicDBObject updateQuery = new BasicDBObject("_id", new ObjectId(bucketId));
//        if (updateQuery.containsKey("bucketKey")) {
//            updateQuery.put("bucketKey", bucket.getBucketKey());
//        } else {
//            updateQuery.append("bucketKey", bucket.getBucketKey());
//        }
//
//        //update object in db
//        collection.update(selectQuery, updateQuery);
//
//
//        return this.find(bucketId);
//    }
}
