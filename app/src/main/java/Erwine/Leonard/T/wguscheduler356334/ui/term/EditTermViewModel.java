package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddTermActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewTermActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditTermViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditTermViewModel.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    public static void startAddTermActivity(Context context, @NonNull LocalDate termStart) {
        Intent intent = new Intent(context, AddTermActivity.class);
        intent.putExtra(TermEntity.STATE_KEY_START, termStart.toEpochDay());
        context.startActivity(intent);
    }

    public static void startViewTermActivity(Context context, long termId) {
        Intent intent = new Intent(context, ViewTermActivity.class);
        intent.putExtra(TermEntity.STATE_KEY_ID, termId);
        context.startActivity(intent);
    }

    private TermEntity termEntity;
    private final MutableLiveData<TermEntity> entityLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Integer> startMessageLiveData;
    private boolean fromSavedState;
    private String name;
    private String normalizedName;
    private LocalDate start;
    private LocalDate end;
    private String notes;
    private String normalizedNotes;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        normalizedName = name = notes = normalizedNotes = "";
        entityLiveData = new MutableLiveData<>();
        startMessageLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
    }

    public Long getId() {
        return (null == termEntity) ? null : termEntity.getId();
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String value) {
        name = (null == value) ? "" : value;
        String oldValue = normalizedName;
        normalizedName = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedName.isEmpty()) {
            if (!oldValue.isEmpty()) {
                nameValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty()) {
            nameValidLiveData.postValue(true);
        }
    }

    public LocalDate getStart() {
        return start;
    }

    public synchronized void setStart(LocalDate value) {
        if (!Objects.equals(value, start)) {
            start = value;
            startMessageLiveData.postValue(validateDateRange(false).orElse(null));
        }
    }

    public LocalDate getEnd() {
        return end;
    }

    public synchronized void setEnd(LocalDate value) {
        if (!Objects.equals(value, end)) {
            end = value;
            startMessageLiveData.postValue(validateDateRange(false).orElse(null));
        }
    }

    public String getNotes() {
        return notes;
    }

    public synchronized void setNotes(String value) {
        if (null == value || value.isEmpty()) {
            normalizedNotes = notes = "";
        } else if (!value.equals(notes)) {
            notes = value;
            normalizedNotes = null;
        }
    }

    LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData;
    }

    LiveData<Integer> getStartMessageLiveData() {
        return startMessageLiveData;
    }

    public MutableLiveData<TermEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public boolean isFromSavedState() {
        return fromSavedState;
    }

    public synchronized Single<TermEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.restoreState");
        fromSavedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromSavedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            termEntity = new TermEntity();
        } else if (state.containsKey(TermEntity.STATE_KEY_ID)) {
            if (fromSavedState) {
                termEntity = new TermEntity(state, true);
            } else {
                return dbLoader.getTermById(state.getLong(TermEntity.STATE_KEY_ID))
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        } else {
            termEntity = new TermEntity(state, fromSavedState);
        }
        entityLiveData.postValue(termEntity);
        if (null == state) {
            setName(termEntity.getName());
            setStart(termEntity.getStart());
            setEnd(termEntity.getEnd());
            setNotes(termEntity.getNotes());
        } else if (fromSavedState) {
            setName(state.getString(TermEntity.STATE_KEY_NAME, ""));
            setStart((state.containsKey(TermEntity.STATE_KEY_START)) ? LocalDate.ofEpochDay(state.getLong(TermEntity.STATE_KEY_START)) : null);
            setEnd((state.containsKey(TermEntity.STATE_KEY_END)) ? LocalDate.ofEpochDay(state.getLong(TermEntity.STATE_KEY_END)) : null);
            setNotes(state.getString(TermEntity.STATE_KEY_NOTES, ""));
        } else {
            setName((state.containsKey(TermEntity.STATE_KEY_NAME)) ? state.getString(TermEntity.STATE_KEY_NAME) : termEntity.getName());
            setStart((state.containsKey(TermEntity.STATE_KEY_START)) ? LocalDate.ofEpochDay(state.getLong(TermEntity.STATE_KEY_START)) : termEntity.getStart());
            setEnd((state.containsKey(TermEntity.STATE_KEY_END)) ? LocalDate.ofEpochDay(state.getLong(TermEntity.STATE_KEY_END)) : termEntity.getEnd());
            setNotes((state.containsKey(TermEntity.STATE_KEY_NOTES)) ? state.getString(TermEntity.STATE_KEY_NOTES) : termEntity.getNotes());
        }
        return Single.just(termEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        termEntity.saveState(outState, true);
        outState.putString(TermEntity.STATE_KEY_NAME, name);
        LocalDate date = start;
        if (null != date) {
            outState.putLong(TermEntity.STATE_KEY_START, date.toEpochDay());
        }
        date = end;
        if (null != date) {
            outState.putLong(TermEntity.STATE_KEY_END, date.toEpochDay());
        }
        outState.putString(TermEntity.STATE_KEY_NOTES, notes);
    }

    public synchronized Single<List<Integer>> save() {
        LocalDate newStart = start;
        LocalDate newEnd = end;
        ArrayList<Integer> messages = new ArrayList<>();
        if (normalizedName.isEmpty()) {
            messages.add(R.string.message_name_required);
        }
        validateDateRange(true).ifPresent(messages::add);
        if (!messages.isEmpty()) {
            return Single.just(messages);
        }
        String originalName = termEntity.getName();
        LocalDate originalStart = termEntity.getStart();
        LocalDate originalEnd = termEntity.getEnd();
        String originalNotes = termEntity.getNotes();
        termEntity.setName(normalizedName);
        termEntity.setStart(newStart);
        termEntity.setEnd(newEnd);
        termEntity.setNotes(notes);
        return dbLoader.saveTerm(termEntity).doOnError(throwable -> {
            termEntity.setName(originalName);
            termEntity.setStart(originalStart);
            termEntity.setEnd(originalEnd);
            termEntity.setNotes(originalNotes);
            Log.e(getClass().getName(), "Error saving term", throwable);
        }).toSingleDefault(Collections.emptyList());
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteTerm(entityLiveData.getValue()).doOnError(throwable -> Log.e(getClass().getName(), "Error deleting term", throwable));
    }

    private void onEntityLoadedFromDb(TermEntity entity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onEntityLoaded");
        termEntity = entity;
        setName(entity.getName());
        setStart(entity.getStart());
        setEnd(entity.getEnd());
        setNotes(entity.getNotes());
        entityLiveData.postValue(entity);
    }

    private synchronized Optional<Integer> validateDateRange(boolean saveMode) {
        if (null != end) {
            if (null == start) {
                return Optional.of((saveMode) ? R.string.message_start_required_with_end : R.string.message_required);
            }
            if (start.compareTo(end) > 0) {
                return Optional.of(R.string.message_start_after_end);
            }
        }
        return Optional.empty();
    }

    public boolean isChanged() {
        if (null != termEntity.getId() && normalizedName.equals(termEntity.getName()) && Objects.equals(start, termEntity.getStart()) && Objects.equals(end, termEntity.getEnd())) {
            if (null == normalizedNotes) {
                normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(notes);
            }
            return !normalizedNotes.equals(termEntity.getNotes());
        }
        return true;
    }

}