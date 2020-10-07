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
import androidx.lifecycle.MutableLiveData;

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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditMentorViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final DbLoader dbLoader;
    private final MutableLiveData<MentorEntity> entityLiveData;
    private final MutableLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final MutableLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Boolean> contactValidLiveData;
    private final MutableLiveData<String> nameLiveData;
    private final CurrentValues currentValues;
    private String viewTitle;
    private Spanned overview;
    private MentorEntity mentorEntity;
    private boolean fromInitializedState;
    private String normalizedNotes;
    private String normalizedName;
    private String normalizedPhoneNumber;
    private String normalizedEmailAddress;

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        titleFactoryLiveData = new MutableLiveData<>(c -> c.getString(R.string.title_activity_mentor_detail));
        overviewFactoryLiveData = new MutableLiveData<>(r -> new SpannableString(""));
        normalizedNotes = normalizedName = normalizedPhoneNumber = normalizedEmailAddress = "";
        entityLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
        contactValidLiveData = new MutableLiveData<>(false);
        nameLiveData = new MutableLiveData<>("");
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

    public String getNormalizedNotes() {
        if (null == normalizedNotes) {
            normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(currentValues.notes);
            if (normalizedNotes.equals(currentValues.notes)) {
                currentValues.notes = null;
            }
        }
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

    public LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData;
    }

    public LiveData<Boolean> getContactValidLiveData() {
        return contactValidLiveData;
    }

    public LiveData<MentorEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public MutableLiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData;
    }

    public MutableLiveData<String> getNameLiveData() {
        return nameLiveData;
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
                        .doOnSuccess(this::onEntityLoaded)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
            }
        }
        titleFactoryLiveData.postValue(this::calculateViewTitle);
        overviewFactoryLiveData.postValue(this::calculateOverview);
        entityLiveData.postValue(mentorEntity);
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

    private void onEntityLoaded(MentorEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoaded");
        mentorEntity = entity;
        viewTitle = null;
        setName(entity.getName());
        titleFactoryLiveData.postValue(this::calculateViewTitle);
        setPhoneNumber(entity.getPhoneNumber());
        setEmailAddress(entity.getEmailAddress());
        overviewFactoryLiveData.postValue(this::calculateOverview);
        setNotes(entity.getNotes());
        entityLiveData.postValue(entity);
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
            return !getNormalizedNotes().equals(mentorEntity.getNotes());
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
            Log.d(LOG_TAG, "Enter setName:  current: " + ToStringBuilder.toEscapedString(this.phoneNumber) + "; new: " + ToStringBuilder.toEscapedString(phoneNumber));
            this.name = (null == name) ? "" : name;
            String oldValue = normalizedName;
            normalizedName = TermEntity.SINGLE_LINE_NORMALIZER.apply(name);
            if (normalizedName.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "setName:  Posting false to nameValidLiveData");
                    nameValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty()) {
                Log.d(LOG_TAG, "setName:  Posting true to nameValidLiveData");
                nameValidLiveData.postValue(true);
            }
            if (!oldValue.equals(normalizedName)) {
                viewTitle = null;
                Log.d(LOG_TAG, "setName:  Posting new recalculation to titleFactoryLiveData");
                titleFactoryLiveData.postValue(EditMentorViewModel.this::calculateViewTitle);
                Log.d(LOG_TAG, "setName: Posting " + ToStringBuilder.toEscapedString(normalizedName) + " to overviewFactoryLiveData");
                nameLiveData.postValue(normalizedName);
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
            if (normalizedPhoneNumber.isEmpty()) {
                if (!oldValue.isEmpty() && normalizedEmailAddress.isEmpty()) {
                    Log.d(LOG_TAG, "setPhoneNumber:  Posting false to contactValidLiveData");
                    contactValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty() && !normalizedEmailAddress.isEmpty()) {
                Log.d(LOG_TAG, "setPhoneNumber:  Posting true to contactValidLiveData");
                contactValidLiveData.postValue(true);
            }
            if (!oldValue.equals(normalizedPhoneNumber)) {
                overview = null;
                Log.d(LOG_TAG, "setPhoneNumber:  Posting new recalculation to overviewFactoryLiveData");
                overviewFactoryLiveData.postValue(EditMentorViewModel.this::calculateOverview);
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
            if (normalizedEmailAddress.isEmpty()) {
                if (!oldValue.isEmpty() && normalizedPhoneNumber.isEmpty()) {
                    Log.d(LOG_TAG, "setEmailAddress:  Posting false to contactValidLiveData");
                    contactValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty() && !normalizedPhoneNumber.isEmpty()) {
                Log.d(LOG_TAG, "setEmailAddress:  Posting true to contactValidLiveData");
                contactValidLiveData.postValue(true);
            }
            if (!oldValue.equals(normalizedEmailAddress)) {
                overview = null;
                Log.d(LOG_TAG, "setEmailAddress:  Posting new recalculation to overviewFactoryLiveData");
                overviewFactoryLiveData.postValue(EditMentorViewModel.this::calculateOverview);
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