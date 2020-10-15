package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddTermActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewTermActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditTermViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditTermViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_TERM_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_ID, false);

    public static void startAddTermActivity(Fragment fragment, int requestCode, @NonNull LocalDate termStart) {
        Intent intent = new Intent(fragment.requireContext(), AddTermActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_START, false), LocalDateConverter.fromLocalDate(termStart));
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startViewTermActivity(Context context, long termId) {
        Intent intent = new Intent(context, ViewTermActivity.class);
        intent.putExtra(EXTRA_KEY_TERM_ID, termId);
        context.startActivity(intent);
    }

    private final DbLoader dbLoader;
    @SuppressWarnings("FieldCanBeLocal")
    private final CompositeDisposable compositeDisposable;
    private final BehaviorSubject<String> nameSubject;
    private final BehaviorSubject<Optional<LocalDate>> startDateSubject;
    private final BehaviorSubject<Optional<ResourceMessageFactory>> startMessageOverride;
    private final BehaviorSubject<Optional<LocalDate>> endDateSubject;
    private final BehaviorSubject<Optional<ResourceMessageFactory>> endMessageOverride;
    private final BehaviorSubject<String> notesSubject;
    private final PrivateLiveData<TermEntity> entity;
    private final PrivateLiveData<Function<Resources, String>> titleFactory;
    private final PrivateLiveData<Function<Resources, Spanned>> overviewFactory;
    private final PrivateLiveData<Boolean> nameValid;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> startMessage;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> endMessage;
    private final PrivateLiveData<Boolean> canShare;
    private final PrivateLiveData<Boolean> canSave;
    private final PrivateLiveData<Boolean> hasChanges;
    private final PrivateLiveData<Boolean> isValid;
    @NonNull
    private final CurrentValues currentValues;
    @NonNull
    private TermEntity originalValues;

    private LiveData<List<TermCourseListItem>> coursesLiveData;
    private boolean fromInitializedState;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        nameSubject = BehaviorSubject.create();
        startDateSubject = BehaviorSubject.create();
        startMessageOverride = BehaviorSubject.create();
        endDateSubject = BehaviorSubject.create();
        endMessageOverride = BehaviorSubject.create();
        notesSubject = BehaviorSubject.create();

        originalValues = new TermEntity();
        currentValues = new CurrentValues();
        nameSubject.onNext("");
        startDateSubject.onNext(Optional.empty());
        startMessageOverride.onNext(Optional.empty());
        endDateSubject.onNext(Optional.empty());
        endMessageOverride.onNext(Optional.empty());
        notesSubject.onNext("");

        entity = new PrivateLiveData<>();
        nameValid = new PrivateLiveData<>(false);
        startMessage = new PrivateLiveData<>(Optional.empty());
        endMessage = new PrivateLiveData<>(Optional.empty());
        hasChanges = new PrivateLiveData<>(false);
        canShare = new PrivateLiveData<>(false);
        canSave = new PrivateLiveData<>(false);
        isValid = new PrivateLiveData<>(false);
        titleFactory = new PrivateLiveData<>();
        overviewFactory = new PrivateLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }

    private void onHasChangesChanged(Boolean hasChanges) {
        boolean c = Boolean.TRUE.equals(hasChanges);
        boolean v = Boolean.TRUE.equals(isValid.getValue());
        this.hasChanges.postValue(c);
        canShare.postValue(v && !c);
        canSave.postValue(v && c);
    }

    private void onValidChanged(Boolean isValid) {
        boolean c = Boolean.TRUE.equals(hasChanges.getValue());
        boolean v = Boolean.TRUE.equals(isValid);
        this.isValid.postValue(v);
        canShare.postValue(v && !c);
        canSave.postValue(v && c);
    }

    private void onStartDateChanged(@Nullable LocalDate localDate) {
        onDateRangeChanged(localDate, endDateSubject.getValue().orElse(null));
    }

    private void onEndDateChanged(@Nullable LocalDate localDate) {
        onDateRangeChanged(startDateSubject.getValue().orElse(null), localDate);
    }

    private void onDateRangeChanged(@Nullable LocalDate s, @Nullable LocalDate e) {
        overviewFactory.postValue(getOverviewFactory(s, e));
    }

    @NonNull
    private Function<Resources, Spanned> getOverviewFactory(LocalDate startDate, LocalDate endDate) {
        return new Function<Resources, Spanned>() {
            Spanned result;
            Resources resources;

            @Override
            public Spanned apply(Resources resources) {
                if (null != result && Objects.equals(resources, this.resources)) {
                    return result;
                }
                this.resources = resources;
                if (null == startDate) {
                    if (null == endDate) {
                        result = Html.fromHtml(resources.getString(R.string.html_no_dates_specified), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                    } else {
                        result = new SpannedString(resources.getString(R.string.format_ends_on, FULL_FORMATTER.format(endDate)));
                    }
                } else if (null != endDate) {
                    result = new SpannedString(resources.getString(R.string.format_range_start_to_end, FULL_FORMATTER.format(startDate), FULL_FORMATTER.format(endDate)));
                } else {
                    result = new SpannedString(resources.getString(R.string.format_starts_on, FULL_FORMATTER.format(startDate)));
                }
                return result;
            }
        };
    }

    @NonNull
    private synchronized Function<Resources, String> getViewTitleFactory(String name) {
        return r -> {
            String t = r.getString(R.string.format_term, name);
            int i = t.indexOf(':');
            return (i > 0 && name.startsWith(t.substring(0, i))) ? name : t;
        };
    }

    public long getId() {
        return originalValues.getId();
    }

    @NonNull
    public String getName() {
        return currentValues.getName();
    }

    public synchronized void setName(String value) {
        currentValues.setName(value);
    }

    @Nullable
    public LocalDate getStart() {
        return currentValues.getStart();
    }

    public synchronized void setStart(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setStart(" + ToStringBuilder.toEscapedString(value, true) + ")");
        startMessageOverride.onNext(Optional.empty());
        currentValues.setStart(value);
    }

    public synchronized void setStart(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setStart(" + value + ")");
        startMessageOverride.onNext(Optional.of(value));
        currentValues.setStart(null);
    }

    @Nullable
    public LocalDate getEnd() {
        return currentValues.getEnd();
    }

    public synchronized void setEnd(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setEnd(" + ToStringBuilder.toEscapedString(value, true) + ")");
        endMessageOverride.onNext(Optional.empty());
        currentValues.setEnd(value);
    }

    public synchronized void setEnd(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setEnd(" + value + ")");
        endMessageOverride.onNext(Optional.of(value));
        currentValues.setEnd(null);
    }

    @NonNull
    public String getNotes() {
        return currentValues.getNotes();
    }

    public synchronized void setNotes(String value) {
        currentValues.setNotes(value);
    }

    public LiveData<Boolean> getNameValid() {
        return nameValid;
    }

    public LiveData<Optional<ResourceMessageFactory>> getStartMessage() {
        return startMessage;
    }

    public LiveData<Optional<ResourceMessageFactory>> getEndMessage() {
        return endMessage;
    }

    public LiveData<Boolean> getCanSave() {
        return canSave;
    }

    public LiveData<Boolean> getCanShare() {
        return canShare;
    }

    public LiveData<Boolean> getHasChanges() {
        return hasChanges;
    }

    public LiveData<Function<Resources, String>> getTitleFactory() {
        return titleFactory;
    }

    public LiveData<Function<Resources, Spanned>> getOverviewFactory() {
        return overviewFactory;
    }

    public LiveData<TermEntity> getEntity() {
        return entity;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public LiveData<List<TermCourseListItem>> getCoursesLiveData() {
        return coursesLiveData;
    }

    public LiveData<List<AlertListItem>> getAllAlerts() {
        long id = originalValues.getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByTermId(id);
        }
        MutableLiveData<List<AlertListItem>> result = new MutableLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
    }

    public LiveData<Boolean> getIsValid() {
        return isValid;
    }

    public synchronized Single<TermEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter initializeViewModelState");
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null != state) {
            currentValues.restoreState(state, false);
            long id = currentValues.getId();
            if (ID_NEW == id || fromInitializedState) {
                originalValues.restoreState(state, fromInitializedState);
            } else {
                return dbLoader.getTermById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        }
        coursesLiveData = new MutableLiveData<>(Collections.emptyList());
        onEntityLoaded(originalValues);
        return Single.just(originalValues).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        originalValues.saveState(outState, true);
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter save(" + ToStringBuilder.toEscapedString(ignoreWarnings) + ")");
        LocalDate newStart = currentValues.start;
        LocalDate newEnd = currentValues.end;
        String originalName = originalValues.getName();
        LocalDate originalStart = originalValues.getStart();
        LocalDate originalEnd = originalValues.getEnd();
        String originalNotes = originalValues.getNotes();
        originalValues.setName(currentValues.name);
        originalValues.setStart(newStart);
        originalValues.setEnd(newEnd);
        originalValues.setNotes(currentValues.notes);
        return dbLoader.saveTerm(originalValues, ignoreWarnings).doOnError(throwable -> {
            originalValues.setName(originalName);
            originalValues.setStart(originalStart);
            originalValues.setEnd(originalEnd);
            originalValues.setNotes(originalNotes);
        }).doOnSuccess(t -> {
            if (t.isError() || !(ignoreWarnings || t.isSucceeded())) {
                originalValues.setName(originalName);
                originalValues.setStart(originalStart);
                originalValues.setEnd(originalEnd);
                originalValues.setNotes(originalNotes);
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete(" + ToStringBuilder.toEscapedString(ignoreWarnings) + ")");
        return dbLoader.deleteTerm(originalValues, ignoreWarnings);
    }

    private void onEntityLoadedFromDb(@NonNull TermEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoadedFromDb(" + ToStringBuilder.toEscapedString(entity, true) + ")");
        originalValues = entity;
        setName(entity.getName());
        setStart(entity.getStart());
        setEnd(entity.getEnd());
        setNotes(entity.getNotes());
        coursesLiveData = dbLoader.getCoursesByTermId(entity.getId());
        onEntityLoaded(originalValues);
    }

    private void onEntityLoaded(TermEntity termEntity) {
        compositeDisposable.clear();

        entity.postValue(termEntity);
        Observable<String> normalizedNameObservable = nameSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                normalizedNameObservable.map(n -> !n.equals(termEntity.getName())),
                startDateSubject.map(o -> !Objects.equals(o.orElse(null), termEntity.getStart())),
                endDateSubject.map(o -> !Objects.equals(o.orElse(null), termEntity.getEnd())),
                notesSubject.map(n -> !AbstractNotedEntity.MULTI_LINE_NORMALIZER.apply(n).equals(termEntity.getNotes())),
                (n, p, e, s) -> n || p || e || s
        );
        Observable<Optional<ResourceMessageFactory>> startMessageObservable = Observable.combineLatest(startMessageOverride, startDateSubject, endDateSubject, (o, s, e) -> {
            if (o.isPresent()) {
                Log.d(LOG_TAG, "startMessageObservable: startMessageOverride.isPresent()=true");
                return o;
            }
            if (s.isPresent()) {
                LocalDate sd = s.get();
                if (e.isPresent()) {
                    LocalDate ed = e.get();
                    Log.d(LOG_TAG, "startMessageObservable: startDateSubject=" + ToStringBuilder.toEscapedString(sd, true) +
                            "; endDateSubject=" + ToStringBuilder.toEscapedString(ed, true) + "; startMessageOverride=EMPTY");
                    if (sd.compareTo(ed) > 0) {
                        return Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end));
                    }
                } else {
                    Log.d(LOG_TAG, "startMessageObservable: startDateSubject=" + ToStringBuilder.toEscapedString(sd, true) +
                            "; endDateSubject=EMPTY; startMessageOverride=EMPTY");
                }
                return Optional.empty();
            }
            if (e.isPresent()) {
                Log.d(LOG_TAG, "startMessageObservable: startDateSubject=EMPTY; endDateSubject=" + ToStringBuilder.toEscapedString(e.get(), true) +
                        "; startMessageOverride=EMPTY");
                return Optional.of(ResourceMessageFactory.ofError(R.string.message_required));
            }
            Log.d(LOG_TAG, "startMessageObservable: startDateSubject=EMPTY; endDateSubject=EMPTY; startMessageOverride=EMPTY");
            return Optional.of(ResourceMessageFactory.ofWarning(R.string.message_recommended));
        });
        Observable<Optional<ResourceMessageFactory>> endMessageObservable = Observable.combineLatest(endMessageOverride, startDateSubject, endDateSubject, (o, s, e) -> {
            if (o.isPresent()) {
                Log.d(LOG_TAG, "endMessageObservable: endMessageOverride.isPresent()=true");
                return o;
            }
            if (s.isPresent()) {
                LocalDate sd = s.get();
                if (e.isPresent()) {
                    LocalDate ed = e.get();
                    Log.d(LOG_TAG, "endMessageObservable: startDateSubject=" + ToStringBuilder.toEscapedString(sd, true) +
                            "; endDateSubject=" + ToStringBuilder.toEscapedString(ed, true) + "; endMessageOverride=EMPTY");
                    if (sd.compareTo(ed) > 0) {
                        return Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end));
                    }
                    return Optional.empty();
                } else {
                    Log.d(LOG_TAG, "endMessageObservable: startDateSubject=" + ToStringBuilder.toEscapedString(sd, true) +
                            "; endDateSubject=EMPTY; endMessageOverride=EMPTY");
                }
            } else if (e.isPresent()) {
                Log.d(LOG_TAG, "endMessageObservable: startDateSubject=EMPTY; endDateSubject=" + ToStringBuilder.toEscapedString(e.get(), true) +
                        "; endMessageOverride=EMPTY");
                return Optional.empty();
            }
            Log.d(LOG_TAG, "endMessageObservable: startDateSubject=EMPTY; endDateSubject=EMPTY; endMessageOverride=EMPTY");
            return Optional.of(ResourceMessageFactory.ofWarning(R.string.message_recommended));
        });
        Observable<Boolean> validObservable = Observable.combineLatest(nameValidObservable, startMessageObservable, endMessageObservable, (n, s, e) -> n && !(s.isPresent() || e.isPresent()));

        compositeDisposable.add(validObservable.subscribe(this::onValidChanged));
        compositeDisposable.add(nameValidObservable.subscribe(nameValid::postValue));
        compositeDisposable.add(normalizedNameObservable.subscribe(t -> titleFactory.postValue(getViewTitleFactory(t))));
        compositeDisposable.add(startDateSubject.subscribe(t -> onStartDateChanged(t.orElse(null))));
        compositeDisposable.add(endDateSubject.subscribe(t -> onEndDateChanged(t.orElse(null))));
        compositeDisposable.add(hasChangesObservable.subscribe(this::onHasChangesChanged));
        compositeDisposable.add(startMessageObservable.subscribe(startMessage::postValue));
        compositeDisposable.add(endMessageObservable.subscribe(endMessage::postValue));

    }

    private static class PrivateLiveData<T> extends LiveData<T> {
        PrivateLiveData(T value) {
            super(value);
        }

        PrivateLiveData() {
        }

        @Override
        public void postValue(T value) {
            super.postValue(value);
        }

        @Override
        public void setValue(T value) {
            super.setValue(value);
        }
    }

    private class CurrentValues implements Term {

        @NonNull
        private String name = "";
        private LocalDate start;
        private LocalDate end;
        @NonNull
        private String notes = "";

        @Override
        public long getId() {
            return originalValues.getId();
        }

        @Override
        public void setId(long id) {
            originalValues.setId(id);
        }

        @NonNull
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            Log.d(LOG_TAG, "Enter CurrentValues.setNotes(" + ToStringBuilder.toEscapedString(name) + "); oldValue = " + ToStringBuilder.toEscapedString(this.name));
            if (null == name || name.isEmpty()) {
                if (this.name.isEmpty()) {
                    return;
                }
                nameSubject.onNext("");
            } else if (!name.equals(this.name)) {
                this.name = name;
                nameSubject.onNext(this.name);
            }
            nameSubject.onNext(this.name);
        }

        @Nullable
        @Override
        public LocalDate getStart() {
            return start;
        }

        @Override
        public void setStart(@Nullable LocalDate start) {
            if (!Objects.equals(this.start, start)) {
                Log.d(LOG_TAG, "Enter CurrentValues.setStart(" + ToStringBuilder.toEscapedString(start, true) + "); oldValue = " + ToStringBuilder.toEscapedString(this.start, true));
                this.start = start;
                startDateSubject.onNext(Optional.ofNullable(this.start));
            }
        }

        @Nullable
        @Override
        public LocalDate getEnd() {
            return end;
        }

        @Override
        public void setEnd(@Nullable LocalDate end) {
            if (!Objects.equals(this.end, end)) {
                Log.d(LOG_TAG, "Enter CurrentValues.setEnd(" + ToStringBuilder.toEscapedString(end, true) + "); oldValue = " + ToStringBuilder.toEscapedString(this.end, true));
                this.end = end;
                endDateSubject.onNext(Optional.ofNullable(this.end));
            }
        }

        @NonNull
        @Override
        public String getNotes() {
            return notes;
        }

        @Override
        public void setNotes(String notes) {
            Log.d(LOG_TAG, "Enter CurrentValues.setNotes(" + ToStringBuilder.toEscapedString(notes) + "); oldValue = " + ToStringBuilder.toEscapedString(EditTermViewModel.this.getNotes()));
            if (null == notes || notes.isEmpty()) {
                if (this.notes.isEmpty()) {
                    return;
                }
                notesSubject.onNext("");
            } else if (!notes.equals(this.notes)) {
                this.notes = notes;
                notesSubject.onNext(this.notes);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}