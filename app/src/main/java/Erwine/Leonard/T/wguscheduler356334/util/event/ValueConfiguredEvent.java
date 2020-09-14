package Erwine.Leonard.T.wguscheduler356334.util.event;

import androidx.annotation.NonNull;

import java.util.EventObject;

public class ValueConfiguredEvent<T> extends EventObject {
    private final T value;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ValueConfiguredEvent(Object source, T value) {
        super(source);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + ", value=" + value + "]";
    }
}
