package controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class EventControllerTest extends AbstractControllerTest {

    @Test
    public void getEventsForAuthorizedUserShouldReturnOkAndJsonContentType() {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo(AbstractController.CONTENT_TYPE_JSON);
    }

    @Test
    public void getEventsForNonAuthorizedUserShouldReturnUnauthorized() {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Result result = callAction(routes.ref.EventController.getEvents());

        assertThat(status(result)).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void getEventsForUserWithNoEventsShouldReturnEmptyJson() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        JsonNode expectedEventsJson = jsonNodeFromString("[]");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");
        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = jsonNodeFromString(receivedEvents);

        assertEquals(expectedEventsJson, receivedEventsJson);
    }

    @Test
    public void getEventsForUserWithEventsShouldReturnValidJsonResponse() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        JsonNode preparedEventsJson = jsonNodeFromFile(new File("conf/test/data/controllers/events/events-for-user-with-id-1-short-view.json"));

        Http.Cookie playSession = getAuthorizationCookie();
        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = jsonNodeFromString(receivedEvents);

        assertEquals(preparedEventsJson, receivedEventsJson);
    }

    @Test
    public void createNewEventWithJsonForDifferentModelShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode jsonNode = jsonNodeFromString("{\"fooKey\" : \"fooValue\", \"barKey\" : \"barValue\"}");

        Result result = callAction(routes.ref.EventController.createEvent(),
                fakeRequest()
                    .withCookies(playSession)
                    .withHeader(Http.HeaderNames.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                    .withJsonBody(jsonNode)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createNewEventWithCorrectPostDataShouldReturnCreated() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Result result = sendPostWithJsonDataAsAuthorizedUser("conf/test/data/controllers/events/event-with-location.json");

        assertThat(status(result)).isEqualTo(CREATED);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = jsonNodeFromString(responseBody);

        int nodeSize = receivedJson.size();
        assertEquals(2, nodeSize);

        long eventId = receivedJson.path("id").asLong();
        assertTrue(0 < eventId);
    }

    @Test
    public void createMultipleEventsShouldReturnServerError() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Result result = sendPostWithJsonDataAsAuthorizedUser("conf/test/data/controllers/events/array-of-events.json");

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getEventWithNonAjaxRequestShouldReturnNotFound() {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(1L),
                fakeRequest()
                        .withCookies(playSession)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getEventWithAjaxRequestShouldSucceed() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(1L),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(AbstractController.REQUESTED_WITH_HEADER, AbstractController.REQUEST_TYPE_XMLHTTP)
        );

        assertThat(status(result)).isEqualTo(OK);

        JsonNode eventsArrayNode = jsonNodeFromFile(new File("conf/test/data/controllers/events/events-for-user-with-id-1.json"));
        JsonNode expectedJson = eventsArrayNode.get(0);

        String responseBody = contentAsString(result);
        JsonNode receivedJson = jsonNodeFromString(responseBody);

        assertEquals(expectedJson, receivedJson);
    }

    @Test
    public void getEventWithNonexistentIdShouldReturnNotFound() throws IOException {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(100L),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(AbstractController.REQUESTED_WITH_HEADER, AbstractController.REQUEST_TYPE_XMLHTTP)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getEventBelongingToAnotherUserShouldReturnServerError() {
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");

        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(3L),
                fakeRequest()
                    .withCookies(playSession)
                    .withHeader(AbstractController.REQUESTED_WITH_HEADER, AbstractController.REQUEST_TYPE_XMLHTTP)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    private Result sendPostWithJsonDataAsAuthorizedUser(String jsonDataFile) throws IOException {
        Http.Cookie playSession = getAuthorizationCookie();

        JsonNode newEventJson = jsonNodeFromFile(new File(jsonDataFile));

        final Result result = callAction(routes.ref.EventController.createEvent(),
                fakeRequest()
                        .withCookies(playSession)
                        .withHeader(Http.HeaderNames.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON)
                        .withJsonBody(newEventJson)
        );

        return result;
    }
}
