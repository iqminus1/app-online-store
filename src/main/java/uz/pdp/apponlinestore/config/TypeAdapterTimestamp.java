package uz.pdp.apponlinestore.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Objects;

public class TypeAdapterTimestamp extends TypeAdapter<Timestamp> {


    @Override
    public void write(JsonWriter jsonWriter, Timestamp timestamp) throws IOException {
        if (Objects.isNull(timestamp))
            jsonWriter.jsonValue(null);
        else
            jsonWriter.value(timestamp.getTime());
    }

    @Override
    public Timestamp read(JsonReader jsonReader) throws IOException {
        return new Timestamp(Long.parseLong(jsonReader.nextString()));
    }
}
