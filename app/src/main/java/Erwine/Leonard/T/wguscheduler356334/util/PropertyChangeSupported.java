package Erwine.Leonard.T.wguscheduler356334.util;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class PropertyChangeSupported {

    private transient final PropertyChangeSupportImpl propertyChangeSupport = new PropertyChangeSupportImpl();

    /**
     * Gets the {@link PropertyChangeSupport} object for supporting bound properties. The {@link PropertyChangeSupport} for this class fires all
     * {@link PropertyChangeEvent}s on the JavaFX Application Thread.
     *
     * @return The {@link PropertyChangeSupport} object for supporting bound properties.
     */
    protected final PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (null != listener && Arrays.stream(propertyChangeSupport.getPropertyChangeListeners()).allMatch(t -> t != listener)) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected <T> void fireIndexedPropertyChange(String propertyName, int index, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (newValue != oldValue) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (newValue != oldValue) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, String oldValue, String newValue) {
        if ((null == oldValue) ? null != newValue : !oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, LocalDate oldValue, LocalDate newValue) {
        if ((null == oldValue) ? null != newValue : !oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T extends Enum<T>> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * This gets called just before a {@link PropertyChangeEvent} is fired for {@link PropertyChangeSupport}.
     * <p>
     * This will be called immediately after {@link PropertyChangeSupport#firePropertyChange(PropertyChangeEvent)} is called, and on the same thread.</p>
     *
     * @param event The {@code PropertyChangeEvent} that is being fired.
     * @throws Exception Allows implementing classes to throw any exception, which will be caught by the {@link Thread.UncaughtExceptionHandler}.
     */
    @SuppressWarnings("RedundantThrows")
    protected void onPropertyChange(PropertyChangeEvent event) throws Exception {
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

        private static final long serialVersionUID = -5190875010028850398L;

        private int noNameChange = -1;
        private final HashMap<Integer, Integer> noNameIndexedChange = new HashMap<>();

        PropertyChangeSupportImpl() {
            super(PropertyChangeSupported.this);
        }

        private void firePropertyChangeImpl(final PropertyChangeEvent event) {
            // Temporarily replace uncaught exception handler so we can log any exceptions thrown, that might otherwise be untracked.
            final Thread.UncaughtExceptionHandler oldEh = Thread.currentThread().getUncaughtExceptionHandler();
            final Thread.UncaughtExceptionHandler tempEh = (Thread t, Throwable e) -> {
                Log.e(PropertyChangeSupported.this.getClass().getName(), String.format("Uncaught exception firing %s property change event", event.getPropertyName()), e);
                if (null != oldEh) {
                    oldEh.uncaughtException(t, e);
                }
            };
            Thread.currentThread().setUncaughtExceptionHandler(tempEh);
            try {
                onPropertyChange(event);
            } catch (@SuppressWarnings("UseSpecificCatch") Throwable ex) {
                Thread.UncaughtExceptionHandler eh = Thread.currentThread().getUncaughtExceptionHandler();
                ((null == eh) ? tempEh : eh).uncaughtException(Thread.currentThread(), ex);
            } finally {
                try {
                    super.firePropertyChange(event);
                } catch (@SuppressWarnings("UseSpecificCatch") Throwable ex) {
                    Thread.UncaughtExceptionHandler eh = Thread.currentThread().getUncaughtExceptionHandler();
                    ((null == eh) ? tempEh : eh).uncaughtException(Thread.currentThread(), ex);
                } finally {
                    Thread.currentThread().setUncaughtExceptionHandler(oldEh);
                }
            }
        }

        @Override
        public void firePropertyChange(PropertyChangeEvent event) {
            firePropertyChangeImpl(event);
        }

    }
}
