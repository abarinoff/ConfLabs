package controllers;

import models.event.Event;
import models.event.Speaker;
import models.event.Speech;
import org.codehaus.jackson.JsonNode;

import org.junit.Test;

import play.mvc.Http;
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

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "user-1-event-1-speaker-1.json"));
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
    
    @Test
    public void updateSpeakerShouldReturnEmptyJsonAndOk() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "user-1-event-1-speaker-1.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(OK);

        JsonNode expectedResponseBody = jsonNodeFromString("{}");
        JsonNode actualResponseBody = jsonNodeFromString(contentAsString(response));

        assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    public void updateSpeakerWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "user-1-event-1-speaker-1.json"));
        FakeRequest fakeRequest = createNonAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(NOT_FOUND);
    }
    
    @Test
    public void updateSpeakerWithJsonWithoutIdShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-without-id.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeakerWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromString("{}");
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeakerWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromString("{\"id\":1, \"foo\":\"bar\", \"bar\": \"foo\"}");
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeakerWithNonExistentIdShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speaker-with-non-existent-id.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 100L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 100L), fakeRequest);

        assertThat(status(response)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeakerWithInconsistentIdsShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "user-1-event-1-speaker-1.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 2L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 2L), fakeRequest);

        assertThat(status(response)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeakerBelongingToDifferentEventOfTheSameUserShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "event-1-with-speaker-of-event-2.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeakerUrl(1L, 3L), requestJson);

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 3L), fakeRequest);

        assertThat(status(response)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeakerForEventBelongingToDifferentUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        JsonNode requestJson = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "user-1-event-1-speaker-1.json"));
        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyWithSpecifiedUser(Helpers.PUT, getSpeakerUrl(1L, 1L), requestJson, "bar@gmail.com", "123456");

        final Result response = callAction(routes.ref.SpeakersController.updateSpeaker(1L, 1L), fakeRequest);

        assertThat(status(response)).isEqualTo(INTERNAL_SERVER_ERROR);
    }
    
    @Test
    public void deleteSpeakerWithNonAjaxRequestShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyNonAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 1L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeakerWithAjaxRequestShouldReturnEmptyJsonAndOk() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 1L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(OK);

        JsonNode expectedJson = jsonNodeFromString("{}");
        JsonNode receivedJson = jsonNodeFromString(contentAsString(result));

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void deleteSpeakerWithAjaxRequestShouldRemoveSpeaker() throws IOException {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        Speaker speaker = Speaker.find.byId(1L);
        assertNotNull(speaker);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 1L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);
        status(result);

        speaker = Speaker.find.byId(1L);

        assertNull(speaker);
    }
    
    @Test
    public void deleteSpeakerWithNonExistentIdShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 100L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 100L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeakerBelongingToDifferentEventOfTheSameUserShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 3L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 3L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeakerBelongingToDifferentEventOfTheSameUserShouldNotRemoveSpeaker() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        Speaker speaker = Speaker.find.byId(3L);
        assertNotNull(speaker);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 3L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 3L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        speaker = Speaker.find.byId(3L);
        assertNotNull(speaker);
    }

    @Test
    public void deleteSpeakerForEventBelongingToDifferentUserShouldReturnServerError() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsSpecificUser(Helpers.DELETE, getSpeakerUrl(1L, 1L), "bar@gmail.com", "123456");
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deleteSpeakerForEventBelongingToDifferentUserShouldNotRemoveSpeaker() {
        startFakeApplication("test/data/controllers/speakers/event-with-all-entities.yml");

        Speaker speaker = Speaker.find.byId(1L);
        assertNotNull(speaker);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsSpecificUser(Helpers.DELETE, getSpeakerUrl(1L, 1L), "bar@gmail.com", "123456");
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        speaker = Speaker.find.byId(1L);
        assertNotNull(speaker);
    }
    
    @Test
    public void deleteSpeakerShouldDeleteItsIndividualSpeeches() {
        startFakeApplication("test/data/controllers/speakers/speaker-speech-relations.yml");

        List<Speech> speechesBefore = Speaker.find.byId(1L).speeches;
        long speakersIndividualSpeechId = speechesBefore.get(0).id;

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 1L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        Speech speechAfter = Speech.find.byId(speakersIndividualSpeechId);

        assertNull(speechAfter);
    }

    @Test
    public void deleteSpeakerShouldResetReferencesToItFromOtherSpeeches() {
        startFakeApplication("test/data/controllers/speakers/speaker-speech-relations.yml");

        Speaker speaker = Speaker.find.byId(2L);
        long sharedSpeechId = speaker.speeches.get(0).id;

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 2L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 2L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        List<Speaker> referencedSpeakersAfter = Speech.find.byId(sharedSpeechId).speakers;

        assertFalse(referencedSpeakersAfter.contains(speaker));
    }

    @Test
    public void deleteSpeakerShouldNotCorruptOtherSpeechSpeakerReferences() {
        startFakeApplication("test/data/controllers/speakers/speaker-speech-relations.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeakerUrl(1L, 1L));
        final Result result = callAction(routes.ref.SpeakersController.deleteSpeaker(1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        // Speech with id 2 should still be referencing the speaker with id 2
        Speech speech = Speech.find.byId(2L);
         assertTrue(speech.speakers.size() > 0);
    }

    private FakeRequest createAjaxRequestWithJsonBodyWithSpecifiedUser(String method, String url, JsonNode body, String login, String password) {
        return createAjaxRequestWithJsonBody(method, url, body)
                .withAuthorizationCookie(login, password);
    }

    private FakeRequest createAjaxRequestWithJsonBodyAsDefaultUser(String method, String url, JsonNode body) {
        return createAjaxRequestWithJsonBody(method, url, body)
                .withAuthorizationCookie();
    }

    private CustomFakeRequest createNonAjaxRequestWithJsonBodyAsDefaultUser(String method, String url, JsonNode body) {
        return (CustomFakeRequest) createEmptyNonAjaxRequest(method, url)
                .withAuthorizationCookie()
                .withJsonBody(body);
    }

    private FakeRequest createEmptyAjaxRequestAsDefaultUser(String method, String url) {
        return  createEmptyAjaxRequest(method, url)
                    .withAuthorizationCookie();
    }

    private FakeRequest createEmptyAjaxRequestAsSpecificUser(String method, String url, String username, String password) {
        return  createEmptyAjaxRequest(method, url)
                .withAuthorizationCookie(username, password);
    }

    private CustomFakeRequest createEmptyNonAjaxRequestAsDefaultUser(String method, String url) {
        return createEmptyNonAjaxRequest(method, url)
                .withAuthorizationCookie();
    }

    private CustomFakeRequest createAjaxRequestWithJsonBody(String method, String url, JsonNode body) {
        return (CustomFakeRequest) createEmptyAjaxRequest(method, url).withJsonBody(body);
    }

    private CustomFakeRequest createEmptyAjaxRequest(String method, String url) {
        return new CustomFakeRequest(method, url)
                .withRequestedWithHeader();
    }

    private CustomFakeRequest createEmptyNonAjaxRequest(String method, String url) {
        return new CustomFakeRequest(method, url);
    }

    private String getSpeakerUrl(long eventId, long speakerId) {
        return getSpeakersUrl(eventId) + "/" + SPEAKERS_URL;
    }

    private String getSpeakersUrl(long eventId) {
        return "events/" + eventId + "/" + SPEAKERS_URL;
    }
}
