package hr.heisenbug.worxapp;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by Viktor on 17/05/2015.
 */
public class Bucket {

    private String id;
    private String title;
    private String url;
    private String owner;
    private Date createdOn = new Date();

    public Bucket(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.title = dbObject.getString("title");
        this.url = dbObject.getString("url");
        this.owner = dbObject.getString("owner");
        this.createdOn = dbObject.getDate("createdOn");
    }

    public String getTitle() { return title; }

    public String getUrl() { return url; }

    public String getOwner() { return owner; }

    public Date getCreatedOn() { return createdOn; }
}
