package models.event;

import com.avaje.ebean.Ebean;

import models.AbstractModelTest;
import models.authentication.User;

import org.junit.Test;

import play.db.ebean.Model;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EventTest extends AbstractModelTest {

    @Test
    public void createEventWithNonEmptyTitleShouldSucceed() {
        super.initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("Foo");
        user.events.add(event);
        user.save();

        Event savedEvent = Event.find.byId(1L);
        assertEquals(event, savedEvent);
    }

    @Test
    public void createEventWithEmptyTitleShouldFail() {
        super.initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("");
        user.events.add(event);

        checkValidationExceptionOnInvalidModelSave(user);
    }

    @Test
    public void createEventShouldPersistFieldsCorrectly() {
        super.initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("Foo");
        event.description = "bar";
        user.events.add(event);
        user.save();

        Event savedEvent = Event.find.byId(1L);
        assertEquals(event, savedEvent);
    }

    @Test
    public void persistEventShouldPopulateIdField() {
        super.initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("Foo");
        user.events.add(event);
        user.save();

        assertTrue(event.id > 0);
    }

    @Test
    public void updateEventWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.setTitle(event.title + UPDATED_POSTFIX);
        event.update();

        Event updatedEvent = Event.find.byId(1L);
        assertEquals(event, updatedEvent);
    }

    @Test
    public void updateEventWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.setTitle("");

        checkValidationExceptionOnInvalidModelSave(event);
    }

    /*@Test
    public void updateEventShouldPopulateFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.setTitle(event.title + UPDATED_POSTFIX);
        event.setDescription(event.description + UPDATED_POSTFIX);

        Location location = Location.find.byId(2L);
        event.setLocation(location);

        Stage stage = new Stage("bar");
        event.stages.add(stage);

        Speaker speaker = new Speaker("John Smith");
        event.speakers.add(speaker);

        Slot slot = new OrganizationalSlot("New organizational slot", new Date(), "09:00:00", "09:51:00");
        event.slots.add(slot);

        event.update();

        Event updatedEvent = Event.find.byId(1L);
        assertEquals(event, updatedEvent);
    }*/

    /*@Test
    public void deleteEventShouldDeleteStages() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        List<Long> slotsIds = new ArrayList<Long>();
        for (Slot slot : event.slots) {
            slotsIds.add(slot.id);
        }

        event.delete();

        Event deletedEvent = Event.find.byId(1L);
        assertNull(deletedEvent);

        //Stage.find.where().idIn(slotsIds);
        Ebean.
        List<Slot> deletedSlots = Slot.find.where().in("id", slotsIds);
    }*/

    @Override
    public void initializeDatabase(String dataFile) {
        Map<String, List> all = (Map<String, List>) Yaml.load(dataFile);
        Ebean.save(all.get("speeches"));
        Ebean.save(all.get("users"));
    }

    private void checkReferentialExceptionOnDelete(Model model) {
        exception.expect(javax.persistence.PersistenceException.class);
        model.delete();
    }
}