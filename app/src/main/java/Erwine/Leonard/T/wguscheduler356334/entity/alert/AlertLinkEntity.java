package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import androidx.annotation.NonNull;

public interface AlertLinkEntity<T extends AlertLink> {
    @NonNull
    T getLink();

    @NonNull
    AlertEntity getAlert();
}
