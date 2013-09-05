package models;

import org.junit.Rule;
import org.junit.Before;
import org.junit.rules.ExpectedException;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ValidationException;

import play.GlobalSettings;
import play.db.ebean.Model;
import play.libs.Yaml;
import play.test.WithApplication;

import java.util.Map;
import java.util.List;

import static play.test.Helpers.*;

public abstract class AbstractModelTest extends WithApplication {

    protected static final String UPDATED_POSTFIX = "-updated";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), new GlobalSettings()));
    }

    protected void checkValidationExceptionOnInvalidModelSave(Model model) {
        exception.expect(ValidationException.class);
        model.save();
    }

    protected void initializeDatabase(String dataFile) {
        Map<String, List> all = (Map<String, List>) Yaml.load(dataFile);
        Ebean.save(all.get("users"));
    }
}
