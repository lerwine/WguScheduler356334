package Erwine.Leonard.T.wguscheduler356334.entity;

import java.time.LocalDate;

public interface HasTermProperties extends HasNotesProperty {
    String getName();

    LocalDate getStart();

    LocalDate getEnd();
}
