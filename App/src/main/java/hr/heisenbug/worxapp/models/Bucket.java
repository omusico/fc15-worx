package hr.heisenbug.worxapp.models;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;


/**
 * Created by Viktor on 17/05/2015.
 */
public class Bucket {

    private String id;
    private String title;
    private String owner;
    private String bucketKey;
    private String childModels;
    private Date createdOn = new Date();


    public Bucket(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.title = dbObject.getString("title");
        this.owner = dbObject.getString("owner");
        this.bucketKey = dbObject.getString("bucketKey");
        this.childModels = dbObject.getString("childModels");
        this.createdOn = dbObject.getDate("createdOn");
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBucketKey() {
        return bucketKey;
    }

    public void setBucketKey(String bucketKey) {
        this.bucketKey = bucketKey;
    }

    public String getChildModels() {
        return childModels;
    }

    public void setChildModels(String childModels) {
        this.childModels = childModels;
    }
}
