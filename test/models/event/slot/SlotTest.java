package models.event.slot;

import models.event.Event;
import models.AbstractModelTest;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;

public abstract class SlotTest extends AbstractModelTest {

    protected static final long OFFSET = 10;

    @Test
    public void createSlotWithValidStartAndEndDatesShouldSucceed() {
        initializeReadyForSlotCreationDatabase();

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = createSlotWithRequiredFields(start, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);
        event.save();

        assertEquals(1, Slot.find.findRowCount());

        Slot savedSlot = Slot.find.byId(slot.id);
        assertNotNull(savedSlot);
        assertEquals(slot, savedSlot);
    }

    @Test
    public void createSlotWithNullStartAndNotNullEndDatesShouldFail() {
        initializeReadyForSlotCreationDatabase();

        Date end = new Date();
        Slot slot = createSlotWithRequiredFields(null, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createSlotWithNotNullStartAndNullEndDatesShouldFail() {
        initializeReadyForSlotCreationDatabase();

        Date start = new Date();
        Slot slot = createSlotWithRequiredFields(start, null);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createSlotWithNullStartAndNullEndDatesShouldFail() {
        initializeReadyForSlotCreationDatabase();

        Slot slot = createSlotWithRequiredFields(null, null);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }

/*
    @Test
    public void createSlotWithEndDateWhichPrecedesStartDateShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Date start = new Date();
        Date end = new Date(start.getTime() - OFFSET);
        Slot slot = createSlot(start, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createSlotWithEndDateEqualsToStartDateShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Date start = new Date();
        Date end = new Date(start.getTime());
        Slot slot = createSlot(start, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }
*/

    @Test
    public void createSlotShouldPopulateEntityId() {
        initializeReadyForSlotCreationDatabase();

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = createSlotWithRequiredFields(start, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);
        event.save();

        assertThat(slot.id, not(equalTo(0L)));
    }

    @Test
    public void createSlotShouldPersistFieldsCorrectly() {
        initializeReadyForSlotCreationDatabase();

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = createSlotWithAllFields(start, end);

        Event event = Event.find.byId(1L);
        event.slots.add(slot);
        event.save();

        assertEquals(1, Slot.find.findRowCount());

        Slot savedSlot = Slot.find.byId(slot.id);
        assertNotNull(savedSlot);
        assertEquals(slot, savedSlot);
    }

    @Test
    public void readExistingSlotShouldSucceed() {
        initializeDatabaseWithSlot();
        Slot expectedSlot = createExpectedSlot();
        Slot readSlot = Slot.find.byId(1L);

        assertNotNull(readSlot);
        assertEquals(expectedSlot, readSlot);
    }

    @Test
    public void updateExistingSlotWithValidStartAndEndDatesShouldSucceed() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newStart = new Date();
        Date newEnd = new Date(newStart.getTime() + OFFSET);
        slot.setStart(newStart);
        slot.setEnd(newEnd);
        slot.save();

        Slot updatedSlot = Slot.find.byId(1L);

        assertNotNull(updatedSlot);
        assertEquals(slot, updatedSlot);
    }

    @Test
    public void updateExistingSlotWithNullStartAndNotNullEndDatesShouldFail() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newEnd = new Date();
        slot.setStart(null);
        slot.setEnd(newEnd);

        checkValidationExceptionOnInvalidModelSave(slot);
    }

    @Test
    public void updateExistingSlotWithNotNullStartAndNullEndDatesShouldFail() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newStart = new Date();
        slot.setStart(newStart);
        slot.setEnd(null);

        checkValidationExceptionOnInvalidModelSave(slot);
    }

    @Test
    public void updateExistingSlotWithNullStartAndNullEndDatesShouldFail() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);
        slot.setStart(null);
        slot.setEnd(null);

        checkValidationExceptionOnInvalidModelSave(slot);
    }

/*
    @Test
    public void updateExistingSlotWithEndDateWhichPrecedesStartDateShouldFail() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newStart = new Date();
        Date newEnd = new Date(start.getTime() - OFFSET);
        slot.setStart(newStart);
        slot.setEnd(newEnd);

        checkValidationExceptionOnInvalidModelSave(slot);
    }

    @Test
    public void createSlotWithEndDateEqualsToStartDateShouldFail() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newStart = new Date();
        Date newEnd = new Date(newStart.getTime());
        slot.setStart(newStart);
        slot.setEnd(newEnd);

        checkValidationExceptionOnInvalidModelSave(slot);
    }
*/

    @Test
    public void updateExistingSlotShouldNotCreateNewEntity() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);

        Date newStart = new Date();
        Date newEnd = new Date(newStart.getTime() + OFFSET);
        slot.setStart(newStart);
        slot.setEnd(newEnd);
        slot.save();

        assertEquals(1, Slot.find.findRowCount());
        assertThat(slot.id, equalTo(1L));
    }

    @Test
    public void updateExistingSlotShouldPersistFieldsCorrectly() {
        initializeDatabaseWithSlot();

        Slot slot = Slot.find.byId(1L);
        Date newStart = new Date();
        Date newEnd = new Date(newStart.getTime() + OFFSET);
        updateAllSlotFields(slot, newStart, newEnd);
        slot.save();

        Slot updatedSlot = Slot.find.byId(1L);

        assertNotNull(updatedSlot);
        assertEquals(slot, updatedSlot);
    }

    @Test
    public void deleteExistingSlotShouldSucceed() {
        initializeDatabaseWithSlot();

        Event event = Event.find.byId(1L);
        assertThat(event.slots.size(), equalTo(1));

        event.slots.get(0).delete();
        assertEquals(0, Slot.find.findRowCount());

        event = Event.find.byId(1L);
        assertThat(event.slots.size(), equalTo(0));
    }

    protected abstract void initializeReadyForSlotCreationDatabase();

    protected abstract void initializeDatabaseWithSlot();

    protected abstract Slot createExpectedSlot();

    protected abstract Slot createSlotWithAllFields(Date start, Date end);

    protected abstract Slot createSlotWithRequiredFields(Date start, Date end);

    protected abstract void updateAllSlotFields(Slot slot, Date start, Date end);
}
