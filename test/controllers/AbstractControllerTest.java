package controllers;

import com.avaje.ebean.Ebean;
import com.feth.play.module.pa.PlayAuthenticate;
import com.google.common.collect.ImmutableMap;
import models.authentication.SecurityRole;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import play.GlobalSettings;
import play.api.mvc.HandlerRef;
import play.libs.Yaml;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import resolvers.DefaultResolver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class AbstractControllerTest extends WithApplication {

    protected ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    protected void startFakeApplication(String initialDataFile) {
        start(fakeApplication(inMemoryDatabase(), new ControllerTestGlobalSettings(initialDataFile)));
    }

    protected JsonNode jsonNodeFromString(String content) throws IOException {
        return objectMapper.readTree(content);
    }

    protected JsonNode jsonNodeFromFile(File content) throws IOException {
        return objectMapper.readTree(content);
    }

    protected Http.Cookie getAuthorizationCookie() {
        return getAuthorizationCookie("foo@bar.com", "123456");
    }

    protected Http.Cookie getAuthorizationCookie(String userName, String password) {
        Map<String, String> data = ImmutableMap.of(
                "email", userName,
                "password", password);

        Result result = callAction(routes.ref.Application.doLogin(), fakeRequest().withFormUrlEncodedBody(data));

        return cookie("PLAY_SESSION", result);
    }

    protected CustomFakeRequest createFakeRequestWithJsonBody(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        JsonNode jsonNode = jsonNodeFromFile(jsonFile);

        CustomFakeRequest request = new CustomFakeRequest();
        request.withJsonBody(jsonNode);

        return request;
    }

    protected CustomFakeRequest createFakeRequestWithUnknownModel() throws IOException {
        return createFakeRequestWithJsonBody("conf/test/data/controllers/unknown-model.json");
    }

    protected Result testOperation(HandlerRef operationHandler,
                                   FakeRequest request, int expectedStatus) {
        Result result = callAction(operationHandler, request);
        assertThat(status(result)).isEqualTo(expectedStatus);

        return result;
    }

    protected Result testOperationWithAllHeadersAndCookies(HandlerRef operationHandler,
                                                           CustomFakeRequest requestWithBody, int expectedStatus) {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie()
                .withRequestedWithHeader()
                .withContentTypeHeader();

        return testOperation(operationHandler, request, expectedStatus);
    }

    protected void testJsonResponse(Result result, JsonNode expectedJson) throws IOException {
        String responseBody = contentAsString(result);
        JsonNode actualJson = jsonNodeFromString(responseBody);

        assertEquals(expectedJson, actualJson);
    }

    protected void operationForNonAuthorizedUserShouldReturnUnauthorized(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withRequestedWithHeader()
                .withContentTypeHeader();
        testOperation(operationHandler, request, UNAUTHORIZED);
    }

    protected void operationForNonExistingEventShouldReturnNotFound(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        testOperationWithAllHeadersAndCookies(operationHandler, requestWithBody, NOT_FOUND);
    }

    protected void operationViaNonXmlHttpRequestShouldReturnNotFound(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie()
                .withContentTypeHeader();
        testOperation(operationHandler, request, NOT_FOUND);
    }

    protected void operationForEventOfAnotherUserShouldReturnInternalServerError(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        FakeRequest request = requestWithBody
                .withAuthorizationCookie("bar@gmail.com", "123456")
                .withRequestedWithHeader()
                .withContentTypeHeader();

        testOperation(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    protected void operationWhenContentIsMissingShouldReturnInternalServerError(
            HandlerRef operationHandler) throws Exception {

        CustomFakeRequest request = createEmptyFakeRequest();
        testOperationWithAllHeadersAndCookies(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    protected void operationWhenUnknownModelIsPassedShouldReturnInternalServerError(
            HandlerRef operationHandler) throws Exception {

        CustomFakeRequest request = createFakeRequestWithUnknownModel();
        testOperationWithAllHeadersAndCookies(operationHandler, request, INTERNAL_SERVER_ERROR);
    }

    protected void operationWhenInvalidModelIsPassedShouldReturnInternalServerError(
            HandlerRef operationHandler, CustomFakeRequest requestWithBody) throws Exception {

        testOperationWithAllHeadersAndCookies(operationHandler, requestWithBody, INTERNAL_SERVER_ERROR);
    }
    protected CustomFakeRequest createEmptyFakeRequest() {
        return new CustomFakeRequest();
    }

    protected class CustomFakeRequest extends FakeRequest {

        public CustomFakeRequest() {
            super();
        }

        public CustomFakeRequest(String method, String path) {
            super(method, path);
        }

        public CustomFakeRequest withAuthorizationCookie(String userName, String password) {
            Http.Cookie playSession = getAuthorizationCookie(userName, password);
            this.withCookies(playSession);
            return this;
        }

        public CustomFakeRequest withAuthorizationCookie() {
            Http.Cookie playSession = getAuthorizationCookie();
            this.withCookies(playSession);
            return this;
        }

        public CustomFakeRequest withRequestedWithHeader() {
            this.withHeader(AbstractController.REQUESTED_WITH_HEADER, AbstractController.REQUEST_TYPE_XMLHTTP);
            return this;
        }

        public CustomFakeRequest withContentTypeHeader() {
            this.withHeader(Http.HeaderNames.CONTENT_TYPE, AbstractController.CONTENT_TYPE_JSON);
            return this;
        }
    }

    private class ControllerTestGlobalSettings extends GlobalSettings {
        private String initialDataFile;

        private ControllerTestGlobalSettings(String initialDataFile) {
            this.initialDataFile = initialDataFile;
        }

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

            Map<String, List> all = (Map<String, List>) Yaml.load(initialDataFile);
            Ebean.save(all.get("users"));
        }
    }
}
