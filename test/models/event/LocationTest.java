package models;

import com.avaje.ebean.ValidationException;
import models.event.Event;
import models.event.Location;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class LocationTest extends AbstractModelTest {

    @Test
    public void createLocationWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Location location = new Location("Президент готель");

        Event event = Event.find.byId(1L);
        event.location = location;
        event.update();

        assertNotNull(Location.find.byId(location.id));
    }

    @Test
    public void createLocationWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Event event = Event.find.byId(1L);
        event.location = new Location("");

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createLocationShouldPopulateEntityId() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Location location = new Location("Президент готель");
        Event event = Event.find.byId(1L);
        event.location = location;
        event.update();

        assertThat(location.id, not(equalTo(0L)));
    }

    @Test
    public void createLocationShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Location location = new Location("Президент готель");
        location.address = "Київ, вул. Госпітальна, 12";
        Event event = Event.find.byId(1L);
        event.location = location;
        event.update();

        Location storedLocation = Location.find.byId(location.id);
        assertEquals(location, storedLocation);
    }

    @Test
    public void updateLocationWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/event-with-location.yml");

        Location location = Location.find.byId(1L);
        location.setTitle(location.title + UPDATED_POSTFIX);
        location.update();

        Location storedLocation = Location.find.byId(1L);
        assertEquals(location.title, storedLocation.title);
    }

    @Test
    public void updateLocationWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/event-with-location.yml");

        Location location = Location.find.byId(1L);
        location.setTitle("");

        checkValidationExceptionOnInvalidModelSave(location);
    }

    @Test
    public void updateLocationShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-location.yml");

        Location location = Location.find.byId(1L);
        location.setTitle(location.title + UPDATED_POSTFIX);
        location.setAddress(location.address + UPDATED_POSTFIX);
        location.update();

        Location storedLocation = Location.find.byId(1L);
        assertEquals(location, storedLocation);
    }

    @Test
    public void updateLocationShouldNotCreateNewEntity() {
        initializeDatabase("test/models/data/event-with-location.yml");

        Location location = Location.find.byId(1L);
        location.setTitle(location.title + UPDATED_POSTFIX);
        location.setAddress(location.address + UPDATED_POSTFIX);
        location.update();

        int rowCount = Location.find.findRowCount();
        assertEquals(1, rowCount);
    }
}
