package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.NormalizingCharSequence;
import Erwine.Leonard.T.wguscheduler356334.util.Values;
import Erwine.Leonard.T.wguscheduler356334.util.live.BooleanAndLiveData;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditMentorViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditMentorViewModel.class.getName();
    static final String ARGUMENT_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String ARGUMENT_KEY_MENTOR_ID = "mentor_id";
    public static final String ARGUMENT_KEY_NAME = "name";
    public static final String ARGUMENT_KEY_PHONE_NUMBER = "phone_number";
    public static final String ARGUMENT_KEY_EMAIL_ADDRESS = "email_address";
    public static final String ARGUMENT_KEY_NOTES = "notes";
    public static final String ARGUMENT_KEY_ORIGINAL_NAME = "original_name";
    public static final String ARGUMENT_KEY_ORIGINAL_PHONE_NUMBER = "original_phone_number";
    public static final String ARGUMENT_KEY_ORIGINAL_EMAIL_ADDRESS = "original_email_address";
    public static final String ARGUMENT_KEY_ORIGINAL_NOTES = "original_notes";

    private final MutableLiveData<MentorEntity> entityLiveData;
    private final DbLoader dbLoader;
    private final MutableLiveData<Boolean> nameValidLiveData;
    private final MutableLiveData<Boolean> contactValidLiveData;
    private final BooleanAndLiveData savableLiveData;
    private MutableLiveData<Boolean> nameChangedLiveData;
    private MutableLiveData<Boolean> phoneNumberChangedLiveData;
    private MutableLiveData<Boolean> emailAddressChangedLiveData;
    private MutableLiveData<Boolean> notesChangedLiveData;
    private boolean fromInitializedState;
    private Long id;
    private final NormalizingCharSequence name;
    private final NormalizingCharSequence phoneNumber;
    private final NormalizingCharSequence emailAddress;
    private final NormalizingCharSequence notes;

    public boolean isChanged() {
        return null == id || Values.notNullOr(nameChangedLiveData.getValue(), phoneNumberChangedLiveData.getValue(),
                emailAddressChangedLiveData.getValue(), notesChangedLiveData.getValue());
    }

    public EditMentorViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        name = new NormalizingCharSequence("", TermEntity.SINGLE_LINE_NORMALIZER);
        phoneNumber = new NormalizingCharSequence("", TermEntity.SINGLE_LINE_NORMALIZER);
        emailAddress = new NormalizingCharSequence("", TermEntity.SINGLE_LINE_NORMALIZER);
        notes = new NormalizingCharSequence("", TermEntity.MULTI_LINE_NORMALIZER);
        entityLiveData = new MutableLiveData<>();
        nameValidLiveData = new MutableLiveData<>(false);
        contactValidLiveData = new MutableLiveData<>(false);
        savableLiveData = new BooleanAndLiveData();
        savableLiveData.addSource(nameValidLiveData);
        savableLiveData.addSource(contactValidLiveData);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.rawString();
    }

    public String getPhoneNumber() {
        return phoneNumber.rawString();
    }

    public String getEmailAddress() {
        return emailAddress.rawString();
    }

    public String getNotes() {
        return notes.rawString();
    }

    public LiveData<Boolean> getNameValidLiveData() {
        return nameValidLiveData;
    }

    public LiveData<Boolean> getContactValidLiveData() {
        return contactValidLiveData;
    }

    public LiveData<Boolean> getSavableLiveData() {
        return savableLiveData;
    }

    public LiveData<MentorEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<MentorEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(ARGUMENT_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        MentorEntity entity;
        if (null == state) {
            id = null;
            name.setValue("");
            phoneNumber.setValue("");
            emailAddress.setValue("");
            notes.setValue("");
            entity = new MentorEntity();
        } else {
            id = (state.containsKey(ARGUMENT_KEY_MENTOR_ID)) ? state.getLong(ARGUMENT_KEY_MENTOR_ID) : null;
            if (fromInitializedState) {
                name.setValue(state.getString(ARGUMENT_KEY_NAME, ""));
                phoneNumber.setValue(state.getString(ARGUMENT_KEY_PHONE_NUMBER, ""));
                emailAddress.setValue(state.getString(ARGUMENT_KEY_EMAIL_ADDRESS, ""));
                notes.setValue(state.getString(ARGUMENT_KEY_NOTES, ""));
            }
            if (null != id) {
                return dbLoader.getMentorById(id).doOnSuccess(this::onEntityLoaded).doOnError(throwable -> Log.e(getClass().getName(), "Error loading mentor", throwable));
            }
            if (fromInitializedState) {
                entity = new MentorEntity(state.getString(ARGUMENT_KEY_ORIGINAL_NAME, ""), state.getString(ARGUMENT_KEY_ORIGINAL_NOTES, ""),
                        state.getString(ARGUMENT_KEY_ORIGINAL_PHONE_NUMBER, ""), state.getString(ARGUMENT_KEY_ORIGINAL_EMAIL_ADDRESS, ""));
            } else {
                entity = new MentorEntity(name.asIs(), notes.asIs(), phoneNumber.asIs(), emailAddress.asIs());
            }
        }
        entityLiveData.postValue(entity);
        onMentorNameTextChanged(name.toString(), entity);
        onPhoneNumberTextChanged(phoneNumber.toString(), entity);
        onEmailAddressTextChanged(emailAddress.toString(), entity);
        onMentorNotesEditTextChanged(notes.toString(), entity);
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable save() {
        MentorEntity entity = Objects.requireNonNull(entityLiveData.getValue());
        String originalName = entity.getName();
        String originalPhoneNumber = entity.getPhoneNumber();
        String originalEmailAddress = entity.getEmailAddress();
        String originalNotes = entity.getNotes();
        entity.setName(name.asIs());
        entity.setPhoneNumber(emailAddress.asIs());
        entity.setEmailAddress(phoneNumber.asIs());
        entity.setNotes(notes.asIs());
        return dbLoader.saveMentor(entity).doOnError(throwable -> {
            entity.setName(originalName);
            entity.setPhoneNumber(originalPhoneNumber);
            entity.setEmailAddress(originalEmailAddress);
            entity.setNotes(originalNotes);
            Log.e(getClass().getName(), "Error saving mentor", throwable);
        });
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteMentor(entityLiveData.getValue()).doOnError(throwable -> Log.e(getClass().getName(), "Error deleting mentor", throwable));
    }

    private void onEntityLoaded(MentorEntity entity) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.onEntityLoaded");
        if (!fromInitializedState) {
            name.setValue(entity.getName());
            phoneNumber.setValue(entity.getPhoneNumber());
            emailAddress.setValue(entity.getEmailAddress());
            notes.setValue(entity.getNotes());
            if (null == entity.getId()) {
                onMentorNameTextChanged(name.toString(), entity);
                onPhoneNumberTextChanged(phoneNumber.toString(), entity);
                onEmailAddressTextChanged(emailAddress.toString(), entity);
                entityLiveData.postValue(entity);
                return;
            }
            nameChangedLiveData = new MutableLiveData<>(false);
            phoneNumberChangedLiveData = new MutableLiveData<>(false);
            emailAddressChangedLiveData = new MutableLiveData<>(false);
            notesChangedLiveData = new MutableLiveData<>(false);
        } else {
            if (null == entity.getId()) {
                entityLiveData.postValue(entity);
                return;
            }
            nameChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(name, entity.getName()));
            phoneNumberChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(phoneNumber, entity.getName()));
            emailAddressChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(emailAddress, entity.getName()));
            notesChangedLiveData = new MutableLiveData<>(!NormalizingCharSequence.equals(notes, entity.getNotes()));
        }
        savableLiveData.addSource(nameChangedLiveData);
        savableLiveData.addSource(phoneNumberChangedLiveData);
        savableLiveData.addSource(emailAddressChangedLiveData);
        savableLiveData.addSource(notesChangedLiveData);
        onMentorNameTextChanged(name.toString(), entity);
        onPhoneNumberTextChanged(phoneNumber.toString(), entity);
        onEmailAddressTextChanged(emailAddress.toString(), entity);
        entityLiveData.postValue(entity);
    }

    public void saveState(Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.saveState");
        outState.putBoolean(ARGUMENT_KEY_STATE_INITIALIZED, true);
        if (null != id) {
            outState.putLong(ARGUMENT_KEY_MENTOR_ID, id);
        }
        outState.putString(ARGUMENT_KEY_NAME, name.rawString());
        outState.putString(ARGUMENT_KEY_PHONE_NUMBER, phoneNumber.rawString());
        outState.putString(ARGUMENT_KEY_EMAIL_ADDRESS, emailAddress.rawString());
        outState.putString(ARGUMENT_KEY_NOTES, notes.rawString());
        MentorEntity termEntity = Objects.requireNonNull(entityLiveData.getValue());
        if (null == id) {
            outState.putString(ARGUMENT_KEY_ORIGINAL_NAME, termEntity.getName());
            outState.putString(ARGUMENT_KEY_ORIGINAL_PHONE_NUMBER, termEntity.getPhoneNumber());
            outState.putString(ARGUMENT_KEY_ORIGINAL_EMAIL_ADDRESS, termEntity.getEmailAddress());
            outState.putString(ARGUMENT_KEY_ORIGINAL_NOTES, termEntity.getNotes());
        }
    }

    public void onMentorNameTextChanged(String s) {
        onMentorNameTextChanged(s, Objects.requireNonNull(entityLiveData.getValue()));
    }

    void onMentorNameTextChanged(String s, MentorEntity entity) {
        String oldValue = name.toString();
        name.setValue(s);
        if (!NormalizingCharSequence.equals(name, oldValue)) {
            if (null != nameChangedLiveData) {
                String originalValue = entity.getName();
                if (oldValue.equals(originalValue)) {
                    nameChangedLiveData.postValue(true);
                } else if (NormalizingCharSequence.equals(name, originalValue)) {
                    nameChangedLiveData.postValue(false);
                }
            }
            if (oldValue.isEmpty()) {
                nameValidLiveData.postValue(true);
            } else if (name.isEmpty()) {
                nameValidLiveData.postValue(false);
            }
        }
    }

    public void onPhoneNumberTextChanged(String s) {
        onPhoneNumberTextChanged(s, Objects.requireNonNull(entityLiveData.getValue()));
    }

    void onPhoneNumberTextChanged(String s, MentorEntity entity) {
        String oldValue = phoneNumber.toString();
        phoneNumber.setValue(s);
        if (!NormalizingCharSequence.equals(phoneNumber, oldValue)) {
            if (null != phoneNumberChangedLiveData) {
                String originalValue = entity.getPhoneNumber();
                if (oldValue.equals(originalValue)) {
                    phoneNumberChangedLiveData.postValue(true);
                } else if (NormalizingCharSequence.equals(phoneNumber, originalValue)) {
                    phoneNumberChangedLiveData.postValue(false);
                }
            }
            if (!emailAddress.isEmpty()) {
                if (oldValue.isEmpty()) {
                    if (!phoneNumber.isEmpty()) {
                        contactValidLiveData.postValue(true);
                    }
                } else if (phoneNumber.isEmpty()) {
                    contactValidLiveData.postValue(false);
                }
            }
        }
    }

    public void onEmailAddressTextChanged(String s) {
        onEmailAddressTextChanged(s, Objects.requireNonNull(entityLiveData.getValue()));
    }

    void onEmailAddressTextChanged(String s, MentorEntity entity) {
        String oldValue = emailAddress.toString();
        emailAddress.setValue(s);
        if (!NormalizingCharSequence.equals(emailAddress, oldValue)) {
            if (null != emailAddressChangedLiveData) {
                String originalValue = entity.getPhoneNumber();
                if (oldValue.equals(originalValue)) {
                    emailAddressChangedLiveData.postValue(true);
                } else if (NormalizingCharSequence.equals(emailAddress, originalValue)) {
                    emailAddressChangedLiveData.postValue(false);
                }
            }
            if (!phoneNumber.isEmpty()) {
                if (oldValue.isEmpty()) {
                    if (!emailAddress.isEmpty()) {
                        contactValidLiveData.postValue(true);
                    }
                } else if (emailAddress.isEmpty()) {
                    contactValidLiveData.postValue(false);
                }
            }
        }
    }

    public void onMentorNotesEditTextChanged(String s) {
        onMentorNotesEditTextChanged(s, entityLiveData.getValue());
    }

    void onMentorNotesEditTextChanged(String s, MentorEntity entity) {
        String oldValue = notes.toString();
        notes.setValue(s);
        if (!NormalizingCharSequence.equals(notes, oldValue)) {
            String originalValue = entity.getNotes();
            if (oldValue.equals(originalValue)) {
                notesChangedLiveData.postValue(true);
            } else if (!NormalizingCharSequence.equals(notes, originalValue)) {
                notesChangedLiveData.postValue(false);
            }
        }
    }
}