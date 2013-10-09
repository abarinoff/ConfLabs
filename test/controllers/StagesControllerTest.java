package controllers;

import models.event.Event;
import models.event.Stage;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class StagesControllerTest extends AbstractControllerTest {
    private static final String EVENTS_URL = "events";
    public static final String STAGES_URL = "stages";

    @Test
    public void createStageWithIdShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createStageWithIdShouldNotAffectExistingStage() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        Stage stageBefore = Stage.find.byId(1L);
        stageBefore.getTitle();

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);
        Stage stageAfter = Stage.find.byId(1L);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
        assertEquals(stageBefore, stageAfter);
    }

    @Test
    public void createStageWithIdShouldNotCreateNewStage() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        List<Stage> stagesBefore = Event.find.byId(1L).getStages();
        int stagesBeforeSize = stagesBefore.size();

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        List<Stage> stagesAfter = Event.find.byId(1L).getStages();

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
        assertEquals(stagesBeforeSize, stagesAfter.size());
    }

    @Test
    public void createStageWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromString("{}");

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createStageWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromString("{\"foo\":\"bar\", \"bar\": \"foo\"}");

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createStageWithAjaxRequestShouldReturnCreated() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-without-id.json"));

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        assertThat(status(result)).isEqualTo(CREATED);
    }

    @Test
    public void createStageWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-without-id.json"));

        final Result result = callAction(routes.ref.StagesController.createStage(1L),
                fakeRequest(Helpers.POST, getStagesUrl(1L))
                        .withCookies(playSession)
                        .withHeader(Helpers.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                        .withJsonBody(createStageJson)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createStageShouldReturnJsonWithStageId() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-without-id.json"));

        List<Stage> stagesBefore = Event.find.byId(1L).getStages();
        stagesBefore.size();

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(1L), request);

        assertThat(status(result)).isEqualTo(CREATED);

        List<Stage> stagesAfter = Event.find.byId(1L).getStages();
        stagesAfter.removeAll(stagesBefore);
        long createdStageId = stagesAfter.get(0).id;
        String responseBody = contentAsString(result);
        JsonNode expectedJson = jsonNodeFromString("{\"id\":" + createdStageId + "}");
        JsonNode responseJson = jsonNodeFromString(responseBody);

        assertNotNull(createdStageId);
        assertEquals(expectedJson, responseJson);
    }

    @Test
    public void createStageForNonExistentEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode createStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-without-id.json"));

        FakeRequest request = fakeRequest(Helpers.POST, getStagesUrl(1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, createStageJson);

        final Result result = callAction(routes.ref.StagesController.createStage(100L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createStageForEventBelongingToAnotherUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-with-non-existing-id.json"));

        final Result result = callAction(routes.ref.StagesController.createStage(1L),
                fakeRequest(Helpers.POST, getStagesUrl(1L))
                        .withCookies(playSession)
                        .withHeader(Helpers.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                        .withHeader("X-Requested-With", "XMLHttpRequest")
                        .withJsonBody(updateStageJson)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateStageWithExistingIdShouldChangeExistingStage() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(OK);

        Stage savedStage = Event.find.byId(1L).getStages().get(0);
        Stage expectedStage = objectMapper.readValue(updateStageJson, Stage.class);

        assertEquals(expectedStage, savedStage);
    }

    @Test
    public void updateStageWithNonExistentIdShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-with-non-existing-id.json"));

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 100L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 100L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateStageShouldReturnEmptyJsonObject() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(OK);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = jsonNodeFromString(responseBody);

        JsonNode expectedJson = jsonNodeFromString("{}");

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void updateStageWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromString("{}");

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateStageWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromString("{\"foo\": \"bar\", \"bar\": \"foo\"}");

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 1L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateStageWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L),
                fakeRequest(Helpers.PUT, getStageUrl(1L, 1L))
                        .withCookies(playSession)
                        .withHeader(Helpers.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                        .withJsonBody(updateStageJson)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateStageWithInconsistentIdsShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 2L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 2L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateStageBelongingToAnotherEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-3.json"));

        FakeRequest request = fakeRequest(Helpers.PUT, getStageUrl(1L, 3L));
        request = expandToAuthorizedAjaxRequestWithJsonBody(request, updateStageJson);

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 3L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateStageForEventBelongingToAnotherUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");

        JsonNode updateStageJson = jsonNodeFromFile(new File("conf/test/data/controllers/stage-id-1.json"));

        final Result result = callAction(routes.ref.StagesController.updateStage(1L, 1L),
                fakeRequest(Helpers.PUT, getStageUrl(1L, 3L))
                        .withCookies(playSession)
                        .withHeader(Helpers.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                        .withHeader("X-Requested-With", "XMLHttpRequest")
                        .withJsonBody(updateStageJson)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deleteStageWithExistingIdShouldReturnEmptyJson() throws IOException {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        FakeRequest request = fakeRequest(Helpers.DELETE, getStageUrl(1L, 1L));
        request = expandToAuthorizedAjaxRequest(request);

        final Result result = callAction(routes.ref.StagesController.deleteStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(OK);

        String responseBody = contentAsString(result);
        JsonNode expectedJson = jsonNodeFromString("{}");
        JsonNode receivedJson = jsonNodeFromString(responseBody);

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void deleteStageWithNonExistentIdShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        FakeRequest request = fakeRequest(Helpers.DELETE, getStageUrl(1L, 100L));
        request = expandToAuthorizedAjaxRequest(request);

        final Result result = callAction(routes.ref.StagesController.deleteStage(1L, 100L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteStageWithNonAjaxRequestShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        FakeRequest request = fakeRequest(Helpers.DELETE, getStageUrl(1L, 1L))
                .withCookies(playSession)
                .withHeader(CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON);

        final Result result = callAction(routes.ref.StagesController.deleteStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteStageBelongingToAnotherEventShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        FakeRequest request = fakeRequest(Helpers.DELETE, getStageUrl(1L, 3L));
        request = expandToAuthorizedAjaxRequest(request);

        final Result result = callAction(routes.ref.StagesController.deleteStage(1L, 3L), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteStageBelongingToAnotherUserShouldReturnServerError() {
        startFakeApplication("test/data/controllers/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");

        FakeRequest request = fakeRequest(Helpers.DELETE, getStageUrl(1L, 1L))
                .withCookies(playSession)
                .withHeader("X-Requested-With", "XMLHttpRequest");

        final Result result = callAction(routes.ref.StagesController.deleteStage(1L, 1L), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    private FakeRequest expandToAuthorizedAjaxRequest(FakeRequest fakeRequest) {
        Http.Cookie playSession = getAuthorizationCookie();

        return fakeRequest
                .withCookies(playSession)
                .withHeader("X-Requested-With", "XMLHttpRequest");
    }

    private FakeRequest expandToAuthorizedAjaxRequestWithJsonBody(FakeRequest fakeRequest, JsonNode body) {
        return expandToAuthorizedAjaxRequest(fakeRequest)
                .withHeader(Helpers.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                .withJsonBody(body);
    }

    private String getStagesUrl(long eventId) {
        return EVENTS_URL + "/" + eventId + "/" + STAGES_URL;
    }

    private String getStageUrl(long eventId, long stageId) {
        return EVENTS_URL + "/" + eventId + "/" + STAGES_URL + "/" + stageId;
    }
}
