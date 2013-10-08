package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.authentication.User;
import models.event.Event;
import models.event.Location;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.mvc.Result;

public class LocationController extends AbstractController {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result addLocation(Long eventId) {
        Result result;
        Event event = Event.find.byId(eventId);

        if((event != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if((event.location == null) && (event.user.id.equals(user.id))) {
                JsonNode jsonNode = requestAsJson();

                try {
                    Location location = createModelFromJson(jsonNode, Location.class);
                    addLocationToEvent(event, location);
                    result = onAddSuccessResponse(location.id);
                } catch (Exception e) {
                    result = internalServerError();
                }
            } else {
                result = internalServerError();
            }
        } else {
            result = notFound();
        }

        return result;
    }

    private static void addLocationToEvent(Event event, Location location) {
        event.location = location;
        event.save();
    }

    private static Result onAddSuccessResponse(Long id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        responseJson.put("id", id);

        return created(responseJson);
    }
}
