package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.authentication.User;
import models.event.Event;
import models.event.Speaker;
import models.event.Speech;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.mvc.Result;
import play.mvc.Results;

import java.io.IOException;
import java.util.LinkedList;

public class SpeechesController extends AbstractController {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result createSpeech(Long eventId, Long speakerId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            JsonNode jsonRequest = requestAsJson();
            if (user.id.equals(event.user.id) && jsonRequest.size() > 0) {
                Speaker speaker = event.getSpeakerById(speakerId);
                if (speaker != null) {
                    System.out.println("speaker is not null");
                    try {
                        Speech speech = createModelFromJson(jsonRequest, Speech.class);
                        if (speech.id == null) {
                            if (speech.speakers == null) {
                                speech.speakers = new LinkedList<Speaker>();
                            }
                            speech.speakers.add(speaker);
                            event.speeches.add(speech);

                            event.save();
                            speech.saveManyToManyAssociations("speakers");

                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode node = mapper.createObjectNode();
                            node.put("id", speech.id);
                            status = created(node);
                        }
                        else {
                            status = internalServerError();
                        }
                    } catch (Exception e) {
                        status = internalServerError();
                    }
                }
                else {
                    status = notFound();
                }
            }
            else {
                status = internalServerError();
            }
        } else {
            status = notFound();
        }

        return status.as("application/json");
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result assignSpeechToSpeaker(Long eventId, Long speakerId, Long speechId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            if (event.user.id.equals(user.id)) {
                JsonNode request = requestAsJson();

                try {
                    Speech speech = createModelFromJson(request, Speech.class);
                    speech.saveManyToManyAssociations("speakers");
                    status = ok("{}");

                    Speaker existingSpeaker = null;
                    for(Speaker speaker : speech.speakers) {
                        if (speaker.id.equals(speakerId)) {
                            existingSpeaker = speaker;
                            break;
                        }
                    }


                } catch (IOException e) {
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
    public static Result unsetSpeech(Long eventId, Long speakerId, Long speechId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            if (user.id.equals(event.user.id)) {
                Speaker speaker = event.getSpeakerById(speakerId);
                if (speaker != null) {
                    Speech speech = speaker.getSpeechById(speechId);
                    if (speech != null) {
                        try {
                            if (speech.speakers.size() > 1) {
                                speech.speakers.remove(speaker);
                                speech.save();
                            }
                            else {
                                speech.delete();
                            }
                            status = ok("{}");
                        } catch (Exception e) {
                            status = internalServerError();
                        }
                    }
                    else {
                        status = notFound();
                    }
                }
                else {
                    status = notFound();
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
}
