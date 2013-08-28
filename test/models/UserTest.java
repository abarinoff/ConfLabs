package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ValidationException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import play.libs.Yaml;
import play.test.WithApplication;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createUserWithNonEmptyCredentialsShouldSucceed() {
        User user = new User("foo@bar.com", "123456");
        user.save();

        assertEquals(1, User.find.all().size());

        User savedUser = User.find.byId(user.id);

        assertNotNull(savedUser);
        assertEquals(user.email, savedUser.email);
        assertEquals(user.password, savedUser.password);
    }

    @Test
    public void createUserWithEmptyCredentialsShouldFail() {
        User user = new User("", "");

        checkValidationOnSave(user);
        assertEquals(0, User.find.all().size());
    }

    @Test
    public void createUserWithEmptyEmailShouldFail() {
        User user = new User("", "123456");

        checkValidationOnSave(user);
        assertEquals(0, User.find.all().size());
    }

    @Test
    public void createUserWithEmptyPasswordShouldFail() {
        User user = new User("foo@bar.com", "");

        checkValidationOnSave(user);
        assertEquals(0, User.find.all().size());
    }

    @Test
    public void updateExistingUserWithNonEmptyCredentialsShouldSucceed() {
        Ebean.save((List) Yaml.load("test/models/data/simple-user-model.yml"));

        User user = User.find.byId(1L);
        user.setEmail("bar@foo.com");
        user.setPassword("654321");
        user.save();

        assertEquals(1, User.find.all().size());

        User updatedUser = User.find.byId(1L);

        assertNotNull(updatedUser);
        assertEquals(user.email, updatedUser.email);
        assertEquals(user.password, updatedUser.password);
    }

    @Test
    public void updateExistingUserWithEmptyCredentialsShouldFail() {
        Ebean.save((List) Yaml.load("test/models/data/simple-user-model.yml"));

        User user = User.find.byId(1L);
        user.setEmail("");
        user.setPassword("");

        checkValidationOnSave(user);
    }

    @Test
    public void updateExistingUserWithEmptyEmailShouldFail() {
        Ebean.save((List) Yaml.load("test/models/data/simple-user-model.yml"));

        User user = User.find.byId(1L);
        user.setEmail("");

        checkValidationOnSave(user);
    }

    @Test
    public void updateExistingUserWithEmptyPasswordShouldFail() {
        Ebean.save((List) Yaml.load("test/models/data/simple-user-model.yml"));

        User user = User.find.byId(1L);
        user.setPassword("");

        checkValidationOnSave(user);
    }

    private void checkValidationOnSave(User user) {
        exception.expect(ValidationException.class);
        user.save();
    }
}
