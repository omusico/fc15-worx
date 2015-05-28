package hr.heisenbug.worxapp;

import com.google.gson.Gson;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vlazar on 5/28/15.
 */
public class ModelService {
    private final DB db;
    private final DBCollection collection;

    public ModelService(DB db) {
        this.db = db;
        this.collection = db.getCollection("models");
    }

    public List<Model> findAll(){
        List<Model> models = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while(dbObjects.hasNext()){
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        return models;
    }

    public void createNewModel(String body){
        Model model = new Gson().fromJson(body, Model.class);
        collection.insert(new BasicDBObject("title", model.getTitle())
                .append("url", model.getUrn())
                .append("parent_bucket", model.getParentBucket())
                .append("createdOn", new Date()));
    }

    public Bucket find(String id){
        return new Bucket((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
