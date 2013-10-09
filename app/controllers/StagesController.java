package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.authentication.User;
import models.event.Event;
import models.event.Stage;
import models.event.slot.Slot;
import models.event.slot.SpeechSlot;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

import java.io.IOException;
import java.util.List;

public class StagesController extends AbstractController {

    @Restrict(@Group(Application.USER_ROLE))
    public static Result createStage(Long eventId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null) {
            if (event.user.id.equals(user.id)) {
                try {
                    JsonNode jsonNode = requestAsJson();
                    Stage stage = createModelFromJson(jsonNode, Stage.class);
                    if (stage.getId() == null) {
                        event.stages.add(stage);
                        event.save();
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode responseNode = mapper.createObjectNode();
                        responseNode.put("id", stage.id);

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

        return status.as("application/json");
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result updateStage(Long eventId, Long stageId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);

        if (event != null && event.getStageById(stageId) != null) {
            try {
                JsonNode jsonNode = requestAsJson();
                Stage stage = createModelFromJson(jsonNode, Stage.class);
                if (event.user.id.equals(user.id) && stage.id.equals(stageId)) {
                    stage.update();
                    status = ok("{}");
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
            status = notFound();
        }

        return status.as("application/json");
    }

    @Restrict(@Group(Application.USER_ROLE))
    @BodyParser.Of(BodyParser.Json.class)
    public static Result deleteStage(Long eventId, Long stageId) {
        if (!isXmlHttpRequest()) {
            return notFound();
        }

        AuthUser authUser = PlayAuthenticate.getUser(session());
        User user = User.findByAuthUserIdentity(authUser);

        Results.Status status;
        Event event = Event.find.byId(eventId);
        Stage stage;
        if (event != null && (stage = event.getStageById(stageId)) != null) {
            if (event.user.id.equals(user.id)) {
                try {
                    resetSlotStageReference(stage);
                    stage.delete();
                    status = ok("{}");
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

    private static void resetSlotStageReference(Stage stage) {
        List<Slot> slots = Slot.find
                .where()
                .eq("stage_id", stage.id)
                .findList();

        for (Slot slot : slots) {
            ((SpeechSlot) slot).setStage(null);
            slot.update();
        }
    }
}
