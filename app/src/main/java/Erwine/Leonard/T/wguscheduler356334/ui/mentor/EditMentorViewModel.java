package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.EditMentorActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
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

    private final DbLoader dbLoader;
    private final BehaviorSubject<MentorEntity> currentEntitySubject;
    private final Observable<MentorEntity> currentEntityObservable;
    private final BehaviorSubject<Function<Resources, String>> titleFactorySubject;
    private final Observable<Function<Resources, String>> titleFactoryObservable;
    private final BehaviorSubject<Function<Resources, Spanned>> overviewFactorySubject;
    private final Observable<Function<Resources, Spanned>> overviewFactoryObservable;
    private final BehaviorSubject<Boolean> nameValidSubject;
    private final Observable<Boolean> nameValidObservable;
    private final BehaviorSubject<Boolean> phoneNotEmptySubject;
    private final BehaviorSubject<Boolean> emailNotEmptySubject;
    private final Observable<Boolean> contactValidObservable;
    private final BehaviorSubject<Boolean> hasChangesSubject;
    private final Observable<Boolean> validObservable;
    private final Observable<Boolean> canShareObservable;
    private final Observable<Boolean> canSaveObservable;
    private final BehaviorSubject<String> currentNameSubject;
    private final Observable<String> currentNameObservable;
    private final CurrentValues currentValues;
    private String viewTitle;
    private Spanned overview;
    private MentorEntity mentorEntity;
    private boolean fromInitializedState;
    @NonNull
    private String normalizedNotes = "";
    @NonNull
    private String normalizedName = "";
    @NonNull
    private String normalizedPhoneNumber = "";
    @NonNull
    private String normalizedEmailAddress = "";

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        titleFactorySubject = BehaviorSubject.create();
        titleFactoryObservable = titleFactorySubject.observeOn(AndroidSchedulers.mainThread());
        overviewFactorySubject = BehaviorSubject.create();
        overviewFactoryObservable = overviewFactorySubject.observeOn(AndroidSchedulers.mainThread());
        currentEntitySubject = BehaviorSubject.create();
        currentEntityObservable = currentEntitySubject.observeOn(AndroidSchedulers.mainThread());
        nameValidSubject = BehaviorSubject.create();
        nameValidObservable = nameValidSubject.observeOn(AndroidSchedulers.mainThread());
        phoneNotEmptySubject = BehaviorSubject.create();
        emailNotEmptySubject = BehaviorSubject.create();
        contactValidObservable = Observable.combineLatest(phoneNotEmptySubject, emailNotEmptySubject, (p, e) -> p || e).observeOn(AndroidSchedulers.mainThread());
        hasChangesSubject = BehaviorSubject.create();
        validObservable = Observable.combineLatest(nameValidSubject, contactValidObservable, (n, c) -> n && c).observeOn(AndroidSchedulers.mainThread());
        canShareObservable = Observable.combineLatest(validObservable, hasChangesSubject, (v, c) -> v && !c).observeOn(AndroidSchedulers.mainThread());
        canSaveObservable = Observable.combineLatest(validObservable, hasChangesSubject, (v, c) -> v && c).observeOn(AndroidSchedulers.mainThread());
        currentNameSubject = BehaviorSubject.create();
        currentNameObservable = currentNameSubject.observeOn(AndroidSchedulers.mainThread());
        currentValues = new CurrentValues();
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

    @NonNull
    public String getNormalizedNotes() {
        return normalizedNotes;
    }

    @NonNull
    public synchronized Spanned calculateOverview(Resources resources) {
        if (null != overview) {
            return overview;
        }
        Spanned result;
        if (normalizedPhoneNumber.isEmpty()) {
            if (normalizedEmailAddress.isEmpty()) {
                result = new SpannableStringBuilder().append(resources.getString(R.string.message_phone_or_email_required),
                        new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                result = Html.fromHtml(resources.getString(R.string.html_format_mentor_email, normalizedEmailAddress), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            }
        } else if (normalizedEmailAddress.isEmpty()) {
            result = Html.fromHtml(resources.getString(R.string.html_format_mentor_phone, normalizedPhoneNumber), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            result = Html.fromHtml(resources.getString(R.string.html_format_mentor_phone_and_email, normalizedPhoneNumber, normalizedEmailAddress), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        }
        overview = result;
        return result;
    }

    private synchronized String calculateViewTitle(Resources resources) {
        if (null != viewTitle) {
            viewTitle = resources.getString(R.string.format_mentor, normalizedName);
        }
        return viewTitle;
    }

    public Observable<Boolean> getNameValid() {
        return nameValidObservable;
    }

    public Observable<Boolean> getContactValid() {
        return contactValidObservable;
    }

    public Observable<MentorEntity> getCurrentEntity() {
        return currentEntityObservable;
    }

    public Observable<Function<Resources, String>> getTitleFactory() {
        return titleFactoryObservable;
    }

    public Observable<Function<Resources, Spanned>> getOverviewFactory() {
        return overviewFactoryObservable;
    }

    public Observable<String> getCurrentName() {
        return currentNameObservable;
    }

    public Observable<Boolean> getValid() {
        return validObservable;
    }

    public Observable<Boolean> getCanShareObservable() {
        return canShareObservable;
    }

    public Observable<Boolean> getCanSaveObservable() {
        return canSaveObservable;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<MentorEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        mentorEntity = new MentorEntity();
        viewTitle = null;
        overview = null;
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
        onEntityLoaded();
        return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ValidationMessage.ResourceMessageResult> save(boolean ignoreWarnings) {
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

    public Single<ValidationMessage.ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete");
        return dbLoader.deleteMentor(mentorEntity, ignoreWarnings);
    }

    private void onEntityLoadedFromDb(MentorEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoaded");
        mentorEntity = entity;
        viewTitle = null;
        setNotes(entity.getNotes());
        setName(entity.getName());
        setPhoneNumber(entity.getPhoneNumber());
        setEmailAddress(entity.getEmailAddress());
        onEntityLoaded();
    }

    private void onEntityLoaded() {
        titleFactorySubject.onNext(this::calculateViewTitle);
        overviewFactorySubject.onNext(this::calculateOverview);
        nameValidSubject.onNext(!normalizedName.isEmpty());
        phoneNotEmptySubject.onNext(!normalizedPhoneNumber.isEmpty());
        emailNotEmptySubject.onNext(!normalizedEmailAddress.isEmpty());
        hasChangesSubject.onNext(isChanged());
        currentEntitySubject.onNext(mentorEntity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        mentorEntity.saveState(outState, true);
    }

    public boolean isChanged() {
        if (ID_NEW != mentorEntity.getId() && normalizedName.equals(mentorEntity.getName()) && normalizedPhoneNumber.equals(mentorEntity.getPhoneNumber()) &&
                normalizedEmailAddress.equals(mentorEntity.getEmailAddress())) {
            return !normalizedNotes.equals(mentorEntity.getNotes());
        }
        return true;
    }

    private class CurrentValues implements Mentor {

        private long id;
        @NonNull
        private String name = "";
        @NonNull
        private String phoneNumber = "";
        @NonNull
        private String emailAddress = "";
        private String notes;

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
            String oldValue = normalizedName;
            normalizedName = TermEntity.SINGLE_LINE_NORMALIZER.apply(name);
            if (normalizedName.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "setName:  Posting false to nameValidLiveData");
                    nameValidSubject.onNext(false);
                }
            } else if (oldValue.isEmpty()) {
                Log.d(LOG_TAG, "setName:  Posting true to nameValidLiveData");
                nameValidSubject.onNext(true);
            }
            if (!oldValue.equals(normalizedName)) {
                viewTitle = null;
                Log.d(LOG_TAG, "setName:  Posting new recalculation to titleFactoryLiveData");
                titleFactorySubject.onNext(EditMentorViewModel.this::calculateViewTitle);
                Log.d(LOG_TAG, "setName: Posting " + ToStringBuilder.toEscapedString(normalizedName) + " to overviewFactoryLiveData");
                currentNameSubject.onNext(normalizedName);
                hasChangesSubject.onNext(isChanged());
            }
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
            String oldValue = normalizedPhoneNumber;
            normalizedPhoneNumber = TermEntity.SINGLE_LINE_NORMALIZER.apply(phoneNumber);
            phoneNotEmptySubject.onNext(!normalizedPhoneNumber.isEmpty());
            if (!oldValue.equals(normalizedPhoneNumber)) {
                overview = null;
                Log.d(LOG_TAG, "setPhoneNumber:  Posting new recalculation to overviewFactoryLiveData");
                overviewFactorySubject.onNext(EditMentorViewModel.this::calculateOverview);
                hasChangesSubject.onNext(isChanged());
            }
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
            String oldValue = normalizedEmailAddress;
            normalizedEmailAddress = TermEntity.SINGLE_LINE_NORMALIZER.apply(emailAddress);
            emailNotEmptySubject.onNext(!normalizedEmailAddress.isEmpty());
            if (!oldValue.equals(normalizedEmailAddress)) {
                overview = null;
                Log.d(LOG_TAG, "setEmailAddress:  Posting new recalculation to overviewFactoryLiveData");
                overviewFactorySubject.onNext(EditMentorViewModel.this::calculateOverview);

                CompositeDisposable d = new CompositeDisposable();
                d.add(hasChangesSubject.last(false).subscribe(t -> {
                    d.clear();
                    if (!t) {
                        hasChangesSubject.onNext(isChanged());
                    }
                }));
            }
        }

        @NonNull
        @Override
        public String getNotes() {
            return (null == notes) ? normalizedNotes : notes;
        }

        @Override
        public void setNotes(String notes) {
            String oldValue = normalizedNotes;
            if (null == notes || notes.isEmpty()) {
                if (normalizedNotes.isEmpty()) {
                    return;
                }
                normalizedNotes = "";
                this.notes = null;
            } else if (!getNotes().equals(notes)) {
                normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(notes);
                this.notes = (normalizedNotes.equals(notes)) ? null : notes;
            }
            CompositeDisposable d = new CompositeDisposable();
            d.add(hasChangesSubject.last(false).subscribe(t -> {
                d.clear();
                if (!(t || normalizedNotes.equals(oldValue))) {
                    hasChangesSubject.onNext(isChanged());
                }
            }));
        }

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}