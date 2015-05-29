package hr.heisenbug.worxapp.models;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by vlazar on 5/28/15.
 */
public class Model {

    private String id;
    private String title;
    private String urn;
    private String parentBucket;
    private Date createdOn = new Date();

    public Model(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.title = dbObject.getString("title");
        this.urn = dbObject.getString("urn");
        this.parentBucket = dbObject.getString("parentBucket");
        this.createdOn = dbObject.getDate("createdOn");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrn() {
        return urn;
    }

    public String getParentBucket() {
        return parentBucket;
    }

    public Date getCreatedOn() {
        return createdOn;
    }
}