package hr.heisenbug.worxapp.helpers;

/**
 * Created by mathabaws on 5/31/15.
 */
public class NameTransformer {
    /**
     * Transform Strings to be valid in Autodesk API
     * @param nameToTransform String that needs to be transformed
     * @return transformed string
     */
    public static String transformName(String nameToTransform){
        String result ="";

        result = nameToTransform;
        result = result.toLowerCase();
        result = result.replace(" ", "_");
        result = result.replaceAll("[^-_.a-z0-9]", "");

        return result;
    }
}
