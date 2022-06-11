package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by cuong on 3/20/2017.
 */
public class JsonHelper {

    private static Logger logger = LoggerFactory.getLogger(JsonHelper.class);
    private static final JsonHelper instance = new JsonHelper();
    private final ObjectMapper mapper;

    private JsonHelper() {
        mapper = new ObjectMapper();
    }

    public static JsonHelper getInstance() {
        return instance;
    }

    public String getJson(Object object) {
        try {
//            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return mapper.writeValueAsString(object);

        } catch (IOException e) {
            logger.error("Error when serializing object: " + e.getMessage());
            return null;
        }
    }

    public <T> T getObject(String jsonAsStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonAsStr, clazz);
        } catch (IOException e) {
            logger.error("Error when deserializing object: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}