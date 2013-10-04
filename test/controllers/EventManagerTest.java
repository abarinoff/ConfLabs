package controllers;

import com.avaje.ebean.Ebean;
import com.feth.play.module.pa.PlayAuthenticate;
import com.google.common.collect.ImmutableMap;

import models.authentication.SecurityRole;

import models.event.Event;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import play.GlobalSettings;

import play.libs.Yaml;

import play.mvc.HandlerRef;
import play.mvc.Http;
import play.mvc.Result;

import play.test.WithApplication;

import resolvers.DefaultResolver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

import static org.junit.Assert.*;

public class EventManagerTest extends WithApplication {
    private static final String CONTENT_TYPE_JSON = "application/json";

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), new CustomGlobalSettings()));

        mapper = new ObjectMapper();
    }

    @Test
    public void getEventsForAuthorizedUserShouldSucceed() {
        Http.Cookie playSession = getAuthorizationCookie();

        Result result = callAction(routes.ref.EventManager.getEvents(), fakeRequest().withCookies(playSession));

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo(CONTENT_TYPE_JSON);
    }

    @Test
    public void getEventsForNonAuthorizedUserShouldFail() {
        Result result = callAction(routes.ref.EventManager.getEvents());

        assertThat(status(result)).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void getEventsForUserWithNoEventsShouldReturnEmptyJson() throws IOException {
        JsonNode expectedEventsJson = mapper.readTree("[]");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");
        Result result = callAction(routes.ref.EventManager.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = mapper.readTree(receivedEvents);

        assertEquals(expectedEventsJson, receivedEventsJson);
    }

    @Test
    public void getEventsForUserWithEventsShouldReturnValidJsonResponse() throws IOException {
        JsonNode preparedEventsJson = mapper.readTree(new File("conf/test/json/data/events-for-user-with-id-1-short-view.json"));

        Http.Cookie playSession = getAuthorizationCookie();
        Result result = callAction(routes.ref.EventManager.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = mapper.readTree(receivedEvents);

        assertEquals(preparedEventsJson, receivedEventsJson);
    }

    @Test
    public void addNewEventWithJsonForDifferentModelShouldFail() throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode jsonNode = mapper.readTree("{\"fooKey\" : \"fooValue\", \"barKey\" : \"barValue\"}");

        Result result = callAction(routes.ref.EventManager.addEvent(),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(Http.HeaderNames.CONTENT_TYPE, CONTENT_TYPE_JSON)
                        .withJsonBody(jsonNode)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        JsonNode expectedJson = mapper.readTree("{\"error\":true}");

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void addNewEventWithCorrectPostDataShouldSucceed() throws IOException {
        Result result = sendPostWithJsonDataAsAuthorizedUser("conf/test/json/data/event-with-location.json");

        assertThat(status(result)).isEqualTo(OK);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        int nodeSize = receivedJson.size();
        assertEquals(2, nodeSize);

        long eventId = receivedJson.path("id").asLong();
        assertTrue(0 < eventId);
    }

    @Test
    public void addMultipleEventsShouldFail() throws IOException {
        Result result = sendPostWithJsonDataAsAuthorizedUser("conf/test/json/data/array-of-events.json");

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        JsonNode expectedJson = mapper.readTree("{\"error\":true}");

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void getEventWithNonAjaxRequestShouldFail() {
        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventManager.getEventById(1L),
                fakeRequest()
                        .withCookies(playSession)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getEventWithAjaxRequestShouldSucceed() throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventManager.getEventById(1L),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader("X-Requested-With", "XMLHttpRequest")
        );

        assertThat(status(result)).isEqualTo(OK);

        JsonNode eventsArrayNode = mapper.readTree(new File("conf/test/json/data/events-for-user-with-id-1.json"));
        JsonNode expectedJson = eventsArrayNode.get(0);
        ((ObjectNode) expectedJson).remove("type");

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void getEventWithNonexistentIdShouldReturnNotFoundAndErrorMessage() throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventManager.getEventById(100L),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader("X-Requested-With", "XMLHttpRequest")
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        JsonNode expectedJson = mapper.readTree("{\"error\":\"Event with a given id was not found\"}");

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void updateEventWithNonAjaxRequestShouldReturnNotFound() throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode updateEventJson = mapper.readTree(new File("conf/test/json/data/event-with-stages.json"));

        final Result result = callAction(routes.ref.EventManager.updateEvent(1L),
                fakeRequest()
                    .withCookies(playSession)
                    .withHeader(Http.HeaderNames.CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .withJsonBody(updateEventJson)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void updateEventWithAjaxRequestShouldSucceed() throws IOException {
        HandlerRef handlerRef = routes.ref.EventManager.updateEvent(1L);
        JsonNode updateEventJson = mapper.readTree(new File("conf/test/json/data/event-with-stages.json"));

        final Result result = callAjaxActionWithJsonBody(handlerRef, updateEventJson);

        assertThat(status(result)).isEqualTo(OK);

        String expectedHeader = header(Http.HeaderNames.CONTENT_TYPE, result);
        assertEquals(expectedHeader, CONTENT_TYPE_JSON);
    }

/*    @Test
    public void updateEventWithImproperJsonShouldReturnServerError() throws IOException {
        HandlerRef handlerRef = routes.ref.EventManager.updateEvent(1L);
        JsonNode updateEventJson = mapper.readTree("{\"fooKey\":\"fooValue\", \"barKey\":\"barValue\"}");

        final Result result = callAjaxActionWithJsonBody(handlerRef, updateEventJson);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        JsonNode receivedJson = mapper.readTree(contentAsString(result));
        JsonNode expectedJson = mapper.readTree("{\"error\": true}");

        assertEquals(receivedJson, expectedJson);
    }

    @Test
    public void updateEventWithNonExistentIdShouldReturnNotFound() throws IOException {
        HandlerRef handlerRef = routes.ref.EventManager.updateEvent(100L);
        JsonNode updateEventJson = mapper.readTree(new File("conf/test/json/data/event-with-stages.json"));

        final Result result = callAjaxActionWithJsonBody(handlerRef, updateEventJson);

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        JsonNode expectedJson = mapper.readTree("{\"error\": true}");

        String responseBody = contentAsString(result);
        JsonNode receivedJson = mapper.readTree(responseBody);

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void updateEventWithInconsistentIdsShouldReturnServerError() throws IOException {
        HandlerRef handlerRef = routes.ref.EventManager.updateEvent(1L);
        JsonNode updateEventJson = mapper.readTree(new File("conf/tests/json/data/event-with-nonexistent-id.json"));

        final Result result = callAjaxActionWithJsonBody(handlerRef, updateEventJson);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);

        JsonNode expectedJson = mapper.readTree("{\"error\": true}");
        JsonNode responseJson = mapper.readTree(contentAsString(result));

        assertEquals(expectedJson, responseJson);
    }

    // @todo do we need this test method? [we already have tests that check persistence of the Event object]
    @Test
    public void updateEventWithExtraStageShouldCreateNewStage() throws IOException {
        HandlerRef handlerRef = routes.ref.EventManager.updateEvent(1L);
        JsonNode updateEventJson = mapper.readTree(new File("conf/test/json/data/event-with-stages.json"));

        final Result result = callAjaxActionWithJsonBody(handlerRef, updateEventJson);

        // @todo Is it correct?
        Event expectedEvent = mapper.readValue(updateEventJson, Event.class);
        Event updatedEvent = Event.find.byId(1L);

        assertEquals(expectedEvent, updatedEvent);
    }

    // @todo is there a need for this method?
    @Test
    public void updateEventWithUpdatedExistingStageShouldUpdateStage() {

    }*/


    private Result sendPostWithJsonDataAsAuthorizedUser(String jsonDataFile) throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode newEventJson = mapper.readTree(new File(jsonDataFile));

        final Result result = callAction(routes.ref.EventManager.addEvent(),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(Http.HeaderNames.CONTENT_TYPE, CONTENT_TYPE_JSON)
                        .withJsonBody(newEventJson)
        );

        return result;
    }

    private Result callAjaxActionWithJsonBody(HandlerRef handlerRef, JsonNode json) {
        Http.Cookie playSession = getAuthorizationCookie();

        return callAction(handlerRef,
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(Http.HeaderNames.CONTENT_TYPE, CONTENT_TYPE_JSON)
                        .withHeader("X-Requested-With", "XMLHttpRequest")
                        .withJsonBody(json)
        );
    }


    private Http.Cookie getAuthorizationCookie(String ... credentials) {
        String username = credentials.length > 0 ? credentials[0] : "foo@bar.com";
        String password = credentials.length > 1 ? credentials[1] : "123456";

        Map<String, String> data = ImmutableMap.of(
                "email", username,
                "password", password);
        Result result = callAction(
            routes.ref.Application.doLogin(), fakeRequest().withFormUrlEncodedBody(data)
        );

        return cookie("PLAY_SESSION", result);
    }

    private class CustomGlobalSettings extends GlobalSettings {
        public void onStart(play.Application app) {
            super.onStart(app);

            PlayAuthenticate.setResolver(new DefaultResolver());

            initializeDatabase();
        }

        private void initializeDatabase() {
            if (SecurityRole.find.findRowCount() == 0) {
                for (final String roleName : Arrays
                        .asList(controllers.Application.USER_ROLE)) {
                    final SecurityRole role = new SecurityRole();
                    role.roleName = roleName;
                    role.save();
                }
            }

            Map<String, List> all = (Map<String, List>) Yaml.load("test/models/data/event-with-all-entities.yml");
            Ebean.save(all.get("users"));
        }
    }
}
