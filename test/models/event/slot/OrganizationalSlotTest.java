package models.event.slot;

import org.junit.Test;

import java.util.Date;
import models.event.Event;

public class OrganizationalSlotTest extends SlotTest {

    @Test
    public void createSlotWithEmptyTitleShouldFail() {
        initializeReadyForSlotCreationDatabase();

        Date start = new Date();
        Date end = new Date(start.getTime() + OFFSET);
        Slot slot = createSlot(start, end, "");

        Event event = Event.find.byId(1L);
        event.slots.add(slot);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void updateExistingSlotWithEmptyTitleShouldFail() {
        initializeDatabaseWithSlot();

        OrganizationalSlot slot = (OrganizationalSlot) Slot.find.byId(1L);
        slot.setTitle("");

        checkValidationExceptionOnInvalidModelSave(slot);
    }

    @Override
    protected void initializeReadyForSlotCreationDatabase() {
        initializeDatabase("test/models/data/user-with-event.yml");
    }

    @Override
    protected void initializeDatabaseWithSlot() {
        initializeDatabase("test/models/data/event-with-org-slot.yml");
    }

    @Override
    protected Slot createExpectedSlot() {
        OrganizationalSlot slot = createSlot(new Date(1006527797000L),
                new Date(1006527917000L), "Cofee break");
        slot.id = 1L;
        return slot;
    }

    @Override
    protected Slot createSlotWithAllFields(Date start, Date end) {
        return createSlot(start, end, "foo");
    }

    @Override
    protected Slot createSlotWithRequiredFields(Date start, Date end) {
        return createSlotWithAllFields(start, end);
    }

    @Override
    protected void updateAllSlotFields(Slot slot, Date start, Date end) {
        OrganizationalSlot slotToUpdate = (OrganizationalSlot) slot;
        slotToUpdate.setStart(start);
        slotToUpdate.setEnd(end);
        slotToUpdate.setTitle(slotToUpdate.title + UPDATED_POSTFIX);
    }

    private OrganizationalSlot createSlot(Date start, Date end, String title) {
        return new OrganizationalSlot(start, end, title);
    }
}
