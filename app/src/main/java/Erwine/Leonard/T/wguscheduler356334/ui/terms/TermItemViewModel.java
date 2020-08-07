package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import android.util.Range;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = "terms")
public class TermItemViewModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private LocalDate start;
    private LocalDate end;

    @Ignore
    public TermItemViewModel() {

    }

    @Ignore
    public TermItemViewModel(String name, LocalDate start, LocalDate end) {
        this.name = Values.asNonNullAndWsNormalized(name);
        this.start = start;
        this.end = end;
    }

    public TermItemViewModel(int id, String name, LocalDate start, LocalDate end) {
        this(name, start, end);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
