package hr.heisenbug.worxapp;

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
    private String childModels;
    private Date createdOn = new Date();


    public Bucket(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.title = dbObject.getString("title");
        this.owner = dbObject.getString("owner");
        this.childModels = dbObject.getString("childModels");
        this.createdOn = dbObject.getDate("createdOn");
    }

    public String getTitle() { return title; }

    public String getOwner() {
        return owner;
    }

    public Date getCreatedOn() { return createdOn; }

    public String getChildModels(){
        return childModels;
    }
}
