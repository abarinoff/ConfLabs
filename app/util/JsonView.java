package util;

import models.event.Event;

import java.io.Serializable;

public abstract class JsonView implements Serializable {

    public static class ShortView extends JsonView {
    }

    public static class FullView extends ShortView {
    }
}