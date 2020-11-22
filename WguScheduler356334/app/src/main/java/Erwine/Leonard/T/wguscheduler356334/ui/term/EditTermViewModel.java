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

    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private final BehaviorComputationSource<TermEntity> entitySubject;
    private final BehaviorComputationSource<String> nameSubject;
    private final BehaviorComputationSource<Optional<LocalDate>> startDateSubject;
    private final BehaviorComputationSource<Optional<ResourceMessageFactory>> startMessageOverride;
    private final BehaviorComputationSource<Optional<LocalDate>> endDateSubject;
    private final BehaviorComputationSource<Optional<ResourceMessageFactory>> endMessageOverride;
    private final BehaviorComputationSource<String> notesSubject;
    private final CompletableSubject initializedSubject;
    private final Completable initializedCompletable;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> titleFactory;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> overviewFactory;
    private final SubscribingLiveDataWrapper<Boolean> nameValid;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> startMessage;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> endMessage;
    private final SubscribingLiveDataWrapper<Boolean> canShare;
    private final SubscribingLiveDataWrapper<Boolean> canSave;
    private final SubscribingLiveDataWrapper<Boolean> hasChanges;
    private final SubscribingLiveDataWrapper<Boolean> isValid;
    private final LiveDataWrapper<List<TermCourseListItem>> coursesLiveData;
    private boolean fromInitializedState;

    public EditTermViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        entitySubject = BehaviorComputationSource.createDefault(new TermEntity());
        nameSubject = BehaviorComputationSource.createDefault("");
        startDateSubject = BehaviorComputationSource.createDefault(Optional.empty());
        startMessageOverride = BehaviorComputationSource.createDefault(Optional.empty());
        endDateSubject = BehaviorComputationSource.createDefault(Optional.empty());
        endMessageOverride = BehaviorComputationSource.createDefault(Optional.empty());
        notesSubject = BehaviorComputationSource.createDefault("");
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());
        coursesLiveData = new LiveDataWrapper<>(Collections.emptyList());

        Observable<String> normalizedNameObservable = nameSubject.getObservable().map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                entitySubject.getObservable(),
                normalizedNameObservable,
                startDateSubject.getObservable(),
                endDateSubject.getObservable(),
                notesSubject.getObservable(),
                (term, name, startDate, endDate, notes) -> !(name.equals(term.getName()) && Objects.equals(startDate.orElse(null), term.getStart()) && Objects.equals(endDate.orElse(null), term.getEnd()) &&
                        notes.equals(term.getNotes()))
        );
        Observable<Optional<ResourceMessageFactory>> startMessageObservable = Observable.combineLatest(
                startMessageOverride.getObservable(),
                startDateSubject.getObservable(),
                endDateSubject.getObservable(),
                (o, s, e) -> {
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
                }
        );
        Observable<Optional<ResourceMessageFactory>> endMessageObservable = Observable.combineLatest(
                endMessageOverride.getObservable(),
                startDateSubject.getObservable(),
                endDateSubject.getObservable(), (o, s, e) -> {
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
                s.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true) && e.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true));

        nameValid = SubscribingLiveDataWrapper.of(false, nameValidObservable);
        startMessage = SubscribingLiveDataWrapper.ofOptional(startMessageObservable);
        endMessage = SubscribingLiveDataWrapper.ofOptional(endMessageObservable);
        hasChanges = SubscribingLiveDataWrapper.of(false, hasChangesObservable);
        canShare = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && !c));
        canSave = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && c));
        isValid = SubscribingLiveDataWrapper.of(false, validObservable);
        titleFactory = SubscribingLiveDataWrapper.of(r -> "", normalizedNameObservable.map(this::getViewTitleFactory));
        overviewFactory = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(startDateSubject.getObservable(), endDateSubject.getObservable(),
                (s, e) -> getOverviewFactory(s.orElse(null), e.orElse(null))));
        compositeDisposable = new CompositeDisposable(nameValid, startMessage, endMessage, hasChanges, canShare, canSave, isValid, titleFactory, overviewFactory);
    }

    @NonNull
    private Function<Resources, CharSequence> getOverviewFactory(LocalDate startDate, LocalDate endDate) {
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
    private synchronized Function<Resources, CharSequence> getViewTitleFactory(String name) {
        return r -> {
            String t = r.getString(R.string.format_term, name);
            int i = t.indexOf(':');
            return (i > 0 && name.startsWith(t.substring(0, i))) ? name : t;
        };
    }

    public long getId() {
        return Objects.requireNonNull(entitySubject.getValue()).getId();
    }

    @NonNull
    public String getName() {
        return Objects.requireNonNull(nameSubject.getValue());
    }

    public synchronized void setName(String value) {
        nameSubject.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getStart() {
        return Objects.requireNonNull(startDateSubject.getValue()).orElse(null);
    }

    public synchronized void setStart(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setStart(" + ToStringBuilder.toEscapedString(value, true) + ")");
        startMessageOverride.onNext(Optional.empty());
        startDateSubject.onNext(Optional.ofNullable(value));
    }

    public synchronized void setStart(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setStart(" + value + ")");
        startMessageOverride.onNext(Optional.of(value));
        startDateSubject.onNext(Optional.empty());
    }

    @Nullable
    public LocalDate getEnd() {
        return Objects.requireNonNull(endDateSubject.getValue()).orElse(null);
    }

    public synchronized void setEnd(@Nullable LocalDate value) {
        Log.d(LOG_TAG, "Enter setEnd(" + ToStringBuilder.toEscapedString(value, true) + ")");
        endMessageOverride.onNext(Optional.empty());
        endDateSubject.onNext(Optional.ofNullable(value));
    }

    public synchronized void setEnd(@NonNull ResourceMessageFactory value) {
        Log.d(LOG_TAG, "Enter setEnd(" + value + ")");
        endMessageOverride.onNext(Optional.of(value));
        endDateSubject.onNext(Optional.empty());
    }

    @NonNull
    public String getNotes() {
        return Objects.requireNonNull(notesSubject.getValue());
    }

    public synchronized void setNotes(String value) {
        notesSubject.onNext((null == value) ? "" : value);
    }

    public LiveData<Boolean> getNameValid() {
        return nameValid.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getStartMessage() {
        return startMessage.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getEndMessage() {
        return endMessage.getLiveData();
    }

    public LiveData<Boolean> getCanSave() {
        return canSave.getLiveData();
    }

    public LiveData<Boolean> getCanShare() {
        return canShare.getLiveData();
    }

    public LiveData<Boolean> getHasChanges() {
        return hasChanges.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getTitleFactory() {
        return titleFactory.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getOverviewFactory() {
        return overviewFactory.getLiveData();
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
        long id = Objects.requireNonNull(entitySubject.getValue()).getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByTermId(id);
        }
        return Single.just(Collections.emptyList());
    }

    public LiveData<Boolean> getIsValid() {
        return isValid.getLiveData();
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
            entitySubject.onNext(entity);
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
            entitySubject.onNext(entity);
            coursesLiveData.postValue(Collections.emptyList());
        }
        initializedSubject.onComplete();
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        Objects.requireNonNull(entitySubject.getValue()).saveState(outState, true);
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NAME, false), nameSubject.getValue());
        Objects.requireNonNull(startDateSubject.getValue()).ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_START, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(endDateSubject.getValue()).ifPresent(d -> outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_END, false), LocalDateConverter.fromLocalDate(d)));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_NOTES, false), notesSubject.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter save(" + ToStringBuilder.toEscapedString(ignoreWarnings) + ")");
        LocalDate newStart = Objects.requireNonNull(startDateSubject.getValue()).orElse(null);
        LocalDate newEnd = Objects.requireNonNull(endDateSubject.getValue()).orElse(null);
        TermEntity originalValues = entitySubject.getValue();
        String originalName = Objects.requireNonNull(originalValues).getName();
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
        entitySubject.onNext(entity);
        nameSubject.onNext(entity.getName());
        startDateSubject.onNext(Optional.ofNullable(entity.getStart()));
        endDateSubject.onNext(Optional.ofNullable(entity.getEnd()));
        notesSubject.onNext(entity.getNotes());
        initializedSubject.onComplete();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}