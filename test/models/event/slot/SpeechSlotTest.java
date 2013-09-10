package models.event.slot;

import org.junit.Test;

import models.event.Speech;
import models.event.Stage;

import java.util.*;

import static org.junit.Assert.*;

public class SpeechSlotTest extends SlotTest {

    @Test
    public void deleteExistingSlotShouldNotRemoveAssignedStage() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);
        slot.delete();

        assertEquals(0, Slot.find.findRowCount());
        assertEquals(2, Stage.find.findRowCount());
    }

    @Test
    public void deleteExistingSlotShouldNotRemoveAssignedSpeech() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);
        slot.delete();

        assertEquals(0, Slot.find.findRowCount());
        assertEquals(2, Speech.find.findRowCount());
    }

    protected void initializeReadyForSlotCreationDatabase() {
        initializeDatabase("test/models/data/event-ready-for-speech-slot-creation.yml");
    }

    @Override
    protected void initializeDatabaseWithSlot() {
        initializeDatabase("test/models/data/event-with-speech-slot.yml");
    }

    @Override
    protected Slot createExpectedSlot() {
        Stage stage = Stage.find.byId(1L);
        Speech speech = Speech.find.byId(1L);

        Slot slot = createSlot(new Date(1006527797000L), new Date(1006527917000L), stage, speech);
        slot.id = 1L;

        return slot;
    }

    @Override
    protected Slot createSlotWithAllFields(Date start, Date end) {
        Stage stage = Stage.find.byId(1L);
        Speech speech = Speech.find.byId(1L);

        return createSlot(start, end, stage, speech);
    }

    @Override
    protected Slot createSlotWithRequiredFields(Date start, Date end) {
        return createSlot(start, end, null, null);
    }

    @Override
    protected void updateAllSlotFields(Slot slot, Date start, Date end) {
        Stage stage = Stage.find.byId(2L);
        Speech speech = Speech.find.byId(2L);

        SpeechSlot slotToUpdate = (SpeechSlot) slot;
        slotToUpdate.setStart(start);
        slotToUpdate.setEnd(end);
        slotToUpdate.setStage(stage);
        slotToUpdate.setSpeech(speech);
    }

    private SpeechSlot createSlot(Date start, Date end, Stage stage, Speech speech) {
        SpeechSlot slot = new SpeechSlot(start, end);
        slot.stage = stage;
        slot.speech = speech;

        return slot;
    }
}
