package Erwine.Leonard.T.wguscheduler356334.entity.course;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

@DatabaseView(
        viewName = AppDb.VIEW_NAME_COURSE_DETAIL,
        value = "SELECT courses.*, " +
                "terms.name as [termName], terms.start as [termStart], terms.[end] as [termEnd], terms.notes as [termNotes], " +
                "mentors.name as [mentorName], mentors.phoneNumber, mentors.emailAddress, mentors.notes as [mentorNotes]\n" +
                "FROM courses LEFT JOIN mentors ON courses.mentorId = mentors.id\n" +
                "LEFT JOIN terms ON courses.termId = terms.id\n" +
                "GROUP BY courses.id ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]"
)
public final class CourseDetails extends AbstractCourseEntity<CourseDetails> {

    /**
     * The name of the {@link #termName "termName"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_NAME = "termName";

    /**
     * The name of the {@link #termStart "termStart"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_START = "termStart";

    /**
     * The name of the {@link #termEnd "termEnd"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_END = "termEnd";

    /**
     * The name of the {@link #termNotes "termNotes"} view column, which contains the name of the term.
     */
    public static final String COLNAME_TERM_NOTES = "termNotes";

    /**
     * The name of the {@link #mentorName "mentorName"} view column, which contains the name of the course mentor.
     */
    public static final String COLNAME_MENTOR_NAME = "mentorName";

    /**
     * The name of the {@link #phoneNumber "phoneNumber"} view column, which contains the phone number for the course mentor.
     */
    public static final String COLNAME_PHONE_NUMBER = "phoneNumber";

    /**
     * The name of the {@link #emailAddress "emailAddress"} view column, which contains the email address for the course mentor.
     */
    public static final String COLNAME_EMAIL_ADDRESS = "emailAddress";

    /**
     * The name of the {@link #mentorNotes "mentorNotes"} view column, which contains the name of the course mentor.
     */
    public static final String COLNAME_MENTOR_NOTES = "mentorNotes";

    @Ignore
    private AbstractTermEntity<?> term;
    @Ignore
    private AbstractMentorEntity<?> mentor;
    @ColumnInfo(name = COLNAME_TERM_NAME)
    private String termName;
    @ColumnInfo(name = COLNAME_TERM_START)
    private LocalDate termStart;
    @ColumnInfo(name = COLNAME_TERM_END)
    private LocalDate termEnd;
    @ColumnInfo(name = COLNAME_TERM_NOTES)
    private String termNotes;
    @ColumnInfo(name = COLNAME_MENTOR_NAME)
    private String mentorName;
    @ColumnInfo(name = COLNAME_PHONE_NUMBER)
    private String phoneNumber;
    @ColumnInfo(name = COLNAME_EMAIL_ADDRESS)
    private String emailAddress;
    @ColumnInfo(name = COLNAME_MENTOR_NOTES)
    private String mentorNotes;

    public CourseDetails(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart, LocalDate expectedEnd, LocalDate actualEnd,
                         int competencyUnits, String notes, long termId, Long mentorId, String termName, LocalDate termStart, LocalDate termEnd, String termNotes,
                         String mentorName, String phoneNumber, String emailAddress, String mentorNotes, long id) {
        super(id, termId, mentorId, number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        setTerm(new TermEntity(termName, termStart, termEnd, termNotes, termId));
        if (null == mentorId) {
            setMentor(null);
        } else {
            setMentor(new MentorEntity(mentorName, phoneNumber, emailAddress, mentorNotes, mentorId));
        }
    }

    @Ignore
    public CourseDetails(@NonNull CourseEntity course, @NonNull AbstractTermEntity<?> term, @Nullable AbstractMentorEntity<?> mentor) {
        super(course);
        setTerm(term);
        setMentor(mentor);
    }

    @Ignore
    public CourseDetails(AbstractTermEntity<?> term) {
        super(ID_NEW, (null == term) ? ID_NEW : term.getId(), null, null, null, CourseStatus.UNPLANNED, null, null, null, null, 0, null);
        if (null != term) {
            setTerm(term);
        } else {
            termName = termNotes = "";
            termStart = termEnd = null;
        }
        setMentor(null);
    }

    @Nullable
    public AbstractTermEntity<?> getTerm() {
        return term;
    }

    public synchronized void setTerm(@NonNull AbstractTermEntity<?> term) {
        long id = IdIndexedEntity.assertNotNewId(term.getId());
        this.term = term;
        super.setTermId(id);
        this.termName = term.getName();
        this.termStart = term.getStart();
        this.termEnd = term.getEnd();
        this.termNotes = term.getNotes();
    }

    @Nullable
    public AbstractMentorEntity<?> getMentor() {
        return mentor;
    }

    public synchronized void setMentor(@Nullable AbstractMentorEntity<?> mentor) {
        this.mentor = mentor;
        if (null == mentor) {
            super.setMentorId(null);
            this.mentorName = this.phoneNumber = this.emailAddress = this.mentorNotes = "";
        } else {
            long id = IdIndexedEntity.assertNotNewId(mentor.getId());
            super.setMentorId(id);
            this.mentorName = mentor.getName();
            this.phoneNumber = mentor.getPhoneNumber();
            this.emailAddress = mentor.getEmailAddress();
            this.mentorNotes = mentor.getNotes();
        }
    }

    @Override
    public void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        super.restoreState(bundle, isOriginal);
        TermEntity t = new TermEntity();
        if (bundle.containsKey(t.stateKey(Term.COLNAME_ID, isOriginal)) || bundle.containsKey(t.stateKey(Term.COLNAME_NAME, isOriginal))) {
            t.restoreState(bundle, isOriginal);
            setTerm(t);
        }
        MentorEntity m = new MentorEntity();
        if (bundle.containsKey(m.stateKey(Mentor.COLNAME_ID, isOriginal)) || bundle.containsKey(m.stateKey(Mentor.COLNAME_NAME, isOriginal))) {
            m.restoreState(bundle, isOriginal);
            setMentor(m);
        }
    }

    @Override
    public synchronized void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        super.saveState(bundle, isOriginal);
        if (null != term) {
            term.saveState(bundle, isOriginal);
        }
        if (null != mentor) {
            mentor.saveState(bundle, isOriginal);
        }
    }

    public CourseEntity toEntity() {
        return new CourseEntity(this);
    }

    public synchronized void applyEntity(@NonNull CourseEntity source, @NonNull List<? extends AbstractTermEntity<?>> terms, @NonNull List<? extends AbstractMentorEntity<?>> mentors) {
        long currentId = getId();
        long sourceId = source.getId();
        Long termId;
        if (ID_NEW == sourceId || !(ID_NEW == currentId || currentId == sourceId) || ID_NEW == (termId = source.getTermId())) {
            throw new IllegalArgumentException();
        }
        AbstractTermEntity<?> newTerm = terms.stream().filter(t -> termId.equals(t.getId())).findFirst().orElseThrow(() -> new IllegalArgumentException("Matching term not found"));
        Long mentorId = source.getMentorId();
        AbstractMentorEntity<?> newMentor = (null == mentorId) ? null : mentors.stream().filter(t -> mentorId.equals(t.getId())).findFirst().orElseThrow(() -> new IllegalArgumentException("Matching mentor not found"));
        if (ID_NEW == currentId) {
            setId(sourceId);
        }
        setTerm(newTerm);
        setMentor(newMentor);
        setNumber(source.getNumber());
        setTitle(source.getTitle());
        setStatus(source.getStatus());
        setExpectedStart(source.getExpectedStart());
        setActualStart(source.getActualStart());
        setExpectedEnd(source.getExpectedEnd());
        setActualEnd(source.getActualEnd());
        setCompetencyUnits(source.getCompetencyUnits());
        setNotes(source.getNotes());
    }

    @NonNull
    @Override
    public String toString() {
        return ToStringBuilder.toEscapedString(this, false);
    }

    @Override
    public void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        super.appendPropertiesAsStrings(sb);
        sb.append("term", term, false).append("mentor", mentor, false);
    }

}
