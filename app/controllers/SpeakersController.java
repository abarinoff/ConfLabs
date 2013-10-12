package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.authentication.User;
import models.event.Event;
import models.event.Speaker;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.mvc.Result;
import play.mvc.Results;

import java.io.IOException;

public class SpeakersController extends AbstractController {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result createSpeaker(Long eventId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            if (event.user.id.equals(user.id)) {
                JsonNode jsonBody = requestAsJson();
                try {
                    Speaker speaker = createModelFromJson(jsonBody, Speaker.class);
                    if (speaker.id == null) {
                        event.speakers.add(speaker);
                        event.save();
                        ObjectMapper objectMapper = new ObjectMapper();
                        ObjectNode responseNode = objectMapper.createObjectNode();
                        responseNode.put("id", speaker.id);

                        status = created(responseNode);
                    }
                    else {
                        status = internalServerError();
                    }
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                    status = internalServerError();
                }
            }
            else {
                status = internalServerError();
            }
        }
        else {
            status = notFound();
        }

        return status.as(CONTENT_TYPE_JSON);
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result updateSpeaker(Long eventId, Long speakerId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            JsonNode requestBody = requestAsJson();
            if (event.user.id.equals(user.id) && requestBody.size() > 0) {
                try {
                    Speaker speaker = createModelFromJson(requestBody, Speaker.class);
                    if (speaker.id != null && event.getSpeakerById(speakerId) != null) {
                        if (speaker.id.equals(speakerId)) {
                            speaker.update();
                            status = ok("{}");
                        }
                        else {
                            status = internalServerError();
                        }
                    }
                    else {
                        status = notFound();
                    }
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                    status = internalServerError();
                }
            }
            else {
                status = internalServerError();
            }
        }
        else {
            status = notFound();
        }

        return status.as("application/json");
    }
}
