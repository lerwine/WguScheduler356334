package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = "terms")
public class TermEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private LocalDate start;
    private LocalDate end;

    public TermEntity(int id, String name, LocalDate start, LocalDate end) {
        this(name, start, end);
        this.id = id;
    }

    @Ignore
    public TermEntity(String name, LocalDate start, LocalDate end) {
        this.name = Values.asNonNullAndWsNormalized(name);
        this.start = start;
        this.end = end;
    }

    @Ignore
    public TermEntity() {
        this(null, null, null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermEntity that = (TermEntity) o;
        if (id > 0) {
            return that.id == id;
        }
        return that.id < 1 && name.equals(that.name) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(name, start, end);
    }

    @Override
    public String toString() {
        return "TermEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
