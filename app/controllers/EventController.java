package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import models.authentication.User;
import models.event.Event;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.*;

import play.Logger;
import play.mvc.*;

import util.JsonView;

import java.io.IOException;

import java.util.List;

public class EventController extends AbstractController {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result getEvents() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        List<Event> events = user.events;
        Status status;
        try {
            String response = createJsonStringWithView(JsonView.ShortView.class, events);
            status = ok(response);
        }
        catch (IOException e) {
            Logger.error(e.getMessage(), e);
            status = internalServerError();
        }

        return status.as(CONTENT_TYPE_JSON);
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result getEventById(Long id) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Event event = Event.find.byId(id);

        Status response;
        if (event != null) {
            if (event.user.id.equals(user.id)) {
                try {
                    String responseBody = createJsonStringWithView(JsonView.FullView.class, event);
                    response = ok(responseBody);
                }
                catch (IOException e) {
                    Logger.error(e.getMessage(), e);
                    response = internalServerError();
                }
            }
            else {
                response = internalServerError();
            }
        } else {
            response = notFound();
        }

        return response.as(CONTENT_TYPE_JSON);
    }

    @Restrict(@Group(Application.USER_ROLE))
    @BodyParser.Of(BodyParser.Json.class)
    public static Result createEvent() {
        Http.RequestBody requestBody = request().body();
        JsonNode jsonNode = requestBody.asJson();

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Event newEvent;
        Results.Status status;
        ObjectMapper mapper = new ObjectMapper();
        try {
            newEvent = mapper.readValue(jsonNode, Event.class);
            user.events.add(newEvent);
            user.save();

            if (newEvent.id != null) {
                try {
                    String response = createJsonStringWithView(JsonView.ShortView.class, newEvent);
                    status = created(response);
                }
                catch (JsonGenerationException e) {
                    Logger.error(e.getMessage());
                    status = internalServerError();
                }
            }
            else {
                status = internalServerError();
            }
        }
        catch (IOException e) {
            Logger.error(e.getMessage(), e);
            status = internalServerError();
        }

        return status.as(CONTENT_TYPE_JSON);
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result updateEvent(long id) {
        Result result;
        Event event = Event.find.byId(id);

        if((event != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if(event.user.id.equals(user.id)) {
                JsonNode jsonNode = requestAsJson();

                try {
                    Event updatedEvent = createModelFromJson(jsonNode, Event.class);

                    if(event.id.equals(updatedEvent.id)) {
                        event.title = updatedEvent.title;
                        event.description = updatedEvent.description;
                        event.update();
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
    public static Result deleteEvent(long id) {
        Result result;
        Event event = Event.find.byId(id);

        if((event != null) && isXmlHttpRequest()) {
            AuthUser authUser = PlayAuthenticate.getUser(session());
            User user = User.findByAuthUserIdentity(authUser);

            if(event.user.id.equals(user.id)) {
                try {
                    event.delete();
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
}
