package Erwine.Leonard.T.wguscheduler356334.util.event;

import java.util.EventListener;

public interface ValueConfiguredEventListener<T> extends EventListener {
    void valueConfigured(ValueConfiguredEvent<T> e);
}
