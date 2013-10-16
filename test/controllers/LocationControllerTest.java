package controllers;

import models.event.Event;
import models.event.Location;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import play.mvc.Result;

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
    public void createLocationForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        operationForNonAuthorizedUserShouldReturnUnauthorized(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void createLocationForNonExistingEventShouldReturnNotFound() throws Exception {
        operationForNonExistingEventShouldReturnNotFound(
                routes.ref.LocationController.createLocation(NON_EXISTING_EVENT_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void createLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        operationViaNonXmlHttpRequestShouldReturnNotFound(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void createLocationForEventWithExistingLocationShouldReturnInternalServerError() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.createLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithValidNewLocation(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createLocationForEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        operationForEventOfAnotherUserShouldReturnInternalServerError(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithValidNewLocation());
    }

    @Test
    public void createLocationWhenContentIsMissingShouldReturnInternalServerError() throws Exception {
        operationWhenContentIsMissingShouldReturnInternalServerError(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID));
    }

    @Test
    public void createLocationWhenNonLocationModelIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenUnknownModelIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID));
    }

    @Test
    public void createLocationWhenIdIsSpecifiedInRequestDataShouldReturnServerError() throws IOException {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithNewLocationWithId(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createLocationWhenInvalidLocationIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenInvalidModelIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
                createFakeRequestWithInvalidNewLocation());
    }

    @Test
    public void createLocationShouldReturnCreated() throws Exception {
        createOperationShouldReturnCreated();
    }

    @Test
    public void createLocationShouldReturnLocationIdInResponse() throws Exception {
        Result result = createOperationShouldReturnCreated();

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
        operationWhenUnknownModelIsPassedShouldReturnInternalServerError(
                routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID));
    }

    @Test
    public void updateLocationWhenLocationIdDiffersFromLocationIdInEventShouldReturnInternalServerError() throws Exception {
        testOperationWithAllHeadersAndCookies(routes.ref.LocationController.updateLocation(EVENT_WITH_LOCATION_ID),
                createFakeRequestWithUpdatedLocationWithDifferentId(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void updateLocationWhenInvalidLocationIsPassedShouldReturnInternalServerError() throws Exception {
        operationWhenInvalidModelIsPassedShouldReturnInternalServerError(
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

    private Result createOperationShouldReturnCreated() throws IOException {
        return testOperationWithAllHeadersAndCookies(
                routes.ref.LocationController.createLocation(EVENT_WITHOUT_LOCATION_ID),
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

    private CustomFakeRequest createFakeRequestWithNewLocationWithId() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/location/new-location-with-id.json");
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

}
