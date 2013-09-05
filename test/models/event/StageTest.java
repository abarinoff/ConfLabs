package models.event;

import org.junit.Test;

import models.AbstractModelTest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class StageTest extends AbstractModelTest {

    @Test
    public void createStageWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Stage stage = new Stage("foo");

        Event event = Event.find.byId(1L);
        event.stages.add(stage);
        event.save();

        assertEquals(1, Stage.find.all().size());

        Stage savedStage = Stage.find.byId(stage.id);
        assertNotNull(savedStage);
        assertEquals(stage, savedStage);
    }

    @Test
    public void createStageWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Stage stage = new Stage("");

        Event event = Event.find.byId(1L);
        event.stages.add(stage);

        checkValidationExceptionOnInvalidModelSave(event);
    }

    @Test
    public void createStageShouldPopulateEntityId() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Stage stage = new Stage("foo");

        Event event = Event.find.byId(1L);
        event.stages.add(stage);
        event.save();

        assertThat(stage.id, not(equalTo(0L)));
    }

    @Test
    public void createStageShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/user-with-event.yml");

        Stage stage = new Stage("foo");
        stage.capacity = 200;

        Event event = Event.find.byId(1L);
        event.stages.add(stage);
        event.save();

        assertEquals(1, Stage.find.all().size());

        Stage savedStage = Stage.find.byId(stage.id);
        assertNotNull(savedStage);
        assertEquals(stage, savedStage);
    }

    @Test
    public void readExistingStageShouldSucceed() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Stage expectedStage = new Stage("Stage1");
        expectedStage.id = 1L;

        Stage readStage = Stage.find.byId(1L);

        assertNotNull(readStage);
        assertEquals(expectedStage, readStage);
    }

    @Test
    public void updateExistingStageWithEmptyTitleShouldFail() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Stage stage = Stage.find.byId(1L);
        stage.setTitle("");

        checkValidationExceptionOnInvalidModelSave(stage);
    }

    @Test
    public void updateExistingStageWithNonEmptyTitleShouldSucceed() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Stage stage = Stage.find.byId(1L);
        stage.setTitle(stage.title + UPDATED_POSTFIX);
        stage.save();

        Stage updatedStage = Stage.find.byId(1L);

        assertNotNull(updatedStage);
        assertEquals(stage, updatedStage);
    }

    @Test
    public void updateExistingStageShouldNotCreateNewEntity() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Stage stage = Stage.find.byId(1L);
        stage.setTitle(stage.title + UPDATED_POSTFIX);
        stage.save();

        assertEquals(1, Stage.find.all().size());
        assertThat(stage.id, equalTo(1L));
    }

    @Test
    public void updateExistingStageShouldPersistFieldsCorrectly() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Stage stage = Stage.find.byId(1L);
        stage.setTitle(stage.title + UPDATED_POSTFIX);
        stage.setCapacity(stage.capacity + 10);
        stage.save();

        Stage updatedStage = Stage.find.byId(1L);

        assertNotNull(updatedStage);
        assertEquals(stage, updatedStage);
    }

    @Test
    public void deleteExistingStageShouldSucceed() {
        initializeDatabase("test/models/data/event-with-stage.yml");

        Event event = Event.find.byId(1L);
        assertThat(event.stages.size(), equalTo(1));

        event.stages.get(0).delete();
        assertEquals(0, Stage.find.all().size());

        event = Event.find.byId(1L);
        assertThat(event.stages.size(), equalTo(0));
    }
}
