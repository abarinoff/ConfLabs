package models.event;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.Query;

import models.AbstractModelTest;
import models.authentication.User;

import models.event.slot.OrganizationalSlot;
import models.event.slot.Slot;

import models.event.slot.SpeechSlot;
import org.junit.Test;
import play.db.ebean.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

public class EventTest extends AbstractModelTest {

    protected static final long OFFSET = 10;

    @Test
    public void createEventWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("Foo");
        user.events.add(event);
        user.save();

        Event savedEvent = Event.find.byId(event.id);
        assertEquals(event, savedEvent);
    }

    @Test
    public void createEventWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("");
        user.events.add(event);

        checkValidationExceptionOnInvalidModelSave(user);
    }

    @Test
    public void createEventShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/single-user-model.yml");
        User user = User.find.byId(1L);

        Event event = new Event("Foo");
        event.description = "bar";

        Location location = new Location("New Foo Location");
        event.location = location;

        Stage stage = new Stage("bar");
        event.stages.add(stage);

        Speaker speaker = new Speaker("John Smith");
        event.speakers.add(speaker);

        Speech speech = new Speech("New Foo Speech");
        event.speeches.add(speech);

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = new OrganizationalSlot(start, end, "New organizational slot");
        event.slots.add(slot);

        user.events.add(event);
        user.save();

        Event savedEvent = Event.find.byId(event.id);
        assertEquals(event, savedEvent);
    }

    @Test
    public void persistEventShouldPopulateIdField() {
        initializeDatabase("test/models/data/single-user-model.yml");
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

    @Test
    public void updateEventShouldPopulateFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.setTitle(event.title + UPDATED_POSTFIX);
        event.setDescription(event.description + UPDATED_POSTFIX);

        Location location = new Location("New Foo Location");
        event.setLocation(location);

        Stage stage = new Stage("bar");
        event.stages.add(stage);

        Speaker speaker = new Speaker("John Smith");
        event.speakers.add(speaker);

        Speech speech = new Speech("New Foo Speech");
        event.speeches.add(speech);

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = new OrganizationalSlot(start, end, "New organizational slot");
        event.slots.add(slot);

        event.update();

        Event updatedEvent = Event.find.byId(1L);
        assertEquals(event, updatedEvent);
    }

    @Test
    public void deleteEventShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.delete();

        Event deletedEvent = Event.find.byId(1L);
        assertNull(deletedEvent);
    }

    @Test
    public void deleteEventShouldDeleteLocation() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        event.delete();

        Location savedLocation = Location.find.byId(1L);
        assertNull(savedLocation);
    }

    @Test
    public void deleteEventShouldDeleteStages() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        List<Long> stageIds = new ArrayList<Long>();
        for (Stage stage : event.stages) {
            stageIds.add(stage.id);
        }

        event.delete();

        List<Model> list = fetchModelsByIds(Stage.class, stageIds);
        assertEquals(0, list.size());
    }

    @Test
    public void deleteEventShouldDeleteSpeakers() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        List<Long> speakerIds = new ArrayList<Long>();
        for (Speaker speaker : event.speakers) {
            speakerIds.add(speaker.id);
        }

        event.delete();

        List<Model> list = fetchModelsByIds(Speaker.class, speakerIds);
        assertEquals(0, list.size());
    }

    @Test
    public void deleteEventShouldDeleteSlots() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        List<Long> slotIds = new ArrayList<Long>();
        for (Slot slot : event.slots) {
            slotIds.add(slot.id);
        }

        event.delete();

        List<Model> list = fetchModelsByIds(Slot.class, slotIds);
        assertEquals(0, list.size());
    }

    @Test
    public void deleteEventShouldDeleteSpeeches() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event event = Event.find.byId(1L);
        List<Long> speechIds = new ArrayList<Long>();
        for (Speech speech : event.speeches) {
            speechIds.add(speech.id);
        }

        event.delete();

        List<Model> list = fetchModelsByIds(Speech.class, speechIds);
        assertEquals(0, list.size());
    }

    @Test
    public void readEventTitleShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.title, savedEvent.title);
    }

    @Test
    public void readEventDescriptionShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.description, savedEvent.description);
    }

    @Test
    public void readEventLocationShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.location, savedEvent.location);
    }

    @Test
    public void readEventStagesShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.stages, savedEvent.stages);
    }

    @Test
    public void readEventSpeakersShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.speakers, savedEvent.speakers);
    }

    @Test
    public void readEventSpeechesShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.speeches, savedEvent.speeches);
    }

    @Test
    public void readSlotsShouldSucceed() {
        initializeDatabase("test/models/data/event-with-all-entities.yml");

        Event savedEvent = Event.find.byId(1L);
        Event expectedEvent = createExpectedEvent();

        assertEquals(expectedEvent.slots, savedEvent.slots);
    }

    private Event createExpectedEvent() {
        Location location = new Location("foo");
        location.id = 1L;
        location.address = "14/1 Wall Street";

        Stage stage = new Stage("foo");
        stage.id = 1L;
        stage.capacity = 128;

        Speaker speaker = new Speaker("John Doe");
        speaker.id = 1L;
        speaker.position = "Engineer";
        speaker.description = "Senior Software Engineer, Lead";

        Speech speech = new Speech("Foo Speech");
        speech.id = 1L;

        GregorianCalendar start = new GregorianCalendar(2013, 8, 2, 9, 0, 0);
        GregorianCalendar stop = new GregorianCalendar(2013, 8, 2, 18, 0, 0);
        Slot slot = new SpeechSlot(start.getTime(), stop.getTime());
        slot.id = 1L;
        ((SpeechSlot) slot).stage = stage;
        ((SpeechSlot) slot).speech = speech;

        Event event = new Event("Foo Event");
        event.id = 1L;
        event.description = "Description";
        event.location = location;
        event.stages.add(stage);
        event.speakers.add(speaker);
        event.speeches.add(speech);
        event.slots.add(slot);

        return event;
    }

    private List<Model> fetchModelsByIds(Class cls, List<Long> ids) {
        Expression expressionIn = Expr.in("id", ids);
        Query<Model> query = Ebean.createQuery(cls);
        query.where(expressionIn);

        return query.findList();
    }
}