package controllers;

import models.event.Speaker;
import models.event.Speech;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static play.mvc.Http.Status.*;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.status;

public class SpeechesControllerTest extends AbstractControllerTest {

    private static final String DATA_FILE_LOCATION = "conf/test/data/controllers/speeches/";

    @Test
    public void createSpeechWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        FakeRequest fakeRequest = createNonAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeechWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromString("{}");

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeechWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromString("{\"foo\":\"bar\"}");

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeechWithJsonContainingIdShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeechWithJsonContainingIdShouldNotCreateNewSpeech() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        int countBefore = Speech.find.all().size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
        int countAfter = Speech.find.all().size();

        assertEquals(countBefore, countAfter);
    }

    @Test
    public void createSpeechWithAjaxRequestShouldReturnOkAndJsonWithSpeechId() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        List<Speech> speechesBefore = Speaker.find.byId(1L).speeches;
        int sizeBefore = speechesBefore.size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(CREATED);

        List<Speech> speechesAfter = Speaker.find.byId(1L).speeches;
        int sizeAfter = speechesAfter.size();

        assertEquals(sizeBefore + 1, sizeAfter);
        long speechId = speechesAfter.get(speechesAfter.size() - 1).id;

        JsonNode expectedJson = jsonNodeFromString("{\"id\": " + speechId + " }");
        JsonNode receivedJson = jsonNodeFromString(contentAsString(result));

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void createSpeechShouldBindNewSpeechToSpeaker() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        Speaker speaker = Speaker.find.byId(1L);
        List<Speech> speechesBefore = speaker.speeches;
        int sizeBefore = speechesBefore.size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(CREATED);

        List<Speech> speechesAfter = Speaker.find.byId(1L).speeches;
        int sizeAfter = speechesAfter.size();

        assertEquals(sizeBefore + 1, sizeAfter);
        long speechId = speechesAfter.get(speechesAfter.size() - 1).id;

        Speech savedSpeech = Speech.find.byId(speechId);

        assertTrue(savedSpeech.speakers.contains(speaker));
    }

    @Test
    public void createSpeechForNonExistentSpeakerShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 100L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 100L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeechForNonExistentSpeakerShouldNotCreateSpeech() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        int countBefore = Speech.find.all().size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 100L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 100L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        int countAfter = Speech.find.all().size();

        assertEquals(countBefore, countAfter);
    }

    @Test
    public void createSpeechForSpeakerBelongingToDifferentEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 3L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 3L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeechForSpeakerBelongingToDifferentEventShouldNotCreateNewSpeech() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        int countBefore = Speech.find.all().size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(1L, 3L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 3L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
        int countAfter = Speech.find.all().size();

        assertEquals(countBefore, countAfter);
    }

    @Test
    public void createSpeechForNonExistentEventButValidSpeakerShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(100L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(100L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createSpeechForNonExistentEventButValidSpeakerShouldNotCreateNewSpeech() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        int countBefore = Speech.find.all().size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.POST, getSpeechesUrl(100L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.createSpeech(100L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
        int countAfter = Speech.find.all().size();

        assertEquals(countBefore, countAfter);
    }

    @Test
    public void createSpeechForEventBelongingToDifferentUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyWithSpecifiedUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody, "bar@gmail.com", "123456");
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createSpeechForEventBelongingToDifferentUserShouldNotCreateNewSpeech() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "new-speech.json"));

        int countBefore = Speech.find.all().size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyWithSpecifiedUser(Helpers.POST, getSpeechesUrl(1L, 1L), requestBody, "bar@gmail.com", "123456");
        Result result = callAction(routes.ref.SpeechesController.createSpeech(1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
        int countAfter = Speech.find.all().size();

        assertEquals(countBefore, countAfter);
    }

    @Test
    public void deleteSpeechThatAssignedToMoreThanOneSpeakerShouldNotDeleteSpeechFromStorage() {

    }

    @Test
    public void deleteSpeechThatAssignedToMoreThanOneSpeakerShouldResetReferenceToCurrentSpeaker() {

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

    private String getSpeechesUrl(Long eventId, Long speakerId) {
        return EVENTS_URL + "/" + eventId + "/" + SPEAKERS_URL + "/" + speakerId + "/" + SPEECHES_URL;
    }

    private String getSpeechUrl(Long eventId, Long speakerId, Long speechId) {
        return this.getSpeechesUrl(eventId, speakerId) + "/" + speechId;
    }
}
