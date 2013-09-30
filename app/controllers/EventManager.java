package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import models.authentication.User;
import models.event.Event;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.mvc.*;

import util.JsonView;

import java.io.IOException;

import java.util.List;

public class EventManager extends Controller {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result getEvents() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        List<Event> events = user.events;

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter objectWriter = mapper.writerWithView(JsonView.ShortView.class);

        Status status;
        try {
            String response = objectWriter.writeValueAsString(events);
            status = ok(response);
        }
        catch (IOException e) {
            Logger.error(e.getMessage(), e);

            JsonNode error = errorResponse();
            status = internalServerError(error);
        }

        return status.as("application/json");
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result getEventById(Long id) {
        String header = request().getHeader("X-Requested-With");
        if (header == null || !header.equals("XMLHttpRequest")) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Event event = Event.find.byId(id);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter objectWriter = mapper.writerWithView(JsonView.FullView.class);

        Status response;
        if (event != null) {
            try {
                String responseBody = objectWriter.writeValueAsString(event);
                response = ok(responseBody);
            }
            catch (IOException e) {
                Logger.error(e.getMessage(), e);

                JsonNode error = errorResponse();
                response = internalServerError(error);
            }
        } else {
            response = notFound(errorResponse("Event with a given id was not found"));
        }

        return response.as("application/json");
    }

    @Restrict(@Group(Application.USER_ROLE))
    @BodyParser.Of(BodyParser.Json.class)
    public static Result addEvent() {
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
                ObjectWriter objectWriter = mapper.writerWithView(JsonView.ShortView.class);
                try {
                    String response = objectWriter.writeValueAsString(newEvent);
                    status = ok(response);
                }
                catch (JsonGenerationException e) {
                    Logger.error("A new Event with the id " + newEvent.id + " had been saved. " +
                            "Couldn't generate JSON response with a new Event model's data");

                    JsonNode error = errorResponse();
                    status = internalServerError(error);
                }
            }
            else {
                Logger.error("Couldn't save new Event for a user with id " + user.id);

                JsonNode error = errorResponse();
                status = internalServerError(error);
            }
        }
        catch (IOException e) {
            Logger.error(e.getMessage(), e);

            JsonNode error = errorResponse();
            status = internalServerError(error);
        }

        return status.as("application/json");
    }

    // @todo Implementation required
    @Restrict(@Group(Application.USER_ROLE))
    public static Result updateEvent(Long id) {
        return status(200);
    }

    private static JsonNode errorResponse(String ... message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        if (message.length == 1) {
            responseJson.put("error", message[0]);
        }
        else {
            responseJson.put("error", true);
        }

        return responseJson;
    }
}
