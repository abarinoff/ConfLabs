package models.event.slot;

import java.util.Date;

import models.event.Speech;
import models.event.Stage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("speech")
public class SpeechSlot extends Slot {

    @OneToOne
    public Stage stage;

    @OneToOne
    public Speech speech;

    public SpeechSlot(Date start, Date end) {
        super(start, end);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;

        SpeechSlot otherSlot = (SpeechSlot) other;

        if (speech != null ? !speech.equals(otherSlot.speech) : otherSlot.speech != null) return false;
        if (stage != null ? !stage.equals(otherSlot.stage) : otherSlot.stage != null) return false;

        return true;
    }
}
