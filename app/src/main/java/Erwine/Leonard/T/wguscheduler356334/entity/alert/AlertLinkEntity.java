package Erwine.Leonard.T.wguscheduler356334.entity.alert;

import androidx.annotation.NonNull;

import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuildable;

public interface AlertLinkEntity<T extends AlertLink> extends ToStringBuildable {
    @NonNull
    T getLink();

    @NonNull
    AlertEntity getAlert();
}
