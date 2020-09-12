package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationTracker;

public class MentorEditState {
    private final LiveData<MentorEntity> mLiveData;
    private final LiveData<List<PhoneNumberEntity>> mEditingPhoneNumbers;
    private final ValidationTracker<PhoneNumberEntity> mPhoneNumberValidation;
    private final LiveData<List<EmailAddressEntity>> mEditingEmailAddresses;
    private final ValidationTracker<EmailAddressEntity> mEmailAddressValidation;
    private final LiveValid mNameValid;
    private final MediatorLiveData<Boolean> mValid;
    private final PropertyChangeListener mMentorNamePropertyChangeListener;
    private MentorEntity attachedMentor;

    public MentorEditState(LiveData<MentorEntity> liveData, LiveData<List<PhoneNumberEntity>> editingPhoneNumbers, LiveData<List<EmailAddressEntity>> editingEmailAddresses) {
        mLiveData = liveData;
        mEditingPhoneNumbers = editingPhoneNumbers;
        mPhoneNumberValidation = new ValidationTracker<>();
        mEditingEmailAddresses = editingEmailAddresses;
        mEmailAddressValidation = new ValidationTracker<>();
        mNameValid = new LiveValid();
        mValid = new MediatorLiveData<>();
        mValid.addSource(mNameValid, v -> mValid.setValue(v && mPhoneNumberValidation.isValid() && mEmailAddressValidation.isValid()));
        mValid.addSource(mPhoneNumberValidation.getIsValidLiveData(), v -> mValid.setValue(v && mNameValid.getValue() && mEmailAddressValidation.isValid()));
        mValid.addSource(mEmailAddressValidation.getIsValidLiveData(), v -> mValid.setValue(v && mNameValid.getValue() && mPhoneNumberValidation.isValid()));
        liveData.observeForever(this::onMentorChanged);
        editingPhoneNumbers.observeForever(this::onPhoneNumbersChanged);
        editingEmailAddresses.observeForever(this::onEmailAddressesChanged);
        mMentorNamePropertyChangeListener = this::onMentorNamePropertyChanged;
    }

    public LiveData<MentorEntity> getLiveData() {
        return mLiveData;
    }

//    public boolean isNameValid() {
//        return mNameValid.postedValue;
//    }

    public LiveData<Boolean> getNameValid() {
        return mNameValid;
    }

//    public boolean isPhoneValid() {
//        return mPhoneNumberValidation.getIsValidLiveData().getValue();
//    }

    public LiveData<Boolean> getPhoneValid() {
        return mPhoneNumberValidation.getIsValidLiveData();
    }

//    public boolean isEmailValid() {
//        return mEmailAddressValidation.getIsValidLiveData().getValue();
//    }

    public LiveData<Boolean> getEmailValid() {
        return mEmailAddressValidation.getIsValidLiveData();
    }

    public LiveData<Boolean> getIsValidLiveData() {
        return mValid;
    }

    private void onMentorNamePropertyChanged(PropertyChangeEvent propertyChangeEvent) {
        Log.i(getClass().getName(), "Mentor name changed");
        validateMentorName();
    }

    private synchronized void validateMentorName() {
        mNameValid.set(null != attachedMentor && !attachedMentor.getName().isEmpty());
    }

    private synchronized void onMentorChanged(MentorEntity mentorEntity) {
        if (null != attachedMentor) {
            if (null == mentorEntity) {
                attachedMentor.removePropertyChangeListener(MentorEntity.COLNAME_NAME, mMentorNamePropertyChangeListener);
                attachedMentor = null;
                mNameValid.set(false);
                Log.i(getClass().getName(), "Mentor changed");
                return;
            }
            if (mentorEntity == attachedMentor) {
                return;
            }
            attachedMentor.removePropertyChangeListener(MentorEntity.COLNAME_NAME, mMentorNamePropertyChangeListener);
        } else if (null == mentorEntity) {
            return;
        }
        attachedMentor = mentorEntity;
        attachedMentor.ensurePropertyChangeListener(MentorEntity.COLNAME_NAME, mMentorNamePropertyChangeListener);
        Log.i(getClass().getName(), "Mentor changed");
        validateMentorName();
    }

    private synchronized void onPhoneNumbersChanged(List<PhoneNumberEntity> phoneNumberEntities) {
        mPhoneNumberValidation.setAll(phoneNumberEntities);
        Log.i(getClass().getName(), "Phone numbers changed");
    }

    private void onEmailAddressesChanged(List<EmailAddressEntity> emailAddressEntities) {
        mEmailAddressValidation.setAll(emailAddressEntities);
        Log.i(getClass().getName(), "Email addresses changed");
    }

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

}
