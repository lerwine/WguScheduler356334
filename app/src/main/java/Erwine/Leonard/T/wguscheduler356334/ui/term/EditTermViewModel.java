package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddTermActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewTermActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

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
    private final MutableLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final MutableLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Integer> startMessageLiveData;
    private final CurrentValues currentValues;
    private String viewTitle;
    private Spanned overview;
    private boolean fromInitializedState;
    private String normalizedName;
    private String normalizedNotes;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        titleFactoryLiveData = new MutableLiveData<>(r -> r.getString(R.string.title_activity_view_term));
        overviewFactoryLiveData = new MutableLiveData<>(r -> new SpannableString(""));
        normalizedName = normalizedNotes = "";
        entityLiveData = new MutableLiveData<>();
        startMessageLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
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

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public MutableLiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<TermEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.restoreState");
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        termEntity = new TermEntity();
        viewTitle = null;
        overview = null;
        if (null != state) {
            currentValues.restoreState(state, false);
            long id = currentValues.getId();
            if (ID_NEW == id || fromInitializedState) {
                termEntity.restoreState(state, fromInitializedState);
            } else {
                return dbLoader.getTermById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        } else {
            normalizedName = termEntity.getName();
        }
        titleFactoryLiveData.postValue(this::calculateViewTitle);
        entityLiveData.postValue(termEntity);
        return Single.just(termEntity).observeOn(AndroidSchedulers.mainThread());
    }


    @NonNull
    public synchronized String calculateViewTitle(Resources resources) {
        if (null == viewTitle) {
            String t = resources.getString(R.string.format_term, normalizedName);
            int i = t.indexOf(':');
            viewTitle = (i > 0 && normalizedName.startsWith(t.substring(0, i))) ? normalizedName : t;
        }
        return viewTitle;
    }

    @NonNull
    public synchronized Spanned calculateOverview(Resources resources) {
        if (null != overview) {
            return overview;
        }
        LocalDate startDate = currentValues.getStart();
        LocalDate endDate = currentValues.getEnd();
        Spanned result;
        if (null == startDate) {
            if (null == endDate) {
                result = Html.fromHtml(resources.getString(R.string.html_no_dates_specified), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            } else {
                result = new SpannedString(resources.getString(R.string.format_ends_on, FORMATTER.format(endDate)));
            }
        } else if (null != endDate) {
            result = new SpannedString(resources.getString(R.string.format_range_start_to_end, FORMATTER.format(startDate), FORMATTER.format(endDate)));
        } else {
            result = new SpannedString(resources.getString(R.string.format_starts_on, FORMATTER.format(startDate)));
        }
        overview = result;
        return result;
    }

    public void saveViewModelState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        termEntity.saveState(outState, true);
    }

    public synchronized Single<ValidationMessage.ResourceMessageResult> save(boolean ignoreWarnings) {
        LocalDate newStart = currentValues.start;
        LocalDate newEnd = currentValues.end;
        String originalName = termEntity.getName();
        LocalDate originalStart = termEntity.getStart();
        LocalDate originalEnd = termEntity.getEnd();
        String originalNotes = termEntity.getNotes();
        termEntity.setName(normalizedName);
        termEntity.setStart(newStart);
        termEntity.setEnd(newEnd);
        termEntity.setNotes(currentValues.notes);
        return dbLoader.saveTerm(termEntity, ignoreWarnings).doOnError(throwable -> {
            termEntity.setName(originalName);
            termEntity.setStart(originalStart);
            termEntity.setEnd(originalEnd);
            termEntity.setNotes(originalNotes);
        }).doOnSuccess(t -> {
            if (t.isError() || !(ignoreWarnings || t.isSucceeded())) {
                termEntity.setName(originalName);
                termEntity.setStart(originalStart);
                termEntity.setEnd(originalEnd);
                termEntity.setNotes(originalNotes);
            }
        });
    }

    public Single<ValidationMessage.ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteTerm(entityLiveData.getValue(), ignoreWarnings);
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

    private synchronized Optional<Integer> validateDateRange() {
        if (null != currentValues.end) {
            if (null == currentValues.start) {
                return Optional.of(R.string.message_required);
            }
            if (currentValues.start.compareTo(currentValues.end) > 0) {
                return Optional.of(R.string.message_start_after_end);
            }
        }
        return Optional.empty();
    }

    public boolean isChanged() {
        if (ID_NEW != termEntity.getId() && normalizedName.equals(termEntity.getName()) && Objects.equals(currentValues.start, termEntity.getStart()) && Objects.equals(currentValues.end, termEntity.getEnd())) {
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

        @Override
        public long getId() {
            return (null == termEntity) ? id : termEntity.getId();
        }

        @Override
        public void setId(long id) {
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
            viewTitle = null;
            titleFactoryLiveData.postValue(EditTermViewModel.this::calculateViewTitle);
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
                startMessageLiveData.postValue(validateDateRange().orElse(null));
                overview = null;
                overviewFactoryLiveData.postValue(EditTermViewModel.this::calculateOverview);
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
                startMessageLiveData.postValue(validateDateRange().orElse(null));
                overview = null;
                overviewFactoryLiveData.postValue(EditTermViewModel.this::calculateOverview);
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