package models.event.slot;

import models.event.Event;
import models.event.Speaker;
import models.event.Speech;
import models.AbstractModelTest;

import org.junit.*;

import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class SpeechTest extends AbstractModelTest {

    @Test
    public void createSpeechWitNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speech speech = new Speech("Foo");

        Event event = Event.find.byId(1L);
        event.speeches.add(speech);
        event.save();

        assertEquals(1, Speech.find.all().size());

        Speech savedSpeech = Speech.find.byId(speech.id);
        assertNotNull(savedSpeech);
        assertEquals(speech, savedSpeech);
    }

    @Test
    public void createSpeechWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speech speech = new Speech("");

        Event event = Event.find.byId(1L);
        event.speeches.add(speech);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createSpeechShouldPopulateId() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Speech speech = new Speech("Foo");

        Event event = Event.find.byId(1L);
        event.speeches.add(speech);
        event.save();

        assertThat(speech.id, not(equalTo(0L)));
    }

    @Test
    public void createSpeechShouldPersistFieldsCorrectly() {
        super.initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech speech = new Speech("Foo");
        Speaker speaker = Speaker.find.byId(1L);
        speech.speakers.add(speaker);

        Event event = Event.find.byId(1L);
        event.speeches.add(speech);
        event.save();

        assertEquals(2, Speech.find.findRowCount());

        Speech savedSpeech = Speech.find.byId(speech.id);
        assertNotNull(savedSpeech);
        assertEquals(speech, savedSpeech);
    }

    @Test
    public void updateSpeechTitleWithEmptyValueShouldFail() {
        initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech speech = Speech.find.byId(1L);
        speech.setTitle("");

        checkValidationExceptionOnInvalidModelSave(speech);
    }

    @Test
    public void updateSpeechTitleWithNonEmptyValueShouldSucceed() {
        initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech speech = Speech.find.byId(1L);
        speech.setTitle(speech.title + UPDATED_POSTFIX);
        speech.update();

        Speech savedSpeech = Speech.find.byId(1L);
        assertEquals(savedSpeech, speech);
    }

    @Test
    public void updateSpeechFieldsPersistCorrectly() {
        initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech speech = Speech.find.byId(1L);
        speech.setTitle(speech.title + UPDATED_POSTFIX);
        speech.setSpeakers(new LinkedList<Speaker>());
        Speaker newSpeaker = Speaker.find.byId(2L);
        speech.speakers.add(newSpeaker);
        speech.update();

        Speech loadedSpeech = Speech.find.byId(1L);
        assertEquals(speech, loadedSpeech);
    }

    @Test
    public void deleteSpeechShouldSucceed() {
        initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech.find.byId(1L).delete();

        int rowCount = Speech.find.findRowCount();
        assertEquals(0, rowCount);

        Speaker speaker = Speaker.find.byId(1L);
        assertNotNull(speaker);
    }
}
