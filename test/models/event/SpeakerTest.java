package models.event;

import org.junit.Test;

import models.AbstractModelTest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class SpeakerTest extends AbstractModelTest {

    @Test
    public void createSpeakerWithNonEmptyNameShouldSucceed() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speaker speaker = new Speaker("foo");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertEquals(1, Speaker.find.findRowCount());

        Speaker savedSpeaker = Speaker.find.byId(speaker.id);
        assertNotNull(savedSpeaker);
        assertEquals(speaker, savedSpeaker);
    }

    @Test
    public void createSpeakerWithEmptyNameShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speaker speaker = new Speaker("");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createSpeakerShouldPopulateEntityId() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speaker speaker = new Speaker("foo");

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertThat(speaker.id, not(equalTo(0L)));
    }

    @Test
    public void createSpeakerShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = new Speaker("foo");
        speaker.position = "bar";
        speaker.description = "foo bar";

        Event event = Event.find.byId(1L);
        event.speakers.add(speaker);
        event.save();

        assertEquals(2, Speaker.find.findRowCount());

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

        checkValidationExceptionOnInvalidModelSave(speaker);
    }

    @Test
    public void updateExistingSpeakerWithNonEmptyNameShouldSucceed() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED_POSTFIX);
        speaker.save();

        Speaker updatedSpeaker = Speaker.find.byId(1L);

        assertNotNull(updatedSpeaker);
        assertEquals(speaker, updatedSpeaker);
    }

    @Test
    public void updateExistingSpeakerShouldNotCreateNewEntity() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED_POSTFIX);
        speaker.save();

        assertEquals(1, Speaker.find.findRowCount());
        assertThat(speaker.id, equalTo(1L));
    }

    @Test
    public void updateExistingSpeakerShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-speaker.yml");

        Speaker speaker = Speaker.find.byId(1L);
        speaker.setName(speaker.name + UPDATED_POSTFIX);
        speaker.setPosition(speaker.position + UPDATED_POSTFIX);
        speaker.setDescription(speaker.description + UPDATED_POSTFIX);
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
        assertEquals(0, Speaker.find.findRowCount());

        event = Event.find.byId(1L);
        assertThat(event.speakers.size(), equalTo(0));
    }
}
