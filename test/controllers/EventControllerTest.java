package controllers;

import models.event.Event;
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
    private static Long EXISTING_EVENT_ID = 1L;
    private static Long NON_EXISTING_EVENT_ID = 10L;

    @Override
    public void setUp() {
        super.setUp();
        startFakeApplication("test/data/controllers/events/event-with-all-entities.yml");
    }

    @Test
    public void getEventsForAuthorizedUserShouldReturnOkAndJsonContentType() {
        Http.Cookie playSession = getAuthorizationCookie();

        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo(AbstractController.CONTENT_TYPE_JSON);
    }

    @Test
    public void getEventsForNonAuthorizedUserShouldReturnUnauthorized() {
        Result result = callAction(routes.ref.EventController.getEvents());

        assertThat(status(result)).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void getEventsForUserWithNoEventsShouldReturnEmptyJson() throws IOException {
        JsonNode expectedEventsJson = jsonNodeFromString("[]");

        Http.Cookie playSession = getAuthorizationCookie("bar@gmail.com", "123456");
        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = jsonNodeFromString(receivedEvents);

        assertEquals(expectedEventsJson, receivedEventsJson);
    }

    @Test
    public void getEventsForUserWithEventsShouldReturnValidJsonResponse() throws IOException {
        JsonNode preparedEventsJson = jsonNodeFromFile(new File("conf/test/data/controllers/events/events-for-user-with-id-1-short-view.json"));

        Http.Cookie playSession = getAuthorizationCookie();
        Result result = callAction(routes.ref.EventController.getEvents(), fakeRequest().withCookies(playSession));

        String receivedEvents = contentAsString(result);
        JsonNode receivedEventsJson = jsonNodeFromString(receivedEvents);

        assertEquals(preparedEventsJson, receivedEventsJson);
    }

    @Test
    public void createNewEventWithJsonForDifferentModelShouldReturnServerError() throws IOException {
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
        testOperationWithAllHeadersAndCookies(routes.ref.EventController.createEvent(),
                createFakeRequestWithMultipleEvents(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getEventWithNonAjaxRequestShouldReturnNotFound() {
        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(1L),
                fakeRequest()
                        .withCookies(playSession)
        );

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getEventWithAjaxRequestShouldSucceed() throws IOException {
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
        Http.Cookie playSession = getAuthorizationCookie();

        final Result result = callAction(routes.ref.EventController.getEventById(3L),
                fakeRequest()
                    .withCookies(playSession)
                    .withHeader(AbstractController.REQUESTED_WITH_HEADER, AbstractController.REQUEST_TYPE_XMLHTTP)
        );

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateEventForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedEvent());
    }

    @Test
    public void updateEventForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.EventController.updateEvent(NON_EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedEvent());
    }

    @Test
    public void updateLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedEvent());
    }

    @Test
    public void updateEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedEvent());
    }

    @Test
    public void updateEventWhenContentIsMissingShouldReturnInternalServerError() throws Exception {
        operationWhenContentIsMissingShouldReturnInternalServerError(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID));
    }

    @Test
    public void updateEventWhenNonEventModelIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenUnknownModelIsPassedShouldReturnInternalServerError(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID));
    }

    @Test
    public void updateEventWhenEventIdDiffersShouldReturnInternalServerError() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithUpdatedEventWithDifferentId(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateEventWhenInvalidEventIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenInvalidModelIsPassedShouldReturnInternalServerError(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithInvalidUpdatedEvent());
    }

    @Test
    public void updateEventShouldReturnOk() throws Exception {
        updateOperationShouldReturnOk();
    }

    @Test
    public void updateEventShouldReturnEmptyJsonInResponse() throws Exception {
        Result result = updateOperationShouldReturnOk();

        JsonNode expectedJson = jsonNodeFromString("{}");
        testJsonResponse(result, expectedJson);
    }

    @Test
    public void updateEventShouldPerformUpdateCorrectly() throws Exception {
        updateOperationShouldReturnOk();

        Event updatedEvent = Event.find.byId(EXISTING_EVENT_ID);

        assertThat(updatedEvent.id).isEqualTo(1);
        assertThat(updatedEvent.title).isEqualTo("updated title");
        assertThat(updatedEvent.description).isEqualTo("updated description");
    }

    @Test
    public void deleteEventForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.EventController.deleteEvent(EXISTING_EVENT_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteEventForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.EventController.deleteEvent(NON_EXISTING_EVENT_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteEventViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.EventController.deleteEvent(EXISTING_EVENT_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.EventController.deleteEvent(EXISTING_EVENT_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteEventShouldReturnOk() throws Exception {
        deleteOperationShouldReturnOk();
    }

    @Test
    public void deleteEventShouldReturnEmptyJsonInResponse() throws Exception {
        Result result = deleteOperationShouldReturnOk();

        JsonNode expectedJson = jsonNodeFromString("{}");
        testJsonResponse(result, expectedJson);
    }

    @Test
    public void deleteEventShouldPerformDeletionCorrectly() throws Exception {
        Event event = Event.find.byId(EXISTING_EVENT_ID);
        Long removedEventId = event.id;

        deleteOperationShouldReturnOk();

        Event removedEvent = Event.find.byId(removedEventId);
        assertEquals(null, removedEvent);
    }

    private Result updateOperationShouldReturnOk() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.EventController.updateEvent(EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedEvent(), OK);
    }

    private Result deleteOperationShouldReturnOk() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.EventController.deleteEvent(EXISTING_EVENT_ID),
                createEmptyFakeRequest(), OK);
    }

    private CustomFakeRequest createFakeRequestWithMultipleEvents() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/events/array-of-events.json");
    }

    private CustomFakeRequest createFakeRequestWithValidUpdatedEvent() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/events/valid-updated-event.json");
    }

    private CustomFakeRequest createFakeRequestWithUpdatedEventWithDifferentId() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/events/valid-updated-event-with-different-id.json");
    }

    private CustomFakeRequest createFakeRequestWithInvalidUpdatedEvent() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/events/invalid-updated-event.json");
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
