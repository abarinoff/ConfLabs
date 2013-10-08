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

    @Test
    public void addLocationForNonAuthorizedUserShouldReturnUnauthorized() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void addLocationForNonExistingEventShouldReturnNotFound() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(NON_EXISTING_EVENT_ID), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void addLocationViaNonXmlHttpRequestShouldReturnNotFound() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    @Test
    public void addLocationForEventWithExistingLocationShouldReturnInternalServerError() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITH_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationForEventOfAnotherUserShouldReturnInternalServerError() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie("bar@gmail.com", "123456")
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationWhenContentIsMissingShouldReturnInternalServerError() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithMissingLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationWhenNonLocationModelIsPassedShouldReturnInternalServerError() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithUnknownModel()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationWhenInvalidLocationIsPassedShouldReturnInternalServerError() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithInvalidLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void addLocationShouldReturnCreated() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        assertThat(status(result)).isEqualTo(CREATED);
    }

    @Test
    public void addLocationShouldReturnLocationIdInResponse() throws Exception {
        startFakeApplicationForAddOperationTesting();

        CustomFakeRequest request = createFakeRequestWithValidLocation()
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        Result result = callAction(routes.ref.LocationController.addLocation(EVENT_WITHOUT_LOCATION_ID), request);

        // Note: this is required for action to be executed
        assertThat(status(result)).isEqualTo(CREATED);

        Event event = Event.find.byId(EVENT_WITHOUT_LOCATION_ID);

        Location location = event.location;
        JsonNode expectedJson = jsonNodeFromString("{\"id\":" + location.id + "}");

        String responseBody = contentAsString(result);
        JsonNode actualJson = jsonNodeFromString(responseBody);

        assertEquals(expectedJson, actualJson);
    }

    private void startFakeApplicationForAddOperationTesting() {
        startFakeApplication("test/data/controllers/event-ready-for-location-addition.yml");
    }

    private CustomFakeRequest createFakeRequestWithValidLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/valid-location.json");
    }

    private CustomFakeRequest createFakeRequestWithInvalidLocation() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/invalid-location.json");
    }

    private CustomFakeRequest createFakeRequestWithMissingLocation() {
        return new CustomFakeRequest();
    }

}
