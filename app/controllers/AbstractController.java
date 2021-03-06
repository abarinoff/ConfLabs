package controllers;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.JsonView;

import java.io.IOException;

public class AbstractController extends Controller {

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String REQUESTED_WITH_HEADER = "X-Requested-With";
    public static final String REQUEST_TYPE_XMLHTTP = "XMLHttpRequest";

    public static boolean isXmlHttpRequest() {
        String header = request().getHeader(AbstractController.REQUESTED_WITH_HEADER);
        return (header != null && header.equals(AbstractController.REQUEST_TYPE_XMLHTTP));
    }

    public static <T> T createModelFromJson(JsonNode jsonNode, Class<T> modelClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonNode, modelClass);
    }

    public static JsonNode requestAsJson() {
        Http.RequestBody requestBody = request().body();
        return requestBody.asJson();
    }

    public static String createJsonStringWithView(Class<? extends JsonView> jsonViewClass, Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter objectWriter = mapper.writerWithView(jsonViewClass);
        String json = objectWriter.writeValueAsString(object);

        return json;
    }

    public static Result emptySuccessResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        return ok(responseJson);
    }
}
