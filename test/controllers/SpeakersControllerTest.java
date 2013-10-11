package controllers;

import models.event.Event;
import models.event.Speaker;
import org.codehaus.jackson.JsonNode;

import org.junit.Test;

import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

public class SpeakersControllerTest extends AbstractControllerTest {
    private static final String SPEAKERS_URL = "speakers";

    private static final String DATA_FILE_LOCATION = "conf/test/data/controllers/speakers/";
    
    @Test
    public void createSpeakerShouldReturnJsonWithSpeakerId() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        List<Speaker> speakersBefore = Event.find.byId(1L).speakers;
        speakersBefore.size();

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(CREATED);

        List<Speaker> speakersAfter = Event.find.byId(1L).speakers;
        speakersAfter.removeAll(speakersBefore);

        long speakerId = speakersAfter.get(0).id;

        JsonNode expectedJson = jsonNodeFromString("{\"id\": " + speakerId + "}");
        JsonNode receivedJson = jsonNodeFromString(contentAsString(result));

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void createSpeakerShouldReturnContentTypeJson() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(CREATED);
        assertThat(contentType(result)).isEqualTo(AbstractController.CONTENT_TYPE_JSON);
    }
    
    @Test
    public void createSpeakerWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromString("{\"foo\": \"bar\", \"bar\": \"foo\"}");
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeakerWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromString("{}");
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeakerWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createNonAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeakerWithJsonContainingIdShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-with-id-1.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(1L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeakerForNonExistentEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createNonAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeakersUrl(100L), requestJson);

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(100L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeakerForEventBelongingToDifferentUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyWithSpecifiedUser(Helpers.POST, getSpeakersUrl(1L), requestJson, "bar@gmail.com", "123456");

        final Result result = callAction(routes.ref.SpeakersController.createSpeaker(1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    private FakeRequest createAjaxRequestWithJsonBodyWithSpecifiedUser(String method, String url, JsonNode body, String login, String password) {
        return createAjaxRequestWithJsonBody(method, url, body)
                .withAuthorizationCookie(login, password);
    }

    private FakeRequest createAjaxRequestWithJsonBodyAsDefaultUser(String method, String url, JsonNode body) {
        return createAjaxRequestWithJsonBody(method, url, body)
                .withAuthorizationCookie();
    }

    private CustomFakeRequest createAjaxRequestWithJsonBody(String method, String url, JsonNode body) {
        return (CustomFakeRequest) new CustomFakeRequest(method, url)
                .withRequestedWithHeader()
                .withJsonBody(body);
    }

    private CustomFakeRequest createNonAjaxRequestWithJsonBodyAsDefaultUser(String method, String url, JsonNode body) {
        return (CustomFakeRequest) new CustomFakeRequest(method, url)
                .withAuthorizationCookie()
                .withJsonBody(body);
    }

    private String getSpeakerUrl(long eventId, long speakerId) {
        return getSpeakersUrl(eventId) + "/" + SPEAKERS_URL;
    }

    private String getSpeakersUrl(long eventId) {
        return "events/" + eventId + "/" + SPEAKERS_URL;
    }
}
