package es.jdl.holydayapi.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.googlecode.objectify.Ref;

import java.lang.reflect.Type;

public class GsonRefSerializer implements JsonSerializer<Ref> {

    @Override
    public JsonElement serialize(Ref ref, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(ref.getKey().getName());
    }
}
