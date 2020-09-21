package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

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

import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.EditMentorActivity;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditMentorViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditMentorViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final MutableLiveData<MentorEntity> entityLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Boolean> contactValidLiveData;
    private MentorEntity mentorEntity;
    private boolean fromInitializedState;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String notes;
    private String normalizedNotes;
    private String normalizedName;
    private String normalizedPhoneNumber;
    private String normalizedEmailAddress;

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        name = phoneNumber = emailAddress = notes = normalizedName = normalizedPhoneNumber = normalizedEmailAddress = "";
        entityLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
        contactValidLiveData = new MutableLiveData<>(false);
    }

    public static void startEditMentorActivity(Context context, Long id) {
        Intent intent = new Intent(context, EditMentorActivity.class);
        if (null != id) {
            intent.putExtra(MentorEntity.STATE_KEY_ID, id);
        }
        context.startActivity(intent);
    }

    public Long getId() {
        return (null == mentorEntity) ? null : mentorEntity.getId();
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String value) {
        name = (null == value) ? "" : value;
        value = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        String oldValue = normalizedName;
        normalizedName = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedName.isEmpty()) {
            if (!oldValue.isEmpty()) {
                nameValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty()) {
            nameValidLiveData.postValue(true);
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public synchronized void setPhoneNumber(String value) {
        phoneNumber = (null == value) ? "" : value;
        value = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        String oldValue = normalizedPhoneNumber;
        normalizedPhoneNumber = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedPhoneNumber.isEmpty()) {
            if (!oldValue.isEmpty() && normalizedEmailAddress.isEmpty()) {
                contactValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty() && !normalizedEmailAddress.isEmpty()) {
            contactValidLiveData.postValue(true);
        }
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public synchronized void setEmailAddress(String value) {
        emailAddress = (null == value) ? "" : value;
        value = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        String oldValue = normalizedEmailAddress;
        normalizedEmailAddress = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedEmailAddress.isEmpty()) {
            if (!oldValue.isEmpty() && normalizedPhoneNumber.isEmpty()) {
                contactValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty() && !normalizedPhoneNumber.isEmpty()) {
            contactValidLiveData.postValue(true);
        }
    }

    public String getNotes() {
        return notes;
    }

    public synchronized void setNotes(String value) {
        if (null == value || value.isEmpty()) {
            normalizedNotes = notes = "";
        } else if (!value.equals(notes)) {
            notes = value;
            normalizedNotes = null;
        }
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

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    @SuppressWarnings("ConstantConditions")
    public synchronized Single<MentorEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            mentorEntity = new MentorEntity();
        } else if (state.containsKey(MentorEntity.STATE_KEY_ID)) {
            if (fromInitializedState) {
                mentorEntity = new MentorEntity(state, true);
            } else {
                return dbLoader.getMentorById(state.getLong(MentorEntity.STATE_KEY_ID))
                        .doOnSuccess(this::onEntityLoaded)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
            }
        } else {
            mentorEntity = new MentorEntity(state, fromInitializedState);
        }
        entityLiveData.postValue(mentorEntity);
        if (fromInitializedState) {
            setName(state.getString(MentorEntity.STATE_KEY_NAME, ""));
            setPhoneNumber(state.getString(MentorEntity.STATE_KEY_PHONE_NUMBER, ""));
            setEmailAddress(state.getString(MentorEntity.STATE_KEY_EMAIL_ADDRESS, ""));
            setNotes(state.getString(MentorEntity.STATE_KEY_NOTES, ""));
        } else {
            setName(mentorEntity.getName());
            setPhoneNumber(mentorEntity.getPhoneNumber());
            setEmailAddress(mentorEntity.getEmailAddress());
            setNotes(mentorEntity.getNotes());
        }
        return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> save() {
        String newName = normalizedName;
        String newPhone = normalizedPhoneNumber;
        String newEmail = normalizedEmailAddress;
        if (newPhone.isEmpty() && newEmail.isEmpty()) {
            if (newName.isEmpty()) {
                return Single.just("Name cannot be empty; Phone number or email address required.");
            }
            return Single.just("Phone number or email address required.");
        } else if (newName.isEmpty()) {
            return Single.just("Name cannot be empty.");
        }
        String originalName = mentorEntity.getName();
        String originalPhoneNumber = mentorEntity.getPhoneNumber();
        String originalEmailAddress = mentorEntity.getEmailAddress();
        String originalNotes = mentorEntity.getNotes();
        mentorEntity.setName(newName);
        mentorEntity.setPhoneNumber(newEmail);
        mentorEntity.setEmailAddress(newPhone);
        mentorEntity.setNotes(notes);
        return dbLoader.saveMentor(mentorEntity).doOnError(throwable -> {
            mentorEntity.setName(originalName);
            mentorEntity.setPhoneNumber(originalPhoneNumber);
            mentorEntity.setEmailAddress(originalEmailAddress);
            mentorEntity.setNotes(originalNotes);
            Log.e(getClass().getName(), "Error saving mentor", throwable);
        }).toSingleDefault("");
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteMentor(mentorEntity).doOnError(throwable -> Log.e(getClass().getName(), "Error deleting mentor", throwable));
    }

    private void onEntityLoaded(MentorEntity entity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onEntityLoaded");
        mentorEntity = entity;
        setName(entity.getName());
        setPhoneNumber(entity.getPhoneNumber());
        setEmailAddress(entity.getEmailAddress());
        setNotes(entity.getNotes());
        entityLiveData.postValue(entity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        mentorEntity.saveState(outState, true);
        outState.putString(MentorEntity.STATE_KEY_NAME, name);
        outState.putString(MentorEntity.STATE_KEY_PHONE_NUMBER, phoneNumber);
        outState.putString(MentorEntity.STATE_KEY_EMAIL_ADDRESS, emailAddress);
        outState.putString(MentorEntity.STATE_KEY_NOTES, notes);
    }

    public boolean isChanged() {
        if (null != mentorEntity.getId() && normalizedName.equals(mentorEntity.getName()) && normalizedPhoneNumber.equals(mentorEntity.getPhoneNumber()) &&
                normalizedEmailAddress.equals(mentorEntity.getEmailAddress())) {
            if (null == normalizedNotes) {
                normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(notes);
            }
            return !normalizedNotes.equals(mentorEntity.getNotes());
        }
        return true;
    }
}