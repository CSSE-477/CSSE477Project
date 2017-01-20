package utilities;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by TrottaSN on 1/19/2017.
 */
public class JsonSerializationUtility implements JsonSerializer, JsonDeserializer {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("MM/dd/yy HH:mm:ss");
        Gson gson = builder.create();
        return gson.fromJson(json, typeOfT);
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("MM/dd/yy HH:mm:ss");
        Gson gson = new Gson();
        return gson.toJsonTree(src);
    }

    public Object deserializeJsonString(String json, Type typeOfT){
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    public String serializeJsonString(Object src, Type typeOfT) {
        Gson gson = new Gson();
        return gson.toJson(src);
    }
}
