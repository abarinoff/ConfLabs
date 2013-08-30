package models.event.slot;

import models.event.Speech;
import models.event.Stage;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class SpeechSlot extends Slot {

    @OneToOne
    public Stage stage;

    @OneToOne
    public Speech speech;

    public SpeechSlot(Date date, String startTime, String endTime) {
        super(date, startTime, endTime);
    }
}
