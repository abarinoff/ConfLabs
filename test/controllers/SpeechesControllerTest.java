package controllers;

import junit.framework.Assert;
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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.fest.assertions.Assertions.assertThat;

import static org.junit.Assert.*;
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
    public void updateSpeechWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createNonAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeechWithAjaxRequestShouldReturnOkAndEmptyJson() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(OK);
    }

    @Test
    public void updateSpeechWithInvalidJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromString("{\"foo\":\"bar\", \"bar\":\"foo\"}");

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeechWithEmptyJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromString("{}");

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeechWithInconsistentIdsInUrlAndJsonShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 2L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 2L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateSpeechForSpeakerBelongingToDifferentEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 3L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 3L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeechBelongingToDifferentEventShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(2L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(2L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateSpeechForEventBelongingToDifferentUserShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyWithSpecifiedUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody, "bar@gmail.com", "123456");
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void assignExistingSpeechToSpeakerShouldCreateReferenceToSpeaker() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        Speech speechBefore = Speech.find.byId(1L);
        Speaker speaker = Speaker.find.byId(2L);
        assertFalse(speechBefore.speakers.contains(speaker));

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 2L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 2L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(OK);

        Speech speechAfter = Speech.find.byId(1L);
        assertTrue(speechAfter.speakers.contains(speaker));
    }

    @Test
    public void assignExistingSpeechToSpeakerShouldNotAffectExistingReferencesToSpeakers() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        List<Speaker> speakersBefore = Speech.find.byId(1L).speakers;
        speakersBefore.size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 2L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 2L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        List<Speaker> speakersAfter = Speech.find.byId(1L).speakers;
        assertTrue(speakersAfter.containsAll(speakersBefore));
    }

    @Test
    public void assignExistingSpeechToSpeakerWithThisSpeechAlreadyAssignedShouldNotCreateExtraReference() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        JsonNode requestBody = jsonNodeFromFile(new File(DATA_FILE_LOCATION + "speech-with-id-1.json"));

        int sizeBefore = Speech.find.byId(1L).speakers.size();

        FakeRequest fakeRequest = createAjaxRequestWithJsonBodyAsDefaultUser(Helpers.PUT, getSpeechUrl(1L, 1L, 1L), requestBody);
        Result result = callAction(routes.ref.SpeechesController.updateSpeech(1L, 1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        int sizeAfter = Speech.find.byId(1L).speakers.size();

        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    public void deleteSpeechWithNonAjaxRequestShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyNonAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 1L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeechWithAjaxRequestShouldReturnEmptyJson() throws IOException {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 1L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 1L), fakeRequest);

        JsonNode responseJson = jsonNodeFromString(contentAsString(result));
        JsonNode expectedJson = jsonNodeFromString("{}");

        assertThat(status(result)).isEqualTo(OK);
        assertEquals(responseJson, expectedJson);
    }

    @Test
    public void deleteSpeechThatAssignedToASingleSpeakerShouldRemoveSpeech() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(1L);
        assertNotNull(speechBefore);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 1L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        Speech speechAfter = Speech.find.byId(1L);
        assertNull(speechAfter);
    }

    @Test
    public void deleteSpeechThatAssignedToMoreThanOneSpeakerShouldNotDeleteSpeechFromStorage() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(2L);
        assertNotNull(speechBefore);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 2L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 2L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        Speech speechAfter = Speech.find.byId(2L);
        assertNotNull(speechAfter);
    }

    @Test
    public void deleteSpeechThatAssignedToMoreThanOneSpeakerShouldResetReferenceToCurrentSpeaker() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(2L);
        List<Speaker> speakersBefore = speechBefore.speakers;
        assertEquals(speakersBefore.size(), 2);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 2L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 2L), fakeRequest);
        assertThat(status(result)).isEqualTo(OK);

        Speech speechAfter = Speech.find.byId(2L);
        List<Speaker> speakersAfter = speechAfter.speakers;

        assertEquals(speakersAfter.size(), 1);
        assertFalse(speakersAfter.contains(speakersBefore.get(0)));
    }

    @Test
    public void deleteSpeechWithNonExistentIdShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 100L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 100L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeechThatDoesNotBelongToAGivenSpeakerShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 2L, 1L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 2L, 1L), fakeRequest);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeechThatDoesNotBelongToAGivenSpeakerShouldNotDeleteSpeech() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(1L);
        assertNotNull(speechBefore);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 2L, 1L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 2L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(NOT_FOUND);

        Speech speechAfter = Speech.find.byId(1L);

        assertEquals(speechBefore, speechAfter);
    }

    @Test
    public void deleteSpeechThatDoesNotBelongToAGivenEventShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 3L, 3L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 3L, 3L), fakeRequest);
        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteSpeechThatDoesNotBelongToAGivenEventShouldNotDeleteSpeech() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(3L);
        assertNotNull(speechBefore);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsDefaultUser(Helpers.DELETE, getSpeechUrl(1L, 3L, 3L));
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 3L, 3L), fakeRequest);
        assertThat(status(result)).isEqualTo(NOT_FOUND);

        Speech speechAfter = Speech.find.byId(3L);
        assertEquals(speechBefore, speechAfter);
    }

    @Test
    public void deleteSpeechForEventThatDoesNotBelongToTheAuthorizedUserShouldReturnServerError() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        FakeRequest fakeRequest = createEmptyAjaxRequestAsSpecificUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 1L), "bar@gmail.com", "123456");
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deleteSpeechForEventThatDoesNotBelongToTheAuthorizedUserShouldNotDeleteSpeech() {
        startFakeApplication("test/data/controllers/speeches/event-with-all-entities.yml");

        Speech speechBefore = Speech.find.byId(1L);
        assertNotNull(speechBefore);

        FakeRequest fakeRequest = createEmptyAjaxRequestAsSpecificUser(Helpers.DELETE, getSpeechUrl(1L, 1L, 1L), "bar@gmail.com", "123456");
        Result result = callAction(routes.ref.SpeechesController.unsetSpeech(1L, 1L, 1L), fakeRequest);
        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        Speech speechAfter = Speech.find.byId(1L);

        assertEquals(speechBefore, speechAfter);
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
