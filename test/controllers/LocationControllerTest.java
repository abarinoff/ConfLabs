package controllers;

import models.event.Event;
import models.event.Location;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.api.mvc.HandlerRef;
import play.mvc.Result;
import play.test.FakeRequest;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class LocationControllerTest extends AbstractControllerTest {
    private static Long EVENT_WITH_LOCATION_ID = 1L;
    private static Long EVENT_WITHOUT_LOCATION_ID = 2L;
    private static Long NON_EXISTING_EVENT_ID = 10L;

    @Override
    public void setUp() {
        super.setUp();
        startFakeApplication("test/data/controllers/location/event-ready-for-location-testing.yml");
    }

    @Test
    public void addLocationForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void addLocationForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.LocationController.addLocation(NON_EXISTING_EVENT_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void addLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void addLocationForEventWithExistingLocationShouldReturnInternalServerError() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.addLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidNewLocation(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationForEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void addLocationWhenContentIsMissingShouldReturnInternalServerError() throws Exception {
        operationWhenContentIsMissingShouldReturnInternalServerError(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID));
    }

    @Test
    public void addLocationWhenNonLocationModelIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenNonLocationModelIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID));
    }

    @Test
    public void addLocationWhenInvalidLocationIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenInvalidLocationIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithInvalidNewLocation());
    }

    @Test
    public void addLocationShouldReturnCreated() throws Exception {
        addOperationShouldReturnCreated();
    }

    @Test
    public void addLocationShouldReturnLocationIdInResponse() throws Exception {
        Result result = addOperationShouldReturnCreated();

        Event event = Event.find.byId(EVENT_WITHOUT_LOCATION_ID);

        Location location = event.getLocation();
        JsonNode expectedJson = jsonNodeFromString("{\"id\":" + location.getId() + "}");
        testJsonResponse(result, expectedJson);
    }

    @Test
    public void updateLocationForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidUpdatedLocation());
    }

    @Test
    public void updateLocationForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.LocationController.updateLocation(NON_EXISTING_EVENT_ID),
                createFakeRequestWithValidUpdatedLocation());
    }

    @Test
    public void updateLocationForEventWithNonExistingLocationShouldReturnNotFound() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.updateLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidUpdatedLocation(), NOT_FOUND);
    }

    @Test
    public void updateLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidUpdatedLocation());
    }

    @Test
    public void updateLocationForEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidUpdatedLocation());
    }

    @Test
    public void updateLocationWhenContentIsMissingShouldReturnInternalServerError() throws Exception {
        operationWhenContentIsMissingShouldReturnInternalServerError(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID));
    }

    @Test
    public void updateLocationWhenNonLocationModelIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenNonLocationModelIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID));
    }

    @Test
    public void updateLocationWhenLocationIdDiffersFromLocationIdInEventShouldReturnInternalServerError() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithUpdatedLocationWithDifferentId(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateLocationWhenInvalidLocationIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenInvalidLocationIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithInvalidUpdatedLocation());
    }

    @Test
    public void updateLocationShouldReturnOk() throws Exception {
        updateOperationShouldReturnOk();
    }

    @Test
    public void updateLocationShouldReturnEmptyJsonInResponse() throws Exception {
        Result result = updateOperationShouldReturnOk();

        JsonNode expectedJson = jsonNodeFromString("{}");
        testJsonResponse(result, expectedJson);
    }

    @Test
    public void updateLocationShouldPerformUpdateCorrectly() throws Exception {
        updateOperationShouldReturnOk();

        Event updatedEvent = Event.find.byId(EVENT_WITH_LOCATION_ID);
        Location updatedLocation = updatedEvent.getLocation();

        assertThat(updatedLocation.getId()).isEqualTo(1);
        assertThat(updatedLocation.getTitle()).isEqualTo("new location title");
        assertThat(updatedLocation.getAddress()).isEqualTo("new location address");
    }

    @Test
    public void deleteLocationForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.LocationController.deleteLocation(EVENT_WITH_LOCATION_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteLocationForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.LocationController.deleteLocation(NON_EXISTING_EVENT_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteLocationForEventWithNonExistingLocationShouldReturnNotFound() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.deleteLocation(EVENT_WITHOUT_LOCATION_ID),
                createEmptyFakeRequest(), NOT_FOUND);
    }

    @Test
    public void deleteLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.LocationController.deleteLocation(EVENT_WITH_LOCATION_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteLocationForEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.LocationController.deleteLocation(EVENT_WITH_LOCATION_ID),
                createEmptyFakeRequest());
    }

    @Test
    public void deleteLocationShouldReturnOk() throws Exception {
        deleteOperationShouldReturnOk();
    }

    @Test
    public void deleteLocationShouldReturnEmptyJsonInResponse() throws Exception {
        Result result = deleteOperationShouldReturnOk();

        JsonNode expectedJson = jsonNodeFromString("{}");
        testJsonResponse(result, expectedJson);
    }

    @Test
    public void deleteLocationShouldPerformDeletionCorrectly() throws Exception {
        Event event = Event.find.byId(EVENT_WITH_LOCATION_ID);
        Long removedLocationId = event.getLocation().getId();

        deleteOperationShouldReturnOk();

        Location removedLocation = Location.find.byId(removedLocationId);
        assertEquals(null, removedLocation);

        Event updatedEvent = Event.find.byId(EVENT_WITH_LOCATION_ID);
        assertEquals(null, updatedEvent.getLocation());
    }

    private void testJsonResponse(Result result, JsonNode expectedJson) throws IOException {
        String responseBody = contentAsString(result);
        JsonNode actualJson = jsonNodeFromString(responseBody);

        assertEquals(expectedJson, actualJson);
    }

    private Result testOperation(HandlerRef operationHandler,
                                 FakeRequest request, int expectedStatus) {
        Result result = callAction(operationHandler, request);
        assertThat(status(result)).isEqualTo(expectedStatus);

        return result;
    }

    private Result testOperationWithAllHeadersAndCookies(HandlerRef operationHandler,
                                                         CustomFakeRequest requestWithBody, int expectedStatus) {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        return testOperation(operationHandler, request, expectedStatus);
    }

    private void operationForNonAuthorizedUserShouldReturnUnauthorized(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withRequestedWithHeader()
                .withContentTypeHeader();
        testOperation(operationHandler, request, UNAUTHORIZED);
    }

    public void operationForNonExistingEventShouldReturnNotFound(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        testOperationWithAllHeadersAndCookies(operationHandler, requestWithBody, NOT_FOUND);
    }

    public void operationViaNonXmlHttpRequestShouldReturnNotFound(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie()
                .withContentTypeHeader();
        testOperation(operationHandler, request, NOT_FOUND);
    }

    public void operationForEventOfAnotherUserShouldReturnInternalServerError(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie("bar@gmail.com", "123456")
                .withRequestedWithHeader()
                .withContentTypeHeader();

        testOperation(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    public void operationWhenContentIsMissingShouldReturnInternalServerError(
            HandlerRef operationHandler) throws Exception {

        CustomFakeRequest request = createEmptyFakeRequest();
        testOperationWithAllHeadersAndCookies(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    public void operationWhenNonLocationModelIsPassedShouldReturnInternalServerError(
            HandlerRef operationHandler) throws Exception {

        CustomFakeRequest request = createFakeRequestWithUnknownModel();
        testOperationWithAllHeadersAndCookies(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    public void operationWhenInvalidLocationIsPassedShouldReturnInternalServerError(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        testOperationWithAllHeadersAndCookies(operationHandler, requestWithBody, INTERNAL_SERVER_ERROR);
    }

    private Result addOperationShouldReturnCreated() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation(), CREATED);
    }

    private Result updateOperationShouldReturnOk() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidUpdatedLocation(), OK);
    }

    private Result deleteOperationShouldReturnOk() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.LocationController.deleteLocation(EVENT_WITH_LOCATION_ID),
                createEmptyFakeRequest(), OK);
    }

    private CustomFakeRequest createFakeRequestWithValidNewLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/valid-new-location.json");
    }

    private CustomFakeRequest createFakeRequestWithInvalidNewLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/invalid-new-location.json");
    }

    private CustomFakeRequest createFakeRequestWithValidUpdatedLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/valid-updated-location.json");
    }

    private CustomFakeRequest createFakeRequestWithUpdatedLocationWithDifferentId() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/valid-updated-location-with-different-id.json");
    }

    private CustomFakeRequest createFakeRequestWithInvalidUpdatedLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/invalid-updated-location.json");
    }

    private CustomFakeRequest createEmptyFakeRequest() {
        return new CustomFakeRequest();
    }
}
