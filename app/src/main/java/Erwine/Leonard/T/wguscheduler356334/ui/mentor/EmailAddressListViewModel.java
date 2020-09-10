package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationAggregate;

public class EmailAddressListViewModel extends AndroidViewModel {
    private final DbLoader dbLoader;
    private final LiveData<List<EmailAddressEntity>> mLiveData;
    private final ValidationAggregate.AutoValidate<String> validator;

    public EmailAddressListViewModel(@NonNull LifecycleOwner owner, @NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        mLiveData = dbLoader.getEmailAddresses();
        validator = new ValidationAggregate.AutoValidate<>(t -> !t.isEmpty());
        validator.addAllValues(mLiveData.getValue().stream().map(t -> t.getValue()));
        mLiveData.observe(owner, this::onLiveDataChanged);
    }

    private void onLiveDataChanged(List<EmailAddressEntity> emailAddressEntities) {
        validator.synchronize(emailAddressEntities.stream().map(t -> t.getValue()));
    }

    public LiveData<List<EmailAddressEntity>> getLiveData() {
        return mLiveData;
    }
}
