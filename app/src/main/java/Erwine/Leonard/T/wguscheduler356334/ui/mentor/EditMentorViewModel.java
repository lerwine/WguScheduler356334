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
import androidx.lifecycle.AndroidViewModel;
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
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditMentorViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    private static final Predicate<String> EMAIL_PREDICATE = Pattern.compile("^[^@]+@[^.]+\\.[^.]+.*$").asPredicate();
    private static final Predicate<String> PHONE_PREDICATE = Pattern.compile("^([0-9+]|\\(\\d{1,3}\\))[0-9\\-. ]{3,15}").asPredicate();

    private final DbLoader dbLoader;
    private final PrivateLiveData<MentorEntity> currentEntitySubject;
    private final PrivateLiveData<Function<Resources, String>> titleFactory;
    private final PrivateLiveData<Function<Resources, Spanned>> overviewFactory;
    private final BehaviorSubject<String> nameSubject;
    private final BehaviorSubject<String> phoneNumberSubject;
    private final BehaviorSubject<String> emailAddressSubject;
    private final BehaviorSubject<String> notesSubject;
    private final PrivateLiveData<Boolean> canShareLiveData;
    private final PrivateLiveData<Boolean> canSaveLiveData;
    private final PrivateLiveData<String> normalizedNameLiveData;
    private final PrivateLiveData<Boolean> nameValidLiveData;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> emailValidationMessageLiveData;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> phoneValidationMessageLiveData;
    private final PrivateLiveData<Boolean> isValidLiveData;
    private final PrivateLiveData<Boolean> hasChangesLiveData;
    private final CurrentValues currentValues;
    @SuppressWarnings("FieldCanBeLocal")
    private final CompositeDisposable compositeDisposable;
    private LiveData<List<MentorCourseListItem>> coursesLiveData;
    private MentorEntity mentorEntity;
    private boolean fromInitializedState;

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        currentEntitySubject = new PrivateLiveData<>();
        nameSubject = BehaviorSubject.create();
        phoneNumberSubject = BehaviorSubject.create();
        emailAddressSubject = BehaviorSubject.create();
        notesSubject = BehaviorSubject.create();
        currentValues = new CurrentValues();

        Observable<String> normalizedNameObservable = nameSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<Boolean> nameValidObservable = normalizedNameObservable.map(s -> !s.isEmpty());
        Observable<String> normalizedPhoneObservable = phoneNumberSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<String> normalizedEmailObservable = emailAddressSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
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
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                normalizedNameObservable.map(n -> !n.equals(mentorEntity.getName())),
                normalizedPhoneObservable.map(n -> !n.equals(mentorEntity.getPhoneNumber())),
                normalizedEmailObservable.map(n -> !n.equals(mentorEntity.getEmailAddress())),
                notesSubject.map(n -> !AbstractNotedEntity.MULTI_LINE_NORMALIZER.apply(n).equals(mentorEntity.getNotes())),
                (n, p, e, s) -> n || p || e || s);
        Observable<Boolean> validObservable = Observable.combineLatest(nameValidObservable, phoneValidationMessageObservable, emailValidationMessageObservable,
                (nameValid, phoneValidationMessage, emailValidationMessage) -> nameValid && phoneValidationMessage.map(ResourceMessageFactory::isWarning).orElse(true) &&
                        emailValidationMessage.map(ResourceMessageFactory::isWarning).orElse(true));

        hasChangesLiveData = new PrivateLiveData<>(false);
        canShareLiveData = new PrivateLiveData<>(false);
        canSaveLiveData = new PrivateLiveData<>(false);
        isValidLiveData = new PrivateLiveData<>(false);
        nameValidLiveData = new PrivateLiveData<>(false);
        normalizedNameLiveData = new PrivateLiveData<>("");
        titleFactory = new PrivateLiveData<>(r -> "");
        overviewFactory = new PrivateLiveData<>(r -> new SpannableString(" "));
        emailValidationMessageLiveData = new PrivateLiveData<>(Optional.empty());
        phoneValidationMessageLiveData = new PrivateLiveData<>(Optional.empty());

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(hasChangesObservable.subscribe(this::onHasChangesChanged));
        compositeDisposable.add(validObservable.subscribe(this::onValidChanged));
        compositeDisposable.add(nameValidObservable.subscribe(nameValidLiveData::postValue));
        compositeDisposable.add(normalizedNameObservable.subscribe(normalizedNameLiveData::postValue));
        compositeDisposable.add(phoneValidationMessageObservable.subscribe(phoneValidationMessageLiveData::postValue));
        compositeDisposable.add(emailValidationMessageObservable.subscribe(emailValidationMessageLiveData::postValue));
        compositeDisposable.add(Observable.combineLatest(normalizedPhoneObservable, normalizedEmailObservable, this::getOverviewFactory)
                .subscribe(overviewFactory::postValue));
    }

    private void onHasChangesChanged(Boolean hasChanges) {
        boolean c = Boolean.TRUE.equals(hasChanges);
        boolean v = Boolean.TRUE.equals(isValidLiveData.getValue());
        this.hasChangesLiveData.postValue(c);
        canShareLiveData.postValue(v && !c);
        canSaveLiveData.postValue(v && c);
    }

    private void onValidChanged(Boolean isValid) {
        boolean c = Boolean.TRUE.equals(hasChangesLiveData.getValue());
        boolean v = Boolean.TRUE.equals(isValid);
        this.isValidLiveData.postValue(v);
        canShareLiveData.postValue(v && !c);
        canSaveLiveData.postValue(v && c);
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

    @NonNull
    private synchronized Function<Resources, String> getViewTitleFactory(String name) {
        return r -> r.getString(R.string.format_mentor, name);
    }

    public static void startEditMentorActivity(Context context, Long id) {
        Intent intent = new Intent(context, EditMentorActivity.class);
        if (null != id) {
            intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, false), id);
        }
        context.startActivity(intent);
    }

    public long getId() {
        return (null == mentorEntity) ? ID_NEW : mentorEntity.getId();
    }

    public String getName() {
        return currentValues.getName();
    }

    public synchronized void setName(String value) {
        currentValues.setName(value);
    }

    public String getPhoneNumber() {
        return currentValues.getPhoneNumber();
    }

    public synchronized void setPhoneNumber(String value) {
        currentValues.setPhoneNumber(value);
    }

    public String getEmailAddress() {
        return currentValues.getEmailAddress();
    }

    public synchronized void setEmailAddress(String value) {
        currentValues.setEmailAddress(value);
    }

    public String getNotes() {
        return currentValues.getNotes();
    }

    public synchronized void setNotes(String value) {
        currentValues.setNotes(value);
    }

    public LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData;
    }

    public LiveData<Optional<ResourceMessageFactory>> getPhoneValidationMessageLiveData() {
        return phoneValidationMessageLiveData;
    }

    public LiveData<Optional<ResourceMessageFactory>> getEmailValidationMessageLiveData() {
        return emailValidationMessageLiveData;
    }

    public LiveData<Function<Resources, String>> getTitleFactory() {
        return titleFactory;
    }

    public LiveData<Function<Resources, Spanned>> getOverviewFactory() {
        return overviewFactory;
    }

    public LiveData<String> getNormalizedNameLiveData() {
        return normalizedNameLiveData;
    }

    public LiveData<Boolean> getIsValidLiveData() {
        return isValidLiveData;
    }

    public LiveData<Boolean> getCanShareLiveData() {
        return canShareLiveData;
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData;
    }

    public LiveData<Boolean> getHasChangesLiveData() {
        return hasChangesLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<MentorEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        mentorEntity = new MentorEntity();
        if (null != state) {
            currentValues.restoreState(state, false);
            long id = currentValues.getId();
            if (ID_NEW == id || fromInitializedState) {
                mentorEntity.restoreState(state, fromInitializedState);
            } else {
                return dbLoader.getMentorById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
            }
        }
        currentEntitySubject.postValue(mentorEntity);
        coursesLiveData = new PrivateLiveData<>(Collections.emptyList());
        return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        String newName = currentValues.name;
        String newPhone = currentValues.phoneNumber;
        String newEmail = currentValues.emailAddress;
        String originalName = mentorEntity.getName();
        String originalPhoneNumber = mentorEntity.getPhoneNumber();
        String originalEmailAddress = mentorEntity.getEmailAddress();
        String originalNotes = mentorEntity.getNotes();
        mentorEntity.setName(newName);
        mentorEntity.setPhoneNumber(newEmail);
        mentorEntity.setEmailAddress(newPhone);
        mentorEntity.setNotes(currentValues.notes);
        return dbLoader.saveMentor(mentorEntity, ignoreWarnings).doOnError(throwable -> {
            mentorEntity.setName(originalName);
            mentorEntity.setPhoneNumber(originalPhoneNumber);
            mentorEntity.setEmailAddress(originalEmailAddress);
            mentorEntity.setNotes(originalNotes);
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete");
        return dbLoader.deleteMentor(mentorEntity, ignoreWarnings);
    }

    private void onEntityLoadedFromDb(MentorEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoaded");
        mentorEntity = entity;
        setNotes(entity.getNotes());
        setName(entity.getName());
        setPhoneNumber(entity.getPhoneNumber());
        setEmailAddress(entity.getEmailAddress());
        coursesLiveData = dbLoader.getCoursesByMentorId(entity.getId());
        currentEntitySubject.postValue(mentorEntity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        mentorEntity.saveState(outState, true);
    }

    public LiveData<List<MentorCourseListItem>> getCoursesLiveData() {
        return coursesLiveData;
    }

    public LiveData<List<AlertListItem>> getAllAlerts() {
        long id = mentorEntity.getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByMentorId(id);
        }
        PrivateLiveData<List<AlertListItem>> result = new PrivateLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
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

    }

    private class CurrentValues implements Mentor {

        private long id;
        @NonNull
        private String name = "";
        @NonNull
        private String phoneNumber = "";
        @NonNull
        private String emailAddress = "";
        @NonNull
        private String notes = "";

        @Override
        public long getId() {
            return id;
        }

        @Override
        public void setId(long id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            Log.d(LOG_TAG, "Enter setName:  current: " + ToStringBuilder.toEscapedString(this.name) + "; new: " + ToStringBuilder.toEscapedString(name));
            this.name = (null == name) ? "" : name;
            nameSubject.onNext(this.name);
        }

        @NonNull
        @Override
        public String getPhoneNumber() {
            return phoneNumber;
        }

        @Override
        public void setPhoneNumber(String phoneNumber) {
            Log.d(LOG_TAG, "Enter setPhoneNumber:  current: " + ToStringBuilder.toEscapedString(this.phoneNumber) + "; new: " + ToStringBuilder.toEscapedString(phoneNumber));
            this.phoneNumber = (null == phoneNumber) ? "" : phoneNumber;
            phoneNumberSubject.onNext(this.phoneNumber);
        }

        @NonNull
        @Override
        public String getEmailAddress() {
            return emailAddress;
        }

        @Override
        public void setEmailAddress(String emailAddress) {
            Log.d(LOG_TAG, "Enter setEmailAddress:  current: " + ToStringBuilder.toEscapedString(this.emailAddress) + "; new: " + ToStringBuilder.toEscapedString(emailAddress));
            this.emailAddress = (null == emailAddress) ? "" : emailAddress;
            emailAddressSubject.onNext(this.emailAddress);
        }

        @NonNull
        @Override
        public String getNotes() {
            return notes;
        }

        @Override
        public void setNotes(String notes) {
            this.notes = (null == notes) ? "" : notes;
            notesSubject.onNext(this.notes);
        }

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}