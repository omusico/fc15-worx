package hr.heisenbug.worxapp.services;

import com.google.gson.Gson;
import com.mongodb.*;
import hr.heisenbug.worxapp.models.Bucket;
import hr.heisenbug.worxapp.models.Model;
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

    public List<Model> findAll() {
        List<Model> models = new ArrayList<>();
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        return models;
    }

    public void createNewModel(String body) {
        Model model = new Gson().fromJson(body, Model.class);
        collection.insert(new BasicDBObject("title", model.getTitle())
                .append("alternateTitle", model.getAlternateTitle())
                .append("urn", model.getUrn())
                .append("parentBucket", model.getParentBucket())
                .append("createdOn", new Date())
                .append("localPreviewPath", model.getLocalPreviewPath())
                .append("localModelPath", model.getLocalModelPath())
                .append("externalModelPath", model.getExternalModelPath())
                .append("childModels", model.getChildModels())
                .append("modelType", model.getModelType()));
        //return model.getId();
    }

    public List<Model> findAllChildren(String bucketId) {
        List<Model> models = new ArrayList<>();
        BasicDBObject queryObject = new BasicDBObject("parentBucket", bucketId);
        DBCursor dbObjects = collection.find(queryObject);
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        System.out.println("findAllChildren() of " + bucketId + " = " + models);
        return models;
    }

    public List<Model> findAllAssemblies(String bucketId) {
        List<Model> models = new ArrayList<>();
        BasicDBObject queryObject = new BasicDBObject("parentBucket", bucketId);
        queryObject.append("modelType", "sldasm");
        DBCursor dbObjects = collection.find(queryObject);
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        queryObject.append("modelType", "iam");
        dbObjects = collection.find(queryObject);
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        queryObject.append("modelType", "prt");
        dbObjects = collection.find(queryObject);
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            models.add(new Model((BasicDBObject) dbObject));
        }
        System.out.println("findAllChildren() of " + bucketId + " = " + models);
        return models;
    }

    public Model find(String id) {
        return new Model((BasicDBObject) collection.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
