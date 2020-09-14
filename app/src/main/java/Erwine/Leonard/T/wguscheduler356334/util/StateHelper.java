package Erwine.Leonard.T.wguscheduler356334.util;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

public class StateHelper {

    public static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    /**
     * Utility method for detecting and restoring state.
     *
     * @param savedInstanceState The saved instance state.
     * @param onRestore          The {@link Consumer} to invoke with the saved instance {@link Bundle} if state is being restored from a saved instance.
     * @param onInit             The {@link Consumer} to invoke with arguments {@link Bundle} if there is no saved state.
     * @return {@code true} if there was a saved state; otherwise {@code false} if a new instance is being initialized.
     */
    public static boolean restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments, Consumer<Bundle> onRestore, Consumer<Bundle> onInit) {
        if (null != savedInstanceState) {
            boolean stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
            if (stateInitialized) {
                onRestore.accept(savedInstanceState);
                return true;
            }
        }
        onInit.accept(getArguments.get());
        return false;
    }

    /**
     * Utility method for detecting and restoring state, including a unique identifier.
     *
     * @param idKey              The name of the key for the unique identifier value.
     * @param savedInstanceState The saved instance state.
     * @param getArguments       Gets the arguments when there is no state to restore.
     * @param onRestore          The {@link Consumer} to invoke with the saved instance {@link Bundle} if state is being restored from a saved instance.
     * @param onInit             The {@link Consumer} to invoke with arguments {@link Bundle} if there is no saved state.
     * @param onIdMissing        The {@link BiConsumer} to invoke with {@code true} if there a saved state, and the {@link Bundle}, if there was no unique identifier.
     * @return {@code true} if there was a saved state; otherwise {@code false} if a new instance is being initialized.
     */
    public static boolean restoreState(String idKey, @Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments, ObjLongConsumer<Bundle> onRestore, ObjLongConsumer<Bundle> onInit,
                                       BiConsumer<Boolean, Bundle> onIdMissing) {
        if (null != savedInstanceState) {
            boolean stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
            if (stateInitialized) {
                if (savedInstanceState.containsKey(idKey)) {
                    onRestore.accept(savedInstanceState, savedInstanceState.getLong(idKey));
                } else {
                    onIdMissing.accept(true, savedInstanceState);
                }
                return true;
            }
        }
        savedInstanceState = getArguments.get();
        if (null != savedInstanceState && savedInstanceState.containsKey(idKey)) {
            onInit.accept(savedInstanceState, savedInstanceState.getLong(idKey));
        } else {
            onIdMissing.accept(false, savedInstanceState);
        }
        return false;
    }

    /**
     * Utility method for detecting and restoring state, including a unique identifier.
     *
     * @param idKey              The name of the key for the unique identifier value.
     * @param savedInstanceState The saved instance state.
     * @param getArguments       Gets the arguments when there is no state to restore.
     * @param onInit             The {@link Consumer} to invoked to initialize state.
     * @param onIdMissing        The {@link BiConsumer} to invoke with {@code true} if there a saved state, and the {@link Bundle}, if there was no unique identifier.
     * @return {@code true} if there was a saved state; otherwise {@code false} if a new instance is being initialized.
     */
    public static boolean restoreState(String idKey, @Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments, ObjLongConsumer<Bundle> onInit,
                                       BiConsumer<Boolean, Bundle> onIdMissing) {
        if (null != savedInstanceState) {
            boolean stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
            if (stateInitialized) {
                if (savedInstanceState.containsKey(idKey)) {
                    onInit.accept(savedInstanceState, savedInstanceState.getLong(idKey));
                } else {
                    onIdMissing.accept(true, savedInstanceState);
                }
                return true;
            }
        }
        savedInstanceState = getArguments.get();
        if (null != savedInstanceState && savedInstanceState.containsKey(idKey)) {
            onInit.accept(savedInstanceState, savedInstanceState.getLong(idKey));
        } else {
            onIdMissing.accept(false, savedInstanceState);
        }
        return false;
    }

    public static void saveState(String idKey, Long id, Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        if (null != id) {
            outState.putLong(idKey, id);
        }
    }

    private StateHelper() {
    }

    public static <T extends Fragment> T setIdArgs(String idKey, Long id, T fragment, Bundle args) {
        if (null != id) {
            args.putLong(idKey, id);
        }
        fragment.setArguments(args);
        return fragment;
    }
}
