package Erwine.Leonard.T.wguscheduler356334.util;

import android.annotation.SuppressLint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;

public class SampleData {

    private SampleData() {

    }

    @SuppressLint("DefaultLocale")
    public static List<TermEntity> getData() {
        List<TermEntity> data = new ArrayList<>();
        LocalDate termBreak = LocalDate.now().withMonth(5).withDayOfMonth(1);
        LocalDate date = termBreak.minusYears(2);
        for (int i = 0; i < 24; i++) {
            if (date.equals(termBreak)) {
                date = date.plusWeeks(2);
            }
            LocalDate next = date.plusMonths(6);
            data.add(new TermEntity(String.format("Term %d", i + 1), date, next.minusDays(1)));
            date = next;
        }
        return data;
    }

}
