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
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewTermActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditTermViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(EditTermViewModel.class);
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
    private final CompositeDisposable compositeDisposable;
    private final BehaviorSubject<TermEntity> entitySubject;
    private final BehaviorSubject<String> nameSubject;
    private final BehaviorSubject<Optional<LocalDate>> startDateSubject;
    private final BehaviorSubject<Optional<ResourceMessageFactory>> startMessageOverride;
    private final BehaviorSubject<Optional<LocalDate>> endDateSubject;
    private final BehaviorSubject<Optional<ResourceMessageFactory>> endMessageOverride;
    private final BehaviorSubject<String> notesSubject;
    private final PrivateLiveData<TermEntity> entityLiveData;
    private final PrivateLiveData<Function<Resources, String>> titleFactory;
    private final PrivateLiveData<Function<Resources, Spanned>> overviewFactory;
    private final PrivateLiveData<Boolean> nameValid;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> startMessage;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> endMessage;
    private final PrivateLiveData<Boolean> canShare;
    private final PrivateLiveData<Boolean> canSave;
    private final PrivateLiveData<Boolean> hasChanges;
    private final PrivateLiveData<Boolean> isValid;

    private LiveData<List<TermCourseListItem>> coursesLiveData;
    private boolean fromInitializedState;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        entitySubject = BehaviorSubject.createDefault(new TermEntity());
        nameSubject = BehaviorSubject.createDefault("");
        startDateSubject = BehaviorSubject.createDefault(Optional.empty());
        startMessageOverride = BehaviorSubject.createDefault(Optional.empty());
        endDateSubject = BehaviorSubject.createDefault(Optional.empty());
        endMessageOverride = BehaviorSubject.createDefault(Optional.empty());
        notesSubject = BehaviorSubject.createDefault("");
        compositeDisposable = new CompositeDisposable();
        entityLiveData = new PrivateLiveData<>();
        nameValid = new PrivateLiveData<>(false);
        startMessage = new PrivateLiveData<>(Optional.empty());
        endMessage = new PrivateLiveData<>(Optional.empty());
        hasChanges = new PrivateLiveData<>(false);
        canShare = new PrivateLiveData<>(false);
        canSave = new PrivateLiveData<>(false);
        isValid = new PrivateLiveData<>(false);
        titleFactory = new PrivateLiveData<>();
        overviewFactory = new PrivateLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    private void initializeObservables(TermEntity entity) {
        entitySubject.onNext(entity);

        Observable<String> normalizedNameObservable = nameSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                entitySubject,
                normalizedNameObservable,
                startDateSubject,
                endDateSubject,
                notesSubject,
                (term, name, startDate, endDate, notes) -> !(name.equals(term.getName()) && Objects.equals(startDate.orElse(null), term.getStart()) && Objects.equals(endDate.orElse(null), term.getEnd()) &&
                        notes.equals(term.getNotes()))
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
        Observable<Boolean> validObservable = Observable.combineLatest(nameValidObservable, startMessageObservable, endMessageObservable, (n, s, e) -> n &&
                s.map(ResourceMessageFactory::isWarning).orElse(true) && e.map(ResourceMessageFactory::isWarning).orElse(true));

        compositeDisposable.add(validObservable.subscribe(isValid::postValue));
        compositeDisposable.add(Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && c).subscribe(canSave::postValue));
        compositeDisposable.add(Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && !c).subscribe(canShare::postValue));
        compositeDisposable.add(nameValidObservable.subscribe(nameValid::postValue));
        compositeDisposable.add(normalizedNameObservable.subscribe(t -> titleFactory.postValue(getViewTitleFactory(t))));
        compositeDisposable.add(hasChangesObservable.subscribe(hasChanges::postValue));
        compositeDisposable.add(startMessageObservable.subscribe(startMessage::postValue));
        compositeDisposable.add(endMessageObservable.subscribe(endMessage::postValue));
        compositeDisposable.add(Observable.combineLatest(startDateSubject, endDateSubject, (s, e) -> getOverviewFactory(s.orElse(null), e.orElse(null)))
                .subscribe(overviewFactory::postValue));
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
        return entitySubject.getValue().getId();
    }

    @NonNull
    public String getName() {
        return nameSubject.getValue();
    }

    public synchronized void setName(String value) {
        nameSubject.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getStart() {
        return startDateSubject.getValue().orElse(null);
    }

    public synchronized void setStart(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setStart(" + ToStringBuilder.toEscapedString(value, true) + ")");
        // TODO: Use validation observables instead
        startMessageOverride.onNext(Optional.empty());
        startDateSubject.onNext(Optional.ofNullable(value));
    }

    // TODO: Use validation observables instead
    public synchronized void setStart(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setStart(" + value + ")");
        startMessageOverride.onNext(Optional.of(value));
        startDateSubject.onNext(Optional.empty());
    }

    @Nullable
    public LocalDate getEnd() {
        return endDateSubject.getValue().orElse(null);
    }

    public synchronized void setEnd(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setEnd(" + ToStringBuilder.toEscapedString(value, true) + ")");
        // TODO: Use validation observables instead
        endMessageOverride.onNext(Optional.empty());
        endDateSubject.onNext(Optional.ofNullable(value));
    }

    // TODO: Use validation observables instead
    public synchronized void setEnd(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setEnd(" + value + ")");
        endMessageOverride.onNext(Optional.of(value));
        endDateSubject.onNext(Optional.empty());
    }

    @NonNull
    public String getNotes() {
        return notesSubject.getValue();
    }

    public synchronized void setNotes(String value) {
        notesSubject.onNext((null == value) ? "" : value);
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

    public LiveData<TermEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public LiveData<List<TermCourseListItem>> getCoursesLiveData() {
        return coursesLiveData;
    }

    public LiveData<List<AlertListItem>> getAllAlerts() {
        long id = entitySubject.getValue().getId();
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
        TermEntity entity;
        if (null != state) {
            long id = state.getLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, false), ID_NEW);
            if (ID_NEW == id) {
                coursesLiveData = new MutableLiveData<>(Collections.emptyList());
            } else if (fromInitializedState) {
                coursesLiveData = dbLoader.getCoursesByTermId(id);
            } else {
                return dbLoader.getTermById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
            entity = new TermEntity();
            entity.restoreState(state, true);
            initializeObservables(entity);
            nameSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, false), ""));
            String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_START, false);
            if (state.containsKey(key)) {
                startDateSubject.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                startDateSubject.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_END, false);
            if (state.containsKey(key)) {
                endDateSubject.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                endDateSubject.onNext(Optional.empty());
            }
            notesSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NOTES, false), ""));
        } else {
            entity = new TermEntity();
            initializeObservables(entity);
            coursesLiveData = new MutableLiveData<>(Collections.emptyList());
        }
        entityLiveData.postValue(entity);
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        entitySubject.getValue().saveState(outState, true);
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, false), nameSubject.getValue());
        startDateSubject.getValue().ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_START, false), LocalDateConverter.fromLocalDate(d)));
        endDateSubject.getValue().ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_END, false), LocalDateConverter.fromLocalDate(d)));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NOTES, false), notesSubject.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter save(" + ToStringBuilder.toEscapedString(ignoreWarnings) + ")");
        LocalDate newStart = startDateSubject.getValue().orElse(null);
        LocalDate newEnd = endDateSubject.getValue().orElse(null);
        TermEntity originalValues = entitySubject.getValue();
        String originalName = originalValues.getName();
        LocalDate originalStart = originalValues.getStart();
        LocalDate originalEnd = originalValues.getEnd();
        String originalNotes = originalValues.getNotes();
        originalValues.setName(nameSubject.getValue());
        originalValues.setStart(newStart);
        originalValues.setEnd(newEnd);
        originalValues.setNotes(notesSubject.getValue());
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
        return dbLoader.deleteTerm(entitySubject.getValue(), ignoreWarnings);
    }

    private void onEntityLoadedFromDb(@NonNull TermEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoadedFromDb(" + ToStringBuilder.toEscapedString(entity, true) + ")");
        initializeObservables(entity);
        nameSubject.onNext(entity.getName());
        startDateSubject.onNext(Optional.ofNullable(entity.getStart()));
        endDateSubject.onNext(Optional.ofNullable(entity.getEnd()));
        notesSubject.onNext(entity.getNotes());
        coursesLiveData = dbLoader.getCoursesByTermId(entity.getId());
        entityLiveData.postValue(entity);
    }

    private static class PrivateLiveData<T> extends LiveData<T> {
        PrivateLiveData(T value) {
            super(value);
        }

        PrivateLiveData() {
        }

        @Override
        protected void postValue(T value) {
            super.postValue(value);
        }
    }

}