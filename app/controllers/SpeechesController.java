package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.authentication.User;
import models.event.Event;
import models.event.Speech;
import org.codehaus.jackson.JsonNode;
import play.mvc.Result;
import play.mvc.Results;

import java.io.IOException;

public class SpeechesController extends AbstractController {

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

        return status.as("application/json");
    }
}
