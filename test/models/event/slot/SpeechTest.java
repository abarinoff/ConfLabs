package models.event.slot;

import com.avaje.ebean.Ebean;
import models.AbstractModelTest;
import models.event.Speaker;
import models.event.Speech;

import org.junit.*;
import play.libs.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SpeechTest extends AbstractModelTest {

    @Test
    public void createSpeechWitNonEmptyTitleShouldSucceed() {
        Speech speech = new Speech("Foo");
        speech.save();

        Speech savedSpeech = Speech.find.byId(1L);
        assertEquals(speech, savedSpeech);
    }

    @Test
    public void createSpeechWithEmptyTitleShouldFail() {
        Speech speech = new Speech("");

        checkValidationExceptionOnInvalidModelSave(speech);
    }

    @Test
    public void createSpeechShouldPopulateId() {
        Speech speech = new Speech("Foo");
        speech.save();

        assertTrue(0 < speech.id);
    }

    @Test
    public void createSpeechShouldPersistFieldsCorrectly() {
        super.initializeDatabase("test/models/data/speech-with-speakers.yml");

        Speech speech = new Speech("Foo");
        Speaker speaker = Speaker.find.byId(1L);
        speech.speakers.add(speaker);
        speech.save();

        Speech savedSpeech = Speech.find.byId(speech.id);
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
        speech.setSpeakers(new ArrayList<Speaker>());
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

    @Override
    protected void initializeDatabase(String dataFile) {
        Map<String, List> all = (Map<String, List>) Yaml.load(dataFile);
        Ebean.save(all.get("users"));
        Ebean.save(all.get("speeches"));
    }
}
