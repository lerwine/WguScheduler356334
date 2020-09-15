package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.NormalizingCharSequence;
import Erwine.Leonard.T.wguscheduler356334.util.Values;
import Erwine.Leonard.T.wguscheduler356334.util.live.BooleanAndLiveData;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TermViewModel extends AndroidViewModel {

    private static final String LOG_TAG = TermViewModel.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    static final String ARGUMENT_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";
    public static final String ARGUMENT_KEY_NAME = "name";
    public static final String ARGUMENT_KEY_START_DATE = "start_date";
    public static final String ARGUMENT_KEY_END_DATE = "end_date";
    public static final String ARGUMENT_KEY_NOTES = "notes";
    public static final String ARGUMENT_KEY_ORIGINAL_NAME = "original_name";
    public static final String ARGUMENT_KEY_ORIGINAL_START_DATE = "original_start_date";
    public static final String ARGUMENT_KEY_ORIGINAL_END_DATE = "original_end_date";
    public static final String ARGUMENT_KEY_ORIGINAL_NOTES = "original_notes";

    private final MutableLiveData<TermEntity> entityLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Integer> startMessageLiveData;
    private final BooleanAndLiveData savableLiveData;
    private MutableLiveData<Boolean> nameChangedLiveData;
    private MutableLiveData<Boolean> startChangedLiveData;
    private MutableLiveData<Boolean> endChangedLiveData;
    private MutableLiveData<Boolean> notesChangedLiveData;
    private boolean fromInitializedState;
    private Long id;
    private final NormalizingCharSequence name;
    private LocalDate start;
    private LocalDate end;
    private final NormalizingCharSequence notes;

    public TermViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        name = new NormalizingCharSequence("", TermEntity.SINGLE_LINE_NORMALIZER);
        notes = new NormalizingCharSequence("", TermEntity.MULTI_LINE_NORMALIZER);
        entityLiveData = new MutableLiveData<>();
        startMessageLiveData = new MutableLiveData<>(R.string.command_ok);
        nameValidLiveData = new MutableLiveData<>(false);
        savableLiveData = new BooleanAndLiveData();
        savableLiveData.addSource(nameValidLiveData);
        savableLiveData.addSource(startMessageLiveData, t -> t == R.string.command_ok);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.rawString();
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public String getNotes() {
        return notes.rawString();
    }

    LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData;
    }

    LiveData<Integer> getStartMessageLiveData() {
        return startMessageLiveData;
    }

    public LiveData<Boolean> getSavableLiveData() {
        return savableLiveData;
    }

    public MutableLiveData<TermEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<TermEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.restoreState");
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(ARGUMENT_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        TermEntity entity;
        if (null == state) {
            id = null;
            name.setValue("");
            notes.setValue("");
            entity = new TermEntity();
        } else {
            id = (state.containsKey(ARGUMENT_KEY_TERM_ID)) ? state.getLong(ARGUMENT_KEY_TERM_ID) : null;
            if (fromInitializedState) {
                name.setValue(state.getString(ARGUMENT_KEY_NAME, ""));
                start = (state.containsKey(ARGUMENT_KEY_START_DATE)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_START_DATE)) : null;
                end = (state.containsKey(ARGUMENT_KEY_END_DATE)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_END_DATE)) : null;
                notes.setValue(state.getString(ARGUMENT_KEY_NOTES, ""));
            }
            if (null != id) {
                return dbLoader.getTermById(id).doOnSuccess(this::onEntityLoaded).doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
            if (fromInitializedState) {
                entity = new TermEntity(state.getString(ARGUMENT_KEY_ORIGINAL_NAME, ""),
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_START_DATE)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_START_DATE)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_END_DATE)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_END_DATE)) : null,
                        state.getString(ARGUMENT_KEY_ORIGINAL_NOTES, "")
                );
            } else {
                entity = new TermEntity(name.asIs(), start, end, notes.asIs());
            }
        }
        entityLiveData.postValue(entity);
        onTermNameEditTextChanged(name.toString(), entity);
        onDateRangeChanged();
        onTermNotesEditTextChanged(notes.toString(), entity);
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable save() {
        TermEntity entity = Objects.requireNonNull(entityLiveData.getValue());
        String originalName = entity.getName();
        LocalDate originalStart = entity.getStart();
        LocalDate originalEnd = entity.getEnd();
        String originalNotes = entity.getNotes();
        entity.setName(name.asIs());
        entity.setStart(start);
        entity.setEnd(end);
        entity.setNotes(notes.asIs());
        return dbLoader.saveTerm(entity).doOnError(throwable -> {
            entity.setName(originalName);
            entity.setStart(originalStart);
            entity.setEnd(originalEnd);
            entity.setNotes(originalNotes);
            Log.e(getClass().getName(), "Error saving term", throwable);
        });
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteTerm(entityLiveData.getValue()).doOnError(throwable -> Log.e(getClass().getName(), "Error deleting term", throwable));
    }

    private void onEntityLoaded(TermEntity entity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onEntityLoaded");
        if (!fromInitializedState) {
            name.setValue(entity.getName());
            start = entity.getStart();
            end = entity.getEnd();
            notes.setValue(entity.getNotes());
            if (null == entity.getId()) {
                onTermNameEditTextChanged(name.toString(), entity);
                onDateRangeChanged();
                onTermNotesEditTextChanged(notes.toString(), entity);
                entityLiveData.postValue(entity);
                return;
            }
            nameChangedLiveData = new MutableLiveData<>(false);
            startChangedLiveData = new MutableLiveData<>(false);
            endChangedLiveData = new MutableLiveData<>(false);
            notesChangedLiveData = new MutableLiveData<>(false);
        } else {
            if (null == entity.getId()) {
                entityLiveData.postValue(entity);
                return;
            }
            nameChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(name, entity.getName()));
            startChangedLiveData = new MutableLiveData<>((null == start) ? null != entity.getStart() : !start.equals(entity.getStart()));
            endChangedLiveData = new MutableLiveData<>((null == end) ? null != entity.getEnd() : !end.equals(entity.getEnd()));
            notesChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(notes, entity.getNotes()));
        }
        savableLiveData.addSource(nameChangedLiveData);
        savableLiveData.addSource(startChangedLiveData);
        savableLiveData.addSource(endChangedLiveData);
        savableLiveData.addSource(notesChangedLiveData);
        onTermNameEditTextChanged(name.toString(), entity);
        onDateRangeChanged();
        onTermNotesEditTextChanged(notes.toString(), entity);
        entityLiveData.postValue(entity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(ARGUMENT_KEY_STATE_INITIALIZED, true);
        if (null != id) {
            outState.putLong(ARGUMENT_KEY_TERM_ID, id);
        }
        outState.putString(ARGUMENT_KEY_NAME, name.rawString());
        TermEntity termEntity = Objects.requireNonNull(entityLiveData.getValue());
        LocalDate date = start;
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_START_DATE, date.toEpochDay());
        }
        date = end;
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_END_DATE, date.toEpochDay());
        }
        outState.putString(ARGUMENT_KEY_NOTES, notes.rawString());
        if (null == id) {
            outState.putString(ARGUMENT_KEY_ORIGINAL_NAME, termEntity.getName());
            date = termEntity.getStart();
            if (null != date) {
                outState.putLong(ARGUMENT_KEY_ORIGINAL_START_DATE, date.toEpochDay());
            }
            date = termEntity.getEnd();
            if (null != date) {
                outState.putLong(ARGUMENT_KEY_ORIGINAL_END_DATE, date.toEpochDay());
            }
            outState.putString(ARGUMENT_KEY_ORIGINAL_NOTES, termEntity.getNotes());
        }
    }

    void onTermNameEditTextChanged(String s) {
        onTermNameEditTextChanged(s, Objects.requireNonNull(entityLiveData.getValue()));
    }

    void onTermNameEditTextChanged(String s, TermEntity termEntity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onTermNameEditTextChanged");
        String oldValue = name.toString();
        name.setValue(s);
        if (!NormalizingCharSequence.equals(name, oldValue)) {
            if (null != nameChangedLiveData) {
                String originalValue = termEntity.getName();
                if (oldValue.equals(originalValue)) {
                    nameChangedLiveData.postValue(true);
                } else if (NormalizingCharSequence.equals(name, originalValue)) {
                    nameChangedLiveData.postValue(false);
                }
            }
            if (oldValue.isEmpty()) {
                nameValidLiveData.postValue(true);
            } else if (name.isEmpty()) {
                nameValidLiveData.postValue(false);
            }
        }
    }

    private void onDateRangeChanged() {
        if (null != end) {
            if (null != start) {
                if (start.compareTo(end) > 0) {
                    startMessageLiveData.postValue(R.string.message_start_after_end);
                } else {
                    startMessageLiveData.postValue(R.string.command_ok);
                }
            } else {
                startMessageLiveData.postValue(R.string.message_required);
            }
        }
    }

    void onStartDateChanged(LocalDate value) {
        LocalDate originalValue = Objects.requireNonNull(entityLiveData.getValue()).getStart();
        LocalDate oldValue = start;
        start = value;

        if (null != startChangedLiveData) {
            if (null == start) {
                if (null != oldValue) {
                    startChangedLiveData.postValue(null != originalValue);
                    onDateRangeChanged();
                }
            } else if (null == oldValue) {
                startChangedLiveData.postValue(null == originalValue);
                onDateRangeChanged();
            } else if (!oldValue.equals(start)) {
                if (start.equals(originalValue)) {
                    startChangedLiveData.postValue(false);
                } else if (oldValue.equals(originalValue)) {
                    startChangedLiveData.postValue(true);
                }
                onDateRangeChanged();
            }
        } else if (null == start) {
            if (null != oldValue) {
                onDateRangeChanged();
            }
        } else if (null == oldValue || !oldValue.equals(start)) {
            onDateRangeChanged();
        }
    }

    void onEndDateChanged(LocalDate value) {
        LocalDate originalValue = Objects.requireNonNull(entityLiveData.getValue()).getEnd();
        LocalDate oldValue = end;
        end = value;
        if (null != endChangedLiveData) {
            if (null == end) {
                if (null != oldValue) {
                    endChangedLiveData.postValue(null != originalValue);
                    onDateRangeChanged();
                }
            } else if (null == oldValue) {
                endChangedLiveData.postValue(null == originalValue);
                onDateRangeChanged();
            } else if (!end.equals(oldValue)) {
                if (end.equals(originalValue)) {
                    endChangedLiveData.postValue(false);
                } else if (oldValue.equals(originalValue)) {
                    endChangedLiveData.postValue(true);
                }
                onDateRangeChanged();
            }
        } else if (null == end) {
            if (null != oldValue) {
                onDateRangeChanged();
            }
        } else if (!end.equals(oldValue)) {
            onDateRangeChanged();
        }
    }

    void onTermNotesEditTextChanged(String s) {
        onTermNotesEditTextChanged(s, entityLiveData.getValue());
    }

    void onTermNotesEditTextChanged(String s, TermEntity entity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onTermNotesEditTextChanged");
        String oldValue = notes.toString();
        notes.setValue(s);
        if (!NormalizingCharSequence.equals(notes, oldValue)) {
            String originalValue = entity.getNotes();
            if (oldValue.equals(originalValue)) {
                notesChangedLiveData.postValue(true);
            } else if (!NormalizingCharSequence.equals(notes, originalValue)) {
                notesChangedLiveData.postValue(false);
            }
        }
    }

    public boolean isChanged() {
        return null == id || Values.notNullOr(nameChangedLiveData.getValue(), startChangedLiveData.getValue(),
                endChangedLiveData.getValue(), notesChangedLiveData.getValue());
    }
}