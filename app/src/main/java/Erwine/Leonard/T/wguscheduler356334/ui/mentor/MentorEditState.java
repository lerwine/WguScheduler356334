package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;

public class MentorEditState {
    private final LiveData<MentorEntity> mLiveData;
    private final LiveData<List<PhoneNumberEntity>> mEditingPhoneNumbers;
    private final LiveData<List<EmailAddressEntity>> mEditingEmailAddresses;
    private final HashMap<Long, PhoneNumberEntity> mAttachedPhoneNumberEntities;
    private final HashMap<Long, EmailAddressEntity> mAttachedEmailAddressEntities;
    private final LiveValid mNameValid;
    private final LiveValid mPhoneValid;
    private final LiveValid mEmailValid;
    private final LiveValid mValid;
    private final PropertyChangeListener mMentorPropertyChangeListener;
    private final PropertyChangeListener mPhoneNumberPropertyChangeListener;
    private final PropertyChangeListener mEmailAddressPropertyChangeListener;
    private MentorEntity attachedMentor;

    private class LiveValid extends LiveData<Boolean> {
        private boolean postedValue;

        private LiveValid() {
            super(false);
            postedValue = false;
        }

        private synchronized void set(boolean value) {
            if (value != postedValue) {
                postedValue = value;
                postValue(value);
            }
        }
    }

    public MentorEditState(LiveData<MentorEntity> liveData, LiveData<List<PhoneNumberEntity>> editingPhoneNumbers, LiveData<List<EmailAddressEntity>> editingEmailAddresses) {
        mLiveData = liveData;
        mEditingPhoneNumbers = editingPhoneNumbers;
        mEditingEmailAddresses = editingEmailAddresses;
        mAttachedPhoneNumberEntities = new HashMap<>();
        mAttachedEmailAddressEntities = new HashMap<>();
        mNameValid = new LiveValid();
        mPhoneValid = new LiveValid();
        mEmailValid = new LiveValid();
        mValid = new LiveValid();
        mMentorPropertyChangeListener = this::onMentorPropertyChanged;
        mPhoneNumberPropertyChangeListener = this::onPhoneNumberPropertyChanged;
        mEmailAddressPropertyChangeListener = this::onEmailAddressPropertyChanged;
        liveData.observeForever(this::onMentorChanged);
        editingPhoneNumbers.observeForever(this::onPhoneNumbersChanged);
        editingEmailAddresses.observeForever(this::onEmailAddressesChanged);
    }

    public LiveData<MentorEntity> getLiveData() {
        return mLiveData;
    }

    public boolean isNameValid() {
        return mNameValid.postedValue;
    }

    public LiveData<Boolean> getNameValid() {
        return mNameValid;
    }

    public boolean isPhoneValid() {
        return mPhoneValid.postedValue;
    }

    public LiveData<Boolean> getPhoneValid() {
        return mPhoneValid;
    }

    public boolean isEmailValid() {
        return mEmailValid.postedValue;
    }

    public LiveData<Boolean> getEmailValid() {
        return mEmailValid;
    }

    private void onMentorPropertyChanged(PropertyChangeEvent propertyChangeEvent) {
        String propertyName = propertyChangeEvent.getPropertyName();
        if (null != propertyName && propertyName.equals(MentorEntity.COLNAME_NAME)) {
            Log.i(getClass().getName(), "Mentor name changed");
            validateMentorName();
        }
    }

    private synchronized void validateMentorName() {
        if (null == attachedMentor || attachedMentor.getName().isEmpty()) {
            mNameValid.set(false);
            mValid.set(false);
        } else {
            mNameValid.set(true);
            mValid.set(mPhoneValid.postedValue && mEmailValid.postedValue);
        }
    }

    private synchronized void onMentorChanged(MentorEntity mentorEntity) {
        if (null != attachedMentor) {
            if (null == mentorEntity) {
                attachedMentor.removePropertyChangeListener(mMentorPropertyChangeListener);
                attachedMentor = null;
                mNameValid.set(false);
                mValid.set(false);
                Log.i(getClass().getName(), "Mentor changed");
                return;
            }
            if (mentorEntity == attachedMentor) {
                return;
            }
            attachedMentor.removePropertyChangeListener(mMentorPropertyChangeListener);
        } else if (null == mentorEntity) {
            return;
        }
        attachedMentor = mentorEntity;
        attachedMentor.addPropertyChangeListener(mMentorPropertyChangeListener);
        Log.i(getClass().getName(), "Mentor changed");
        validateMentorName();
    }

    private synchronized void onPhoneNumbersChanged(List<PhoneNumberEntity> phoneNumberEntities) {
        if (phoneNumberEntities.isEmpty()) {
            if (!mAttachedPhoneNumberEntities.isEmpty()) {
                mAttachedPhoneNumberEntities.values().forEach(t -> t.removePropertyChangeListener(mPhoneNumberPropertyChangeListener));
                mAttachedPhoneNumberEntities.clear();
                mPhoneValid.set(false);
                mValid.set(false);
                Log.i(getClass().getName(), "Phone numbers changed");
            }
            return;
        }

        List<PhoneNumberEntity> removed = mAttachedPhoneNumberEntities.values().stream().filter(t -> phoneNumberEntities.stream().allMatch(u -> u != t))
                .collect(Collectors.toList());
        boolean valid;
        if (removed.isEmpty()) {
            valid = false;
        } else {
            removed.forEach(t -> t.removePropertyChangeListener(mPhoneNumberPropertyChangeListener));
            if (removed.size() == mAttachedPhoneNumberEntities.size()) {
                valid = false;
                mAttachedPhoneNumberEntities.clear();
            } else {
                removed.forEach(t -> mAttachedPhoneNumberEntities.remove(t.getId()));
                valid = mAttachedPhoneNumberEntities.values().stream().anyMatch(t -> !t.getValue().isEmpty());
            }
        }
        valid = phoneNumberEntities.stream().reduce(valid, (t, u) -> {
            if (mAttachedPhoneNumberEntities.containsKey(u.getId())) {
                return false;
            }
            mAttachedPhoneNumberEntities.put(u.getId(), u);
            u.addPropertyChangeListener(mPhoneNumberPropertyChangeListener);
            return !u.getValue().isEmpty();
        }, (t, u) -> t || u);
        mPhoneValid.set(valid);
        mValid.set(valid && mNameValid.postedValue && mEmailValid.postedValue);
        Log.i(getClass().getName(), "Phone numbers changed");
    }

    private void onEmailAddressesChanged(List<EmailAddressEntity> emailAddressEntities) {
        if (emailAddressEntities.isEmpty()) {
            if (!mAttachedEmailAddressEntities.isEmpty()) {
                mAttachedEmailAddressEntities.values().forEach(t -> t.removePropertyChangeListener(mEmailAddressPropertyChangeListener));
                mAttachedEmailAddressEntities.clear();
                mEmailValid.set(false);
                mValid.set(false);
                Log.i(getClass().getName(), "Email addresses changed");
            }
            return;
        }

        List<EmailAddressEntity> removed = mAttachedEmailAddressEntities.values().stream().filter(t -> emailAddressEntities.stream().allMatch(u -> u != t))
                .collect(Collectors.toList());
        boolean valid;
        if (removed.isEmpty()) {
            valid = false;
        } else {
            removed.forEach(t -> t.removePropertyChangeListener(mEmailAddressPropertyChangeListener));
            if (removed.size() == mAttachedEmailAddressEntities.size()) {
                valid = false;
                mAttachedEmailAddressEntities.clear();
            } else {
                removed.forEach(t -> mAttachedEmailAddressEntities.remove(t.getId()));
                valid = mAttachedEmailAddressEntities.values().stream().anyMatch(t -> !t.getValue().isEmpty());
            }
        }
        valid = emailAddressEntities.stream().reduce(valid, (t, u) -> {
            if (mAttachedEmailAddressEntities.containsKey(u.getId())) {
                return false;
            }
            mAttachedEmailAddressEntities.put(u.getId(), u);
            u.addPropertyChangeListener(mEmailAddressPropertyChangeListener);
            return !u.getValue().isEmpty();
        }, (t, u) -> t || u);
        mEmailValid.set(valid);
        mValid.set(valid && mNameValid.postedValue && mPhoneValid.postedValue);
        Log.i(getClass().getName(), "Email addresses changed");
    }

    private void onPhoneNumberPropertyChanged(PropertyChangeEvent propertyChangeEvent) {
        PhoneNumberEntity entity = (PhoneNumberEntity) propertyChangeEvent.getSource();
        boolean valid;
        if (entity.getValue().isEmpty()) {
            valid = mAttachedPhoneNumberEntities.size() > 1 && mAttachedPhoneNumberEntities.values().stream().anyMatch(t -> !t.getValue().isEmpty());
        } else {
            valid = true;
        }
        mPhoneValid.set(valid);
        mValid.set(valid && mNameValid.postedValue && mEmailValid.postedValue);
        Log.i(getClass().getName(), "Single phone number changed");
    }

    private void onEmailAddressPropertyChanged(PropertyChangeEvent propertyChangeEvent) {
        EmailAddressEntity entity = (EmailAddressEntity) propertyChangeEvent.getSource();
        boolean valid;
        if (entity.getValue().isEmpty()) {
            valid = mAttachedEmailAddressEntities.size() > 1 && mAttachedEmailAddressEntities.values().stream().anyMatch(t -> !t.getValue().isEmpty());
        } else {
            valid = true;
        }
        mEmailValid.set(valid);
        mValid.set(valid && mNameValid.postedValue && mPhoneValid.postedValue);
        Log.i(getClass().getName(), "Single mail addresses changed");
    }

}
