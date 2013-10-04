package util;

import models.event.Stage;
import models.event.slot.Slot;
import models.event.slot.SpeechSlot;

import java.util.List;

public class ModelMergeHelper {
    public static void resetSlotStageReference(List<Stage> stages) {
        for(Stage stage : stages) {
            List<Slot> slots = Slot.find.where()
                .eq("stage_id", stage.id)
                .findList();

            for(Slot slot : slots) {
                ((SpeechSlot) slot).setStage(null);
                slot.update();
            }
        }
    }
}
