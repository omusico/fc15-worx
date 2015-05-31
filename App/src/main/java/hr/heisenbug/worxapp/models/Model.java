package hr.heisenbug.worxapp.models;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by vlazar on 5/28/15.
 */
public class Model {

    private String id;
    private String title;
    private String alternateTitle;
    private String urn;
    private String parentBucket;
    private Date createdOn = new Date();
    //additional
    private String localPreviewPath;
    private String localModelPath;
    private String externalModelPath;
    private String childModels;
    private String modelType;

    public Model(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.title = dbObject.getString("title");
        this.alternateTitle = dbObject.getString("alternateTitle");
        this.urn = dbObject.getString("urn");
        this.parentBucket = dbObject.getString("parentBucket");
        this.createdOn = dbObject.getDate("createdOn");
        //additional
        this.localPreviewPath = dbObject.getString("localPreviewPath");
        this.localModelPath = dbObject.getString("localModelPath");
        this.externalModelPath = dbObject.getString("externalModelPath");
        this.childModels = dbObject.getString("childModels");
        this.modelType = dbObject.getString("modelType");

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
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

    public String getLocalPreviewPath() {
        return localPreviewPath;
    }

    public String getLocalModelPath() {
        return localModelPath;
    }

    public String getExternalModelPath() {
        return externalModelPath;
    }

    public String getChildModels() {
        return childModels;
    }

    public String getModelType() {
        return modelType;
    }
}