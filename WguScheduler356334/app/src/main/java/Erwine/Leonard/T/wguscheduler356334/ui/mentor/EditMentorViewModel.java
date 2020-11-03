package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import Erwine.Leonard.T.wguscheduler356334.EditMentorActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.MessageLevel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(EditMentorViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    private static final String STATE_KEY_SHARING_DISABLED_NOTIFICATION_DISMISSED = "sharing_disabled_notification_dismissed";
    private static final Predicate<String> EMAIL_PREDICATE = Pattern.compile("^[^@]+@[^.]+\\.[^.]+.*$").asPredicate();
    private static final Predicate<String> PHONE_PREDICATE = Pattern.compile("^([0-9+]|\\(\\d{1,3}\\))[0-9\\-. ]{3,15}").asPredicate();

    public static void startEditMentorActivity(Context context, Long id) {
        Intent intent = new Intent(context, EditMentorActivity.class);
        if (null != id) {
            intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, false), id);
        }
        context.startActivity(intent);
    }

    private final DbLoader dbLoader;
    private final BehaviorComputationSource<MentorEntity> originalValuesSubject;
    private final BehaviorComputationSource<Boolean> sharingDisabledNotificationDismissedSubject;
    private final BehaviorComputationSource<String> nameSubject;
    private final BehaviorComputationSource<String> phoneNumberSubject;
    private final BehaviorComputationSource<String> emailAddressSubject;
    private final BehaviorComputationSource<String> notesSubject;
    private final Observable<String> normalizedNameObservable;
    private final SubscribingLiveDataWrapper<Boolean> canShareLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canSaveLiveData;
    private final SubscribingLiveDataWrapper<Boolean> sharingDisabledNotificationVisibleLiveData;
    private final SubscribingLiveDataWrapper<String> normalizedNameLiveData;
    private final SubscribingLiveDataWrapper<Boolean> nameValidLiveData;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> emailValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<ResourceMessageFactory> phoneValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Boolean> isValidLiveData;
    private final SubscribingLiveDataWrapper<Boolean> hasChangesLiveData;
    private final LiveDataWrapper<Function<Resources, String>> titleFactory;
    private final SubscribingLiveDataWrapper<Function<Resources, Spanned>> overviewFactory;
    @SuppressWarnings("FieldCanBeLocal")
    private final CompositeDisposable compositeDisposable;
    private LiveData<List<MentorCourseListItem>> coursesLiveData;
    private boolean fromInitializedState;

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        originalValuesSubject = BehaviorComputationSource.createDefault(new MentorEntity());
        nameSubject = BehaviorComputationSource.createDefault("");
        phoneNumberSubject = BehaviorComputationSource.createDefault("");
        emailAddressSubject = BehaviorComputationSource.createDefault("");
        notesSubject = BehaviorComputationSource.createDefault("");
        sharingDisabledNotificationDismissedSubject = BehaviorComputationSource.createDefault(false);

        normalizedNameObservable = nameSubject.getObservable().map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<String> normalizedPhoneObservable = phoneNumberSubject.getObservable().map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<String> normalizedEmailObservable = emailAddressSubject.getObservable().map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Optional<ResourceMessageFactory>> phoneValidationMessageObservable = Observable.combineLatest(normalizedPhoneObservable, normalizedEmailObservable,
                (phone, email) -> {
                    if (phone.isEmpty()) {
                        return (email.isEmpty()) ? Optional.of(ResourceMessageFactory.ofError(R.string.message_phone_or_email_required)) : Optional.empty();
                    }
                    return (PHONE_PREDICATE.test(phone)) ? Optional.empty() : Optional.of(ResourceMessageFactory.ofError(R.string.message_invalid_phone_number));
                });
        Observable<Optional<ResourceMessageFactory>> emailValidationMessageObservable = Observable.combineLatest(normalizedPhoneObservable, normalizedEmailObservable,
                (phone, email) -> {
                    if (email.isEmpty()) {
                        return (phone.isEmpty()) ? Optional.of(ResourceMessageFactory.ofError(R.string.message_phone_or_email_required)) : Optional.empty();
                    }
                    return (EMAIL_PREDICATE.test(email)) ? Optional.empty() : Optional.of(ResourceMessageFactory.ofError(R.string.message_invalid_email_address));
                });

        Observable<Boolean> hasChangesObservable = Observable.combineLatest(originalValuesSubject.getObservable(), normalizedNameObservable, normalizedPhoneObservable,
                normalizedEmailObservable, notesSubject.getObservable().map(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply),
                (o, n, p, e, s) -> n.equals(o.getName()) && p.equals(o.getPhoneNumber()) && e.equals(o.getEmailAddress()) && n.equals(o.getNotes()));
        Observable<Boolean> validObservable = Observable.combineLatest(nameValidObservable, phoneValidationMessageObservable, emailValidationMessageObservable,
                (nameValid, phoneValidationMessage, emailValidationMessage) -> nameValid && phoneValidationMessage.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true) &&
                        emailValidationMessage.map(f -> f.getLevel() != MessageLevel.ERROR).orElse(true));

        hasChangesLiveData = SubscribingLiveDataWrapper.of(false, hasChangesObservable);
        canShareLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && !c));
        canSaveLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && c));
        isValidLiveData = SubscribingLiveDataWrapper.of(false, validObservable);
        nameValidLiveData = SubscribingLiveDataWrapper.of(false, nameValidObservable);
        sharingDisabledNotificationVisibleLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(hasChangesObservable, validObservable,
                sharingDisabledNotificationDismissedSubject.getObservable(), (c, v, d) -> v && c && !d));
        normalizedNameLiveData = SubscribingLiveDataWrapper.of("", normalizedNameObservable);
        titleFactory = new LiveDataWrapper<>(r -> "");
        overviewFactory = SubscribingLiveDataWrapper.of(r -> new SpannableString(" "), Observable.combineLatest(normalizedPhoneObservable, normalizedEmailObservable, this::getOverviewFactory));
        emailValidationMessageLiveData = SubscribingLiveDataWrapper.ofOptional(emailValidationMessageObservable);
        phoneValidationMessageLiveData = SubscribingLiveDataWrapper.ofOptional(phoneValidationMessageObservable);
        compositeDisposable = new CompositeDisposable(hasChangesLiveData, canShareLiveData, canSaveLiveData, isValidLiveData, nameValidLiveData,
                sharingDisabledNotificationVisibleLiveData, normalizedNameLiveData, overviewFactory, emailValidationMessageLiveData, phoneValidationMessageLiveData);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    @NonNull
    private Function<Resources, Spanned> getOverviewFactory(String phoneNumber, String emailAddress) {
        return new Function<Resources, Spanned>() {
            Spanned result;
            Resources resources;

            @Override
            public Spanned apply(Resources resources) {
                if (null != result && Objects.equals(resources, this.resources)) {
                    return result;
                }
                this.resources = resources;
                if (phoneNumber.isEmpty()) {
                    if (emailAddress.isEmpty()) {
                        result = new SpannableStringBuilder().append(resources.getString(R.string.message_phone_or_email_required),
                                new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        result = Html.fromHtml(resources.getString(R.string.html_format_mentor_email, emailAddress), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                    }
                } else if (emailAddress.isEmpty()) {
                    result = Html.fromHtml(resources.getString(R.string.html_format_mentor_phone, phoneNumber), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                } else {
                    result = Html.fromHtml(resources.getString(R.string.html_format_mentor_phone_and_email, phoneNumber, emailAddress), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                }
                return result;
            }
        };
    }

    public long getId() {
        MentorEntity mentorEntity = originalValuesSubject.getValue();
        return (null == mentorEntity) ? ID_NEW : mentorEntity.getId();
    }

    public String getName() {
        return nameSubject.getValue();
    }

    public synchronized void setName(String value) {
        nameSubject.onNext(value);
    }

    public String getPhoneNumber() {
        return phoneNumberSubject.getValue();
    }

    public synchronized void setPhoneNumber(String value) {
        phoneNumberSubject.onNext(value);
    }

    public String getEmailAddress() {
        return emailAddressSubject.getValue();
    }

    public synchronized void setEmailAddress(String value) {
        emailAddressSubject.onNext(value);
    }

    public String getNotes() {
        return notesSubject.getValue();
    }

    public synchronized void setNotes(String value) {
        notesSubject.onNext(value);
    }

    public void setSharingDisabledNotificationDismissed(boolean value) {
        sharingDisabledNotificationDismissedSubject.onNext(value);
    }

    public LiveData<Boolean> getSharingDisabledNotificationVisibleLiveData() {
        return sharingDisabledNotificationVisibleLiveData.getLiveData();
    }

    public LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getPhoneValidationMessageLiveData() {
        return phoneValidationMessageLiveData.getLiveData();
    }

    public LiveData<ResourceMessageFactory> getEmailValidationMessageLiveData() {
        return emailValidationMessageLiveData.getLiveData();
    }

    public LiveData<Function<Resources, String>> getTitleFactory() {
        return titleFactory.getLiveData();
    }

    public LiveData<Function<Resources, Spanned>> getOverviewFactory() {
        return overviewFactory.getLiveData();
    }

    public LiveData<String> getNormalizedNameLiveData() {
        return normalizedNameLiveData.getLiveData();
    }

    public LiveData<Boolean> getIsValidLiveData() {
        return isValidLiveData.getLiveData();
    }

    public LiveData<Boolean> getCanShareLiveData() {
        return canShareLiveData.getLiveData();
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData.getLiveData();
    }

    public LiveData<Boolean> getHasChangesLiveData() {
        return hasChangesLiveData.getLiveData();
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    @NonNull
    public synchronized Single<MentorEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        MentorEntity mentorEntity = new MentorEntity();
        if (null != state) {
            sharingDisabledNotificationDismissedSubject.onNext(state.getBoolean(STATE_KEY_SHARING_DISABLED_NOTIFICATION_DISMISSED, false));
            long id = state.getLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, true), ID_NEW);
            if (ID_NEW == id || fromInitializedState) {
                nameSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NAME, false), ""));
                phoneNumberSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_PHONE_NUMBER, false), ""));
                emailAddressSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_EMAIL_ADDRESS, false), ""));
                notesSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NOTES, false), ""));
                mentorEntity.restoreState(state, true);
            } else {
                return dbLoader.getMentorById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
            }
        }
        originalValuesSubject.onNext(mentorEntity);
        if (mentorEntity.getId() == ID_NEW) {
            titleFactory.postValue(r -> r.getString(R.string.title_activity_new_mentor));
        } else {
            compositeDisposable.add(normalizedNameObservable.subscribe(n ->
                    titleFactory.postValue(r -> r.getString(R.string.format_mentor, n))));
        }
        coursesLiveData = new LiveData<List<MentorCourseListItem>>(Collections.emptyList()) {
        };
        return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        MentorEntity mentorEntity = new MentorEntity(originalValuesSubject.getValue());
        long oldId = mentorEntity.getId();
        mentorEntity.setName(nameSubject.getValue());
        mentorEntity.setPhoneNumber(emailAddressSubject.getValue());
        mentorEntity.setEmailAddress(phoneNumberSubject.getValue());
        mentorEntity.setNotes(notesSubject.getValue());
        return dbLoader.saveMentor(mentorEntity, ignoreWarnings).doOnSuccess(m -> {
            if (!m.isError()) {
                originalValuesSubject.onNext(mentorEntity);
                if (oldId == ID_NEW) {
                    compositeDisposable.add(normalizedNameObservable.subscribe(n ->
                            titleFactory.postValue(r -> r.getString(R.string.format_mentor, n))));
                }
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete(" + ignoreWarnings + ")");
        return dbLoader.deleteMentor(originalValuesSubject.getValue(), ignoreWarnings);
    }

    private void onEntityLoadedFromDb(@NonNull MentorEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoadedFromDb(" + entity + ")");
        notesSubject.onNext(entity.getNotes());
        nameSubject.onNext(entity.getName());
        phoneNumberSubject.onNext(entity.getPhoneNumber());
        emailAddressSubject.onNext(entity.getEmailAddress());
        coursesLiveData = dbLoader.getCoursesByMentorId(entity.getId());
        originalValuesSubject.onNext(entity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        outState.putBoolean(STATE_KEY_SHARING_DISABLED_NOTIFICATION_DISMISSED, Boolean.TRUE.equals(sharingDisabledNotificationDismissedSubject.getValue()));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NAME, false), nameSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_PHONE_NUMBER, false), phoneNumberSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_EMAIL_ADDRESS, false), emailAddressSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_NOTES, false), notesSubject.getValue());
        Objects.requireNonNull(originalValuesSubject.getValue()).saveState(outState, true);
    }

    public LiveData<List<MentorCourseListItem>> getCoursesLiveData() {
        return coursesLiveData;
    }

    public Single<List<AlertListItem>> getAllAlerts() {
        long id = Objects.requireNonNull(originalValuesSubject.getValue()).getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByMentorId(id);
        }
        return Single.just(Collections.emptyList());
    }

}