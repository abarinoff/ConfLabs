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
    public static Result createLocation(Long eventId) {
        Result result;
        Event event = Event.find.byId(eventId);

        if((event != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if((event.location == null) && (event.user.id.equals(user.id))) {
                JsonNode jsonNode = requestAsJson();

                try {
                    Location location = createModelFromJson(jsonNode, Location.class);
                    if(location.id == null) {
                        addLocationToEvent(event, location);
                        result = successResponseWithId(location.id);
                    } else {
                        result = internalServerError();
                    }
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

    @Restrict(@Group(Application.USER_ROLE))
    public static Result updateLocation(Long eventId) {
        Result result;
        Event event = Event.find.byId(eventId);

        if((event != null) && (event.location != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if(event.user.id.equals(user.id)) {
                JsonNode jsonNode = requestAsJson();

                try {
                    Location location = createModelFromJson(jsonNode, Location.class);

                    if(event.location.id.equals(location.id)) {
                        location.update();
                        result = emptySuccessResponse();
                    } else {
                        result = internalServerError();
                    }
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

    @Restrict(@Group(Application.USER_ROLE))
    public static Result deleteLocation(Long eventId) {
        Result result;
        Event event = Event.find.byId(eventId);

        if((event != null) && (event.location != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if(event.user.id.equals(user.id)) {
                try {
                    deleteLocationFromEvent(event);
                    result = emptySuccessResponse();
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

    private static void deleteLocationFromEvent(Event event) {
        Location oldLocation = event.location;
        event.location = null;
        event.update();
        oldLocation.delete();
    }

    private static Result successResponseWithId(Long id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        responseJson.put("id", id);

        return created(responseJson);
    }

    private static Result emptySuccessResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        return ok(responseJson);
    }
}
