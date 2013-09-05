package models;

import models.event.Event;
import models.event.Speaker;

import play.GlobalSettings;
import play.libs.Yaml;
import play.db.ebean.Model;
import play.test.WithApplication;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ValidationException;

import java.util.Map;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static org.hamcrest.CoreMatchers.*;

public class SpeakerTest extends WithApplication {

    private static final String UPDATED = "-updated";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), new GlobalSettings()));
    }

    @Test
    public void createSpeakerWithNonEmptyNameShouldSucceed() {
        initializeDatabase("test/models/data/single-user-with-one-event.yml");

        Speaker speaker = new Speaker("foo");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertEquals(1, Speaker.find.all().size());

        Speaker savedSpeaker = Speaker.find.byId(speaker.id);
        assertNotNull(savedSpeaker);
        assertEquals(speaker, savedSpeaker);
    }

    @Test
    public void createSpeakerWithEmptyNameShouldFail() {
        initializeDatabase("test/models/data/single-user-with-one-event.yml");

        Speaker speaker = new Speaker("");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);

        checkValidationExceptionOnSave(event);
    }

    @Test
    public void createSpeakerShouldPopulateEntityId() {
        initializeDatabase("test/models/data/single-user-with-one-event.yml");

        Speaker speaker = new Speaker("foo");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertThat(speaker.id, not(equalTo(0L)));
    }

    @Test
    public void createSpeakerShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/single-user-with-one-event.yml");

        Speaker speaker = new Speaker("foo");
        speaker.position = "bar";
        speaker.description = "foo bar";

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertEquals(1, Speaker.find.all().size());

        Speaker savedSpeaker = Speaker.find.byId(speaker.id);
        assertNotNull(savedSpeaker);
        assertEquals(speaker, savedSpeaker);
    }

    @Test
    public void readExistingSpeakerShouldSucceed() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker expectedSpeaker = new Speaker("Alex");
        expectedSpeaker.id = 1L;
        expectedSpeaker.position = "Programmer";
        expectedSpeaker.description = "Bla-bla-bla";

        Speaker readSpeaker = Speaker.find.byId(1L);

        assertNotNull(readSpeaker);
        assertEquals(expectedSpeaker, readSpeaker);
    }

    @Test
    public void updateExistingSpeakerWithEmptyNameShouldFail() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName("");

        checkValidationExceptionOnSave(speaker);
    }

    @Test
    public void updateExistingSpeakerWithNonEmptyNameShouldSucceed() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED);
        speaker.save();

        Speaker updatedSpeaker = Speaker.find.byId(1L);

        assertNotNull(updatedSpeaker);
        assertEquals(speaker, updatedSpeaker);
    }

    @Test
    public void updateExistingSpeakerShouldNotCreateNewEntity() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED);
        speaker.save();

        assertEquals(1, Speaker.find.all().size());
        assertThat(speaker.id, equalTo(1L));
    }

    @Test
    public void updateExistingSpeakerShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED);
        speaker.setPosition(speaker.position + UPDATED);
        speaker.setDescription(speaker.description + UPDATED);
        speaker.save();

        Speaker updatedSpeaker = Speaker.find.byId(1L);

        assertNotNull(updatedSpeaker);
        assertEquals(speaker, updatedSpeaker);
    }

    @Test
    public void deleteExistingSpeakerShouldSucceed() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Event event = Event.find.byId(1L);
        assertThat(event.speakers.size(), equalTo(1));

        event.speakers.get(0).delete();
        assertEquals(0, Speaker.find.all().size());

        event = Event.find.byId(1L);
        assertThat(event.speakers.size(), equalTo(0));
    }

    private void checkValidationExceptionOnSave(Model model) {
        exception.expect(ValidationException.class);
        model.save();
    }

    private void initializeDatabase(String dataFile) {
        Map<String, List> all = (Map<String, List>) Yaml.load(dataFile);
        Ebean.save(all.get("users"));
    }
}
