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
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditTermViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditTermViewModel.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    public static void startAddTermActivity(Context context, @NonNull LocalDate termStart) {
        Intent intent = new Intent(context, AddTermActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_START, false), termStart.toEpochDay());
        context.startActivity(intent);
    }

    public static void startViewTermActivity(Context context, long termId) {
        Intent intent = new Intent(context, ViewTermActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_ID, false), termId);
        context.startActivity(intent);
    }

    private TermEntity termEntity;
    private final MutableLiveData<TermEntity> entityLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Integer> startMessageLiveData;
    private final MutableLiveData<String> nameLiveData;
    private final CurrentValues currentValues;
    private boolean fromSavedState;
    private String normalizedName;
    private String normalizedNotes;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        normalizedName = normalizedNotes = "";
        entityLiveData = new MutableLiveData<>();
        startMessageLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
        nameLiveData = new MutableLiveData<>("");
        currentValues = new CurrentValues();
    }

    public Long getId() {
        return (null == termEntity) ? null : termEntity.getId();
    }

    public String getName() {
        return currentValues.getName();
    }

    public synchronized void setName(String value) {
        currentValues.setName(value);
    }

    public LocalDate getStart() {
        return currentValues.getStart();
    }

    public synchronized void setStart(LocalDate value) {
        currentValues.setStart(value);
    }

    public LocalDate getEnd() {
        return currentValues.getEnd();
    }

    public synchronized void setEnd(LocalDate value) {
        currentValues.setEnd(value);
    }

    public String getNotes() {
        return currentValues.getNotes();
    }

    public synchronized void setNotes(String value) {
        currentValues.setNotes(value);
    }

    public String getNormalizedNotes() {
        if (null == normalizedNotes) {
            normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(currentValues.notes);
            if (normalizedNotes.equals(currentValues.notes)) {
                currentValues.notes = null;
            }
        }
        return normalizedNotes;
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

    public MutableLiveData<String> getNameLiveData() {
        return nameLiveData;
    }

    public boolean isFromSavedState() {
        return fromSavedState;
    }

    public synchronized Single<TermEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.restoreState");
        fromSavedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromSavedState) ? savedInstanceState : getArguments.get();
        termEntity = new TermEntity();
        if (null != state) {
            currentValues.restoreState(state, false);
            Long id = currentValues.getId();
            if (null == id || fromSavedState) {
                termEntity.restoreState(state, fromSavedState);
            } else {
                return dbLoader.getTermById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        }
        entityLiveData.postValue(termEntity);
        return Single.just(termEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        termEntity.saveState(outState, true);
    }

    public synchronized Single<List<Integer>> save() {
        LocalDate newStart = currentValues.start;
        LocalDate newEnd = currentValues.end;
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
        termEntity.setNotes(currentValues.notes);
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
        entityLiveData.postValue(termEntity);
    }

    private synchronized Optional<Integer> validateDateRange(boolean saveMode) {
        if (null != currentValues.end) {
            if (null == currentValues.start) {
                return Optional.of((saveMode) ? R.string.message_start_required_with_end : R.string.message_required);
            }
            if (currentValues.start.compareTo(currentValues.end) > 0) {
                return Optional.of(R.string.message_start_after_end);
            }
        }
        return Optional.empty();
    }

    public boolean isChanged() {
        if (null != termEntity.getId() && normalizedName.equals(termEntity.getName()) && Objects.equals(currentValues.start, termEntity.getStart()) && Objects.equals(currentValues.end, termEntity.getEnd())) {
            return !getNormalizedNotes().equals(termEntity.getNotes());
        }
        return true;
    }

    private class CurrentValues implements Term {

        private Long id;
        private String name = "";
        private LocalDate start;
        private LocalDate end;
        private String notes = "";

        @Nullable
        @Override
        public Long getId() {
            return (null == termEntity) ? id : termEntity.getId();
        }

        @Override
        public void setId(Long id) {
            if (null != termEntity) {
                termEntity.setId(id);
            }
            this.id = id;
        }

        @NonNull
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = (null == name) ? "" : name;
            String oldValue = normalizedName;
            normalizedName = TermEntity.SINGLE_LINE_NORMALIZER.apply(name);
            if (normalizedName.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    nameValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty()) {
                nameValidLiveData.postValue(true);
            }
            nameLiveData.postValue(normalizedName);
        }

        @Nullable
        @Override
        public LocalDate getStart() {
            return start;
        }

        @Override
        public void setStart(LocalDate start) {
            if (!Objects.equals(this.start, start)) {
                this.start = start;
                startMessageLiveData.postValue(validateDateRange(false).orElse(null));
            }
        }

        @Nullable
        @Override
        public LocalDate getEnd() {
            return end;
        }

        @Override
        public void setEnd(LocalDate end) {
            if (!Objects.equals(this.end, end)) {
                this.end = end;
                startMessageLiveData.postValue(validateDateRange(false).orElse(null));
            }
        }

        @NonNull
        @Override
        public String getNotes() {
            return (null == notes) ? normalizedNotes : notes;
        }

        @Override
        public void setNotes(String notes) {
            if (null == notes || notes.isEmpty()) {
                normalizedNotes = "";
                this.notes = null;
            } else if (!getNotes().equals(notes)) {
                this.notes = notes;
                normalizedNotes = null;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}