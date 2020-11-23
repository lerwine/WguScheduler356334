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
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.MessageLevel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.CompletableSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditTermViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(EditTermViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_TERM_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_ID, false);

    public static void startAddTermActivity(@NonNull Fragment fragment, int requestCode, @NonNull LocalDate termStart) {
        Intent intent = new Intent(fragment.requireContext(), AddTermActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, TermEntity.COLNAME_START, false), LocalDateConverter.fromLocalDate(termStart));
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startViewTermActivity(Context context, long termId) {
        Intent intent = new Intent(context, ViewTermActivity.class);
        intent.putExtra(EXTRA_KEY_TERM_ID, termId);
        context.startActivity(intent);
    }

    @NonNull
    private static Function<Resources, CharSequence> getOverviewFactory(LocalDate startDate, LocalDate endDate) {
        return new Function<Resources, CharSequence>() {
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
    private static Function<Resources, CharSequence> getViewTitleFactory(String name) {
        return r -> {
            String t = r.getString(R.string.format_term, name);
            int i = t.indexOf(':');
            return (i > 0 && name.startsWith(t.substring(0, i))) ? name : t;
        };
    }

    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private final BehaviorComputationSource<TermEntity> originalValuesSource;
    private final BehaviorComputationSource<String> nameSource;
    private final BehaviorComputationSource<Optional<LocalDate>> startDateSource;
    private final BehaviorComputationSource<Optional<ResourceMessageFactory>> startMessageOverrideSource;
    private final BehaviorComputationSource<Optional<LocalDate>> endDateSource;
    private final BehaviorComputationSource<Optional<ResourceMessageFactory>> endMessageOverrideSource;
    private final BehaviorComputationSource<String> notesSource;
    private final CompletableSubject initializedSubject;
    private final Completable initializedCompletable;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> titleFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> overviewFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> nameValidObserver;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> startMessageObserver;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> endMessageObserver;
    private final SubscribingLiveDataWrapper<Boolean> canShareObserver;
    private final SubscribingLiveDataWrapper<Boolean> canSaveObserver;
    private final SubscribingLiveDataWrapper<Boolean> hasChangesObserver;
    private final SubscribingLiveDataWrapper<Boolean> isValidObserver;
    private final LiveDataWrapper<List<TermCourseListItem>> coursesLiveData;
    private boolean fromInitializedState;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        originalValuesSource = BehaviorComputationSource.createDefault(new TermEntity());
        nameSource = BehaviorComputationSource.createDefault("");
        startDateSource = BehaviorComputationSource.createDefault(Optional.empty());
        startMessageOverrideSource = BehaviorComputationSource.createDefault(Optional.empty());
        endDateSource = BehaviorComputationSource.createDefault(Optional.empty());
        endMessageOverrideSource = BehaviorComputationSource.createDefault(Optional.empty());
        notesSource = BehaviorComputationSource.createDefault("");
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());
        coursesLiveData = new LiveDataWrapper<>(Collections.emptyList());

        Observable<String> normalizedNameObservable = nameSource.getObservable().map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                originalValuesSource.getObservable(),
                normalizedNameObservable,
                startDateSource.getObservable(),
                endDateSource.getObservable(),
                notesSource.getObservable(),
                (term, name, startDate, endDate, notes) -> !(name.equals(term.getName()) && Objects.equals(startDate.orElse(null), term.getStart()) &&
                        Objects.equals(endDate.orElse(null), term.getEnd()) && notes.equals(term.getNotes()))
        );
        Observable<Optional<ResourceMessageFactory>> startMessageObservable = Observable.combineLatest(
                startMessageOverrideSource.getObservable(),
                startDateSource.getObservable(),
                endDateSource.getObservable(),
                (startMessageOverride, startDate, endDate) -> {
                    Log.d(LOG_TAG, "Calculating startMessage: startMessageOverride=" + startMessageOverride + "; startDate=" + startDate + "; endDate=" + endDate);
                    if (startMessageOverride.isPresent()) {
                        return startMessageOverride;
                    }
                    if (startDate.isPresent()) {
                        LocalDate sd = startDate.get();
                        if (endDate.isPresent() && sd.compareTo(endDate.get()) > 0) {
                            return Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end));
                        }
                        return Optional.empty();
                    }
                    if (endDate.isPresent()) {
                        return Optional.of(ResourceMessageFactory.ofError(R.string.message_required));
                    }
                    return Optional.of(ResourceMessageFactory.ofWarning(R.string.message_recommended));
                }
        );
        Observable<Optional<ResourceMessageFactory>> endMessageObservable = Observable.combineLatest(
                endMessageOverrideSource.getObservable(),
                startDateSource.getObservable(),
                endDateSource.getObservable(), (endMessageOverride, startDate, endDate) -> {
                    Log.d(LOG_TAG, "Calculating endMessage: endMessageOverride=" + endMessageOverride + "; startDate=" + startDate + "; endDate=" + endDate);
                    if (endMessageOverride.isPresent()) {
                        return endMessageOverride;
                    }
                    if (startDate.isPresent()) {
                        LocalDate sd = startDate.get();
                        if (endDate.isPresent()) {
                            if (sd.compareTo(endDate.get()) > 0) {
                                return Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end));
                            }
                            return Optional.empty();
                        }
                    } else if (endDate.isPresent()) {
                        return Optional.empty();
                    }
                    return Optional.of(ResourceMessageFactory.ofWarning(R.string.message_recommended));
                });
        Observable<Boolean> validObservable = Observable.combineLatest(nameValidObservable, startMessageObservable, endMessageObservable, (n, s, e) -> {
            Log.d(LOG_TAG, "Calculating valid: nameValid = " + n + "; startMessage = " + s + "; endMessage = " + e);
            return n && s.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true) && e.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true);
        });

        nameValidObserver = SubscribingLiveDataWrapper.of(false, nameValidObservable);
        startMessageObserver = SubscribingLiveDataWrapper.ofOptional(startMessageObservable);
        endMessageObserver = SubscribingLiveDataWrapper.ofOptional(endMessageObservable);
        hasChangesObserver = SubscribingLiveDataWrapper.of(false, hasChangesObservable);
        canShareObserver = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canShare: valid = " + v + "; hasChanges = " + c);
            return v && !c;
        }));
        canSaveObserver = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canSave: valid = " + v + "; hasChanges = " + c);
            return v && c;
        }));
        isValidObserver = SubscribingLiveDataWrapper.of(false, validObservable);
        titleFactoryObserver = SubscribingLiveDataWrapper.of(r -> "", normalizedNameObservable.map(EditTermViewModel::getViewTitleFactory));
        overviewFactoryObserver = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(startDateSource.getObservable(), endDateSource.getObservable(),
                (s, e) -> getOverviewFactory(s.orElse(null), e.orElse(null))));
        compositeDisposable = new CompositeDisposable(nameValidObserver, startMessageObserver, endMessageObserver, hasChangesObserver, canShareObserver, canSaveObserver,
                isValidObserver, titleFactoryObserver, overviewFactoryObserver);
    }

    public long getId() {
        return Objects.requireNonNull(originalValuesSource.getValue()).getId();
    }

    @NonNull
    public String getName() {
        return Objects.requireNonNull(nameSource.getValue());
    }

    public synchronized void setName(String value) {
        nameSource.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getStart() {
        return Objects.requireNonNull(startDateSource.getValue()).orElse(null);
    }

    public synchronized void setStart(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setStart(" + ToStringBuilder.toEscapedString(value, true) + ")");
        startMessageOverrideSource.onNext(Optional.empty());
        startDateSource.onNext(Optional.ofNullable(value));
    }

    public synchronized void setStart(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setStart(" + value + ")");
        startMessageOverrideSource.onNext(Optional.of(value));
        startDateSource.onNext(Optional.empty());
    }

    @Nullable
    public LocalDate getEnd() {
        return Objects.requireNonNull(endDateSource.getValue()).orElse(null);
    }

    public synchronized void setEnd(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setEnd(" + ToStringBuilder.toEscapedString(value, true) + ")");
        endMessageOverrideSource.onNext(Optional.empty());
        endDateSource.onNext(Optional.ofNullable(value));
    }

    public synchronized void setEnd(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setEnd(" + value + ")");
        endMessageOverrideSource.onNext(Optional.of(value));
        endDateSource.onNext(Optional.empty());
    }

    @NonNull
    public String getNotes() {
        return Objects.requireNonNull(notesSource.getValue());
    }

    public synchronized void setNotes(String value) {
        notesSource.onNext((null == value) ? "" : value);
    }

    public LiveData<Boolean> getNameValid() {
        return nameValidObserver.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getStartMessage() {
        return startMessageObserver.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getEndMessage() {
        return endMessageObserver.getLiveData();
    }

    public LiveData<Boolean> getCanSave() {
        return canSaveObserver.getLiveData();
    }

    public LiveData<Boolean> getCanShare() {
        return canShareObserver.getLiveData();
    }

    public LiveData<Boolean> getHasChanges() {
        return hasChangesObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getTitleFactory() {
        return titleFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getOverviewFactory() {
        return overviewFactoryObserver.getLiveData();
    }

    public Completable getInitializedCompletable() {
        return initializedCompletable;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public LiveData<List<TermCourseListItem>> getCoursesLiveData() {
        return coursesLiveData.getLiveData();
    }

    public Single<List<AlertListItem>> getAllAlerts() {
        long id = Objects.requireNonNull(originalValuesSource.getValue()).getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByTermId(id);
        }
        return Single.just(Collections.emptyList());
    }

    public LiveData<Boolean> getIsValid() {
        return isValidObserver.getLiveData();
    }

    public synchronized Single<TermEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        Log.d(LOG_TAG, "Enter initializeViewModelState");
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        TermEntity entity;
        if (null != state) {
            long id = state.getLong(EXTRA_KEY_TERM_ID, ID_NEW);
            if (ID_NEW == id) {
                coursesLiveData.postValue(Collections.emptyList());
            } else {
                ObserverHelper.observe(dbLoader.getCoursesLiveDataByTermId(id), this, coursesLiveData::postValue);
                if (!fromInitializedState) {
                    return dbLoader.getTermById(id)
                            .doOnSuccess(this::onEntityLoadedFromDb)
                            .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
                }
            }
            entity = new TermEntity();
            entity.restoreState(state, true);
            originalValuesSource.onNext(entity);
            nameSource.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, false), ""));
            String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_START, false);
            if (state.containsKey(key)) {
                startDateSource.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                startDateSource.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_END, false);
            if (state.containsKey(key)) {
                endDateSource.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                endDateSource.onNext(Optional.empty());
            }
            notesSource.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NOTES, false), ""));
        } else {
            entity = new TermEntity();
            originalValuesSource.onNext(entity);
            coursesLiveData.postValue(Collections.emptyList());
        }
        initializedSubject.onComplete();
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoadedFromDb(@NonNull TermEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoadedFromDb(" + ToStringBuilder.toEscapedString(entity, true) + ")");
        originalValuesSource.onNext(entity);
        nameSource.onNext(entity.getName());
        startDateSource.onNext(Optional.ofNullable(entity.getStart()));
        endDateSource.onNext(Optional.ofNullable(entity.getEnd()));
        notesSource.onNext(entity.getNotes());
        initializedSubject.onComplete();
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        Objects.requireNonNull(originalValuesSource.getValue()).saveState(outState, true);
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, false), nameSource.getValue());
        Objects.requireNonNull(startDateSource.getValue()).ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_START, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(endDateSource.getValue()).ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_END, false), LocalDateConverter.fromLocalDate(d)));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NOTES, false), notesSource.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter save(" + ToStringBuilder.toEscapedString(ignoreWarnings) + ")");
        LocalDate newStart = Objects.requireNonNull(startDateSource.getValue()).orElse(null);
        LocalDate newEnd = Objects.requireNonNull(endDateSource.getValue()).orElse(null);
        TermEntity originalValues = originalValuesSource.getValue();
        String originalName = Objects.requireNonNull(originalValues).getName();
        LocalDate originalStart = originalValues.getStart();
        LocalDate originalEnd = originalValues.getEnd();
        String originalNotes = originalValues.getNotes();
        originalValues.setName(nameSource.getValue());
        originalValues.setStart(newStart);
        originalValues.setEnd(newEnd);
        originalValues.setNotes(notesSource.getValue());
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
        return dbLoader.deleteTerm(originalValuesSource.getValue(), ignoreWarnings);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}