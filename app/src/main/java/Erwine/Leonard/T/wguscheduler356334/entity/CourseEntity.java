package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentDAO;
import Erwine.Leonard.T.wguscheduler356334.db.CourseDAO;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_COURSES, indices = {
        @Index(value = CourseEntity.COLNAME_TERM_ID, name = CourseEntity.INDEX_TERM),
        @Index(value = CourseEntity.COLNAME_MENTOR_ID, name = CourseEntity.INDEX_MENTOR),
        @Index(value = {CourseEntity.COLNAME_NUMBER, CourseEntity.COLNAME_TERM_ID}, name = CourseEntity.INDEX_NUMBER, unique = true)
})
public class CourseEntity {

    public static final String INDEX_TERM = "IDX_COURSE_TERM";
    public static final String INDEX_MENTOR = "IDX_COURSE_MENTOR";
    public static final String INDEX_NUMBER = "IDX_COURSE_NUMBER";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_TERM_ID = "termId";
    public static final String COLNAME_MENTOR_ID = "mentorId";
    public static final String COLNAME_NUMBER = "number";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ForeignKey(entity = TermEntity.class, parentColumns = {TermEntity.COLNAME_ID}, childColumns = {COLNAME_TERM_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_TERM_ID)
    private int termId;
    @ForeignKey(entity = MentorEntity.class, parentColumns = {MentorEntity.COLNAME_ID}, childColumns = {COLNAME_MENTOR_ID}, onDelete = ForeignKey.RESTRICT, deferred = true)
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private Integer mentorId;
    @ColumnInfo(name = COLNAME_NUMBER)
    private String number;
    private String title;
    private LocalDate expectedStart;
    private LocalDate actualStart;
    private LocalDate expectedEnd;
    private LocalDate actualEnd;
    private CourseStatus status;
    private int competencyUnits;
    private String notes;
    @Ignore
    private LiveData<List<AssessmentEntity>> assessments;

    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, int termId, Integer mentorId, int id) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes, termId, mentorId);
        this.id = id;
    }

    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes, int termId, Integer mentorId) {
        this(number, title, status, expectedStart, actualStart, expectedEnd, actualEnd, competencyUnits, notes);
        this.termId = termId;
        this.mentorId = mentorId;
    }

    @Ignore
    public CourseEntity(String number, String title, CourseStatus status, LocalDate expectedStart, LocalDate actualStart,
                        LocalDate expectedEnd, LocalDate actualEnd, Integer competencyUnits, String notes) {
        this.number = Values.asNonNullAndWsNormalized(number);
        this.title = Values.asNonNullAndWsNormalized(title);
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
        this.expectedStart = expectedStart;
        this.actualStart = actualStart;
        this.expectedEnd = expectedEnd;
        this.actualEnd = actualEnd;
        this.competencyUnits = (null == competencyUnits) ? 0 : competencyUnits;
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    @Ignore
    public CourseEntity() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public static void populateSampleData(AppDb appDb, HashMap<String, TermEntity.SampleData> allTerms) {
        HashMap<String, MentorEntity> allMentors = MentorEntity.populateSampleData(appDb);
        CourseDAO courseDAO = appDb.courseDAO();
        int id = allTerms.get(TermEntity.SAMPLE_TERM_1).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("ORA", "Orientation", CourseStatus.PASSED, LocalDate.of(2015, 4, 15),
                        LocalDate.of(2015, 4, 15), LocalDate.of(2015, 4, 21), LocalDate.of(2015, 4, 30),
                        0, "", id, null),
                new CourseEntity("C182", "Introduction to IT", CourseStatus.PASSED, LocalDate.of(2015, 9, 1),
                        LocalDate.of(2015, 5, 1), LocalDate.of(2015, 9, 30), LocalDate.of(2015, 6, 9),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_SCOTT_STROMBERG).getId()),
                new CourseEntity("C483", "Principles of Management", CourseStatus.PASSED, LocalDate.of(2015, 8, 1),
                        LocalDate.of(2015, 6, 10), LocalDate.of(2015, 8, 31), LocalDate.of(2015, 8, 28),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_ANDY_DOREN).getId()),
                new CourseEntity("C176", "Business of IT - Project Management", CourseStatus.PASSED, LocalDate.of(2015, 7, 1),
                        LocalDate.of(2015, 8, 29), LocalDate.of(2015, 7, 31), LocalDate.of(2015, 10, 23),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_KATIE_CRAIG).getId())
        );
        AssessmentDAO assessmentDAO = appDb.assessmentDAO();
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "ORA":
                    assessmentDAO.insertItem(new AssessmentEntity("ORA1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "",
                            LocalDate.of(2015, 4, 21), t.getId()));
                    break; // P
                case "C182":
                    assessmentDAO.insertItem(new AssessmentEntity("PAG5", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2015, 5, 4), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("GSC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "",
                            t.getActualEnd(), t.getId()));
                    break; // O
                case "C483":
                    assessmentDAO.insertItem(new AssessmentEntity("PIAC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2015, 8, 23), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("IAC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "",
                            t.getActualEnd(), t.getId()));
                    break; // O
                case "C176":
                    assessmentDAO.insertItem(new AssessmentEntity("EKV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "CompTIA - Project+",
                            t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_2).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C168", "Critical Thinking and Logic", CourseStatus.PASSED, LocalDate.of(2016, 1, 1),
                        LocalDate.of(2016, 1, 1), LocalDate.of(2016, 2, 22), LocalDate.of(2016, 2, 22),
                        3, "was \"Reasoning and Problem Solving\"", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_STEVEN_HARRIS).getId()),
                new CourseEntity("C132", "Elements of Effective Communication", CourseStatus.PASSED, LocalDate.of(2016, 2, 23),
                        LocalDate.of(2016, 2, 23), LocalDate.of(2016, 5, 7), LocalDate.of(2016, 5, 7),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_SABRENA_PARTON).getId()),
                new CourseEntity("C455", "English Composition I", CourseStatus.PASSED, LocalDate.of(2016, 5, 6),
                        LocalDate.of(2016, 5, 6), LocalDate.of(2016, 6, 5), LocalDate.of(2016, 6, 5),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_ABIGAIL_SCHEG).getId()),
                new CourseEntity("C456", "English Composition II", CourseStatus.PASSED, LocalDate.of(2016, 6, 6),
                        LocalDate.of(2016, 6, 6), LocalDate.of(2016, 7, 5), LocalDate.of(2016, 7, 5),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_ABIGAIL_SCHEG).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C168":
                    assessmentDAO.insertItem(new AssessmentEntity("PLMC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2016, 1, 17), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("LMC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C132":
                    assessmentDAO.insertItem(new AssessmentEntity("PYJC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2016, 5, 5), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("YJC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "",
                            LocalDate.of(2016, 5, 7), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("YJT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // P O
                case "C455":
                    assessmentDAO.insertItem(new AssessmentEntity("DIT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 1", LocalDate.of(2016, 5, 15), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("DIT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 2", LocalDate.of(2016, 5, 22), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("DIT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 3", LocalDate.of(2016, 5, 28), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("DIT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 4", t.getActualEnd(), t.getId()));
                    break; // P
                case "C456":
                    assessmentDAO.insertItem(new AssessmentEntity("DJT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 1", t.getActualEnd(), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("DJT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 2", t.getActualEnd(), t.getId()));
                    break; // P
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_3).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C393", "IT Foundations", CourseStatus.PASSED, LocalDate.of(2016, 7, 1),
                        LocalDate.of(2016, 7, 6), LocalDate.of(2016, 10, 22), LocalDate.of(2016, 10, 22),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_CONSTANCE_BLANSON).getId()),
                new CourseEntity("C278", "College Algebra", CourseStatus.PASSED, LocalDate.of(2016, 10, 23),
                        LocalDate.of(2016, 10, 23), LocalDate.of(2016, 12, 28), LocalDate.of(2016, 12, 28),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JEFF_EDMUNDS).getId()),
                new CourseEntity("C394", "IT Applications", CourseStatus.PASSED, LocalDate.of(2016, 12, 28),
                        LocalDate.of(2016, 12, 28), LocalDate.of(2016, 12, 28), LocalDate.of(2016, 12, 28),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_CONSTANCE_BLANSON).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C393":
                    assessmentDAO.insertItem(new AssessmentEntity("KEV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "CompTIA A+ Part 1/2", t.getActualEnd(), t.getId()));
                    break; // O
                case "C278":
                    assessmentDAO.insertItem(new AssessmentEntity("PCEC", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2016, 12, 13), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("CEC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C394":
                    assessmentDAO.insertItem(new AssessmentEntity("KFV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "CompTIA - A+", t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_4).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C173", "Scripting and Programming - Foundations", CourseStatus.PASSED, LocalDate.of(2017, 2, 1),
                        LocalDate.of(2017, 2, 1), LocalDate.of(2017, 2, 14), LocalDate.of(2017, 2, 22),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JESSICA_SHIELDS).getId()),
                new CourseEntity("C100", "Introduction to Humanities", CourseStatus.PASSED, LocalDate.of(2017, 2, 15),
                        LocalDate.of(2017, 2, 23), LocalDate.of(2017, 4, 6), LocalDate.of(2017, 5, 8),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MICHAEL_SMITH).getId()),
                new CourseEntity("C169", "Scripting and Programming - Applications", CourseStatus.PASSED, LocalDate.of(2017, 3, 1),
                        LocalDate.of(2017, 5, 9), LocalDate.of(2017, 4, 29), LocalDate.of(2017, 5, 23),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_ED_LAVIERI).getId()),
                new CourseEntity("C255", "Introduction to Geography", CourseStatus.PASSED, LocalDate.of(2017, 4, 7),
                        LocalDate.of(2017, 5, 24), LocalDate.of(2017, 5, 29), LocalDate.of(2017, 7, 22),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MASON_MCWATTERS).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C173":
                    assessmentDAO.insertItem(new AssessmentEntity("PCGO", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 1, 5), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("CGO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C100":
                    assessmentDAO.insertItem(new AssessmentEntity("PCKC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 2, 22), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("PCKC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 5, 3), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("CKC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "",
                            LocalDate.of(2017, 5, 8), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("UXT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P O
                case "C169":
                    assessmentDAO.insertItem(new AssessmentEntity("PDQC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 5, 9), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("VGT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "",
                            LocalDate.of(2017, 5, 21), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("DQC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O P
                case "C255":
                    assessmentDAO.insertItem(new AssessmentEntity("PJQC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 7, 19), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("JQC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_5).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C165", "Integrated Physical Sciences", CourseStatus.PASSED, LocalDate.of(2017, 8, 1),
                        LocalDate.of(2017, 8, 1), LocalDate.of(2017, 8, 11), LocalDate.of(2017, 8, 11),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_KANDI_DUFF).getId()),
                new CourseEntity("C683", "Natural Science Lab", CourseStatus.PASSED, LocalDate.of(2017, 8, 12),
                        LocalDate.of(2017, 8, 13), LocalDate.of(2017, 10, 30), LocalDate.of(2017, 10, 30),
                        2, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JESSICA_SHIELDS).getId()),
                new CourseEntity("C779", "Web Development Foundations", CourseStatus.PASSED, LocalDate.of(2017, 10, 31),
                        LocalDate.of(2017, 10, 31), LocalDate.of(2017, 11, 29), LocalDate.of(2017, 11, 29),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JESSICA_SHIELDS).getId()),
                new CourseEntity("C484", "Organizational Behavior and Leadership", CourseStatus.PASSED, LocalDate.of(2017, 11, 30),
                        LocalDate.of(2017, 11, 30), LocalDate.of(2018, 1, 3), LocalDate.of(2018, 1, 3),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JIM_JIVIDEN).getId()),
                new CourseEntity("C459", "Introduction to Probability and Statistics", CourseStatus.PASSED, LocalDate.of(2018, 1, 4),
                        LocalDate.of(2018, 1, 4), LocalDate.of(2018, 1, 31), LocalDate.of(2018, 1, 31),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_NICK_MEYER).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C165":
                    assessmentDAO.insertItem(new AssessmentEntity("PHTC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 8, 2), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("HTC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C683":
                    assessmentDAO.insertItem(new AssessmentEntity("BRP1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
                case "C779":
                    assessmentDAO.insertItem(new AssessmentEntity("HCV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C484":
                    assessmentDAO.insertItem(new AssessmentEntity("PIBC", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2017, 12, 26), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("IBC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C459":
                    assessmentDAO.insertItem(new AssessmentEntity("GVC1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_6).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C175", "Data Management - Foundations", CourseStatus.PASSED, LocalDate.of(2018, 2, 1),
                        LocalDate.of(2018, 2, 1), LocalDate.of(2018, 6, 14), LocalDate.of(2018, 5, 15),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_RANDY_RUTLEDGE).getId()),
                new CourseEntity("C170", "Data Management - Applications", CourseStatus.PASSED, LocalDate.of(2018, 6, 14),
                        LocalDate.of(2018, 5, 16), LocalDate.of(2018, 7, 31), LocalDate.of(2018, 7, 28),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MARIA_SCHENK).getId()),
                new CourseEntity("C773", "User Interface Design", CourseStatus.PASSED, LocalDate.of(2018, 3, 1),
                        LocalDate.of(2018, 7, 29), LocalDate.of(2018, 3, 31), LocalDate.of(2018, 7, 27),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JESSICA_SHIELDS).getId()),
                new CourseEntity("C768", "Technical Communication", CourseStatus.PASSED, LocalDate.of(2018, 2, 15),
                        LocalDate.of(2018, 7, 28), LocalDate.of(2018, 2, 28), LocalDate.of(2018, 8, 9),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JOE_BARNHART).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C175":
                    assessmentDAO.insertItem(new AssessmentEntity("PFKO", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2018, 4, 13), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("FKO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C170":
                    assessmentDAO.insertItem(new AssessmentEntity("PFJO", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2018, 5, 15), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("FJO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "",
                            LocalDate.of(2018, 6, 2), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("VHT1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P O
                case "C773":
                    assessmentDAO.insertItem(new AssessmentEntity("FPV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C768":
                    assessmentDAO.insertItem(new AssessmentEntity("EWP1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_7).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C188", "Software Engineering", CourseStatus.NOT_PASSED, LocalDate.of(2018, 8, 1),
                        LocalDate.of(2018, 8, 1), LocalDate.of(2018, 9, 30), LocalDate.of(2018, 11, 20),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_WANDA_BURWICK).getId()),
                new CourseEntity("C191", "Operating Systems for Programmers", CourseStatus.PASSED, LocalDate.of(2018, 12, 1),
                        LocalDate.of(2018, 11, 21), LocalDate.of(2018, 1, 31), LocalDate.of(2019, 1, 30),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_WANDA_BURWICK).getId()),
                new CourseEntity("C777", "Web Development Applications", CourseStatus.PASSED, LocalDate.of(2018, 10, 1),
                        LocalDate.of(2019, 1, 31), LocalDate.of(2018, 11, 30), LocalDate.of(2019, 2, 1),
                        6, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_JESSICA_SHIELDS).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C777":
                    assessmentDAO.insertItem(new AssessmentEntity("FRV1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C191":
                    assessmentDAO.insertItem(new AssessmentEntity("PAG7", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2018, 10, 31), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("ACO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C188":
                    assessmentDAO.insertItem(new AssessmentEntity("AAO1", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", LocalDate.of(2019, 1, 31), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_8).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C482", "Software I", CourseStatus.PASSED, LocalDate.of(2019, 2, 1),
                        LocalDate.of(2019, 2, 1), LocalDate.of(2019, 8, 10), LocalDate.of(2019, 5, 28),
                        6, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_WANDA_BURWICK).getId()),
                new CourseEntity("C179", "Business of IT - Applications", CourseStatus.PASSED, LocalDate.of(2019, 7, 10),
                        LocalDate.of(2019, 5, 29), LocalDate.of(2019, 8, 10), LocalDate.of(2019, 7, 4),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_WANDA_BURWICK).getId()),
                new CourseEntity("C188", "Software Engineering", CourseStatus.PASSED, LocalDate.of(2019, 7, 10),
                        LocalDate.of(2019, 7, 5), LocalDate.of(2019, 8, 10), LocalDate.of(2019, 7, 31),
                        4, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_WANDA_BURWICK).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C482":
                    assessmentDAO.insertItem(new AssessmentEntity("GYP1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
                case "C179":
                    assessmentDAO.insertItem(new AssessmentEntity("DIP1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
                case "C188":
                    assessmentDAO.insertItem(new AssessmentEntity("PAG8", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2019, 1, 30), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("AAO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_9).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C193", "Client-Server Application Development", CourseStatus.PASSED, LocalDate.of(2019, 10, 1),
                        LocalDate.of(2019, 10, 1), LocalDate.of(2019, 11, 30), LocalDate.of(2019, 10, 10),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_CHARLES_LIVELY).getId()),
                new CourseEntity("C195", "Software II - Advanced Java Concepts", CourseStatus.NOT_PASSED, LocalDate.of(2019, 12, 1),
                        LocalDate.of(2019, 10, 11), LocalDate.of(2019, 2, 28), LocalDate.of(2020, 5, 8),
                        6, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MALCOLM_WABARA).getId()),
                new CourseEntity("C192", "Data Management for Programmers", CourseStatus.NOT_PASSED, LocalDate.of(2019, 3, 1),
                        LocalDate.of(2019, 10, 11), LocalDate.of(2020, 3, 31), LocalDate.of(2020, 5, 8),
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MALCOLM_WABARA).getId())
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C193":
                    assessmentDAO.insertItem(new AssessmentEntity("PAG9", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2019, 10, 2), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("AEO1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
                case "C195":
                    assessmentDAO.insertItem(new AssessmentEntity("GZP1", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
                case "C192":
                    assessmentDAO.insertItem(new AssessmentEntity("PADO", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.PRE_ASSESSMENT, "",
                            LocalDate.of(2019, 10, 2), t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("ADO1", AssessmentStatus.NOT_PASSED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", t.getActualEnd(), t.getId()));
                    break; // O
            }
        });
        id = allTerms.get(TermEntity.SAMPLE_TERM_10).getTerm().getId();
        courseDAO.insertAllItems(
                new CourseEntity("C195", "Software II - Advanced Java Concepts", CourseStatus.PASSED, LocalDate.of(2020, 4, 1),
                        LocalDate.of(2020, 4, 1), LocalDate.of(2020, 6, 30), LocalDate.of(2020, 8, 5),
                        6, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_MARK_KINKEAD).getId()),
                new CourseEntity("C196", "Mobile Application Development", CourseStatus.IN_PROGRESS, LocalDate.of(2020, 8, 21),
                        null, LocalDate.of(2020, 9, 10), null,
                        3, "", id, allMentors.get(MentorEntity.SAMPLE_MENTOR_ALVARO_ESCOBAR).getId()),
                new CourseEntity("C769", "IT Capstone Written Project", CourseStatus.PLANNED, LocalDate.of(2020, 9, 10),
                        null, LocalDate.of(2020, 9, 30), null,
                        4, "", id, null),
                new CourseEntity("C192", "Data Management for Programmers", CourseStatus.UNPLANNED, null, null, null, null,
                        3, "", id, null)
        );
        courseDAO.getItemsByTermId(id).forEach(t -> {
            switch (t.number) {
                case "C195":
                    assessmentDAO.insertItem(new AssessmentEntity("GZP1", AssessmentStatus.PASSED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", t.getActualEnd(), t.getId()));
                    break; // P
                case "C196":
                    assessmentDAO.insertItem(new AssessmentEntity("ABM1\n", AssessmentStatus.IN_PROGRESS, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "", null, t.getId()));
                    break; // P
                case "C769":
                    assessmentDAO.insertItem(new AssessmentEntity("ROM2", AssessmentStatus.NOT_STARTED, LocalDate.of(2020, 9, 13), AssessmentType.PERFORMANCE_EVALUATION, "Task 1", null, t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("ROM2", AssessmentStatus.NOT_STARTED, LocalDate.of(2020, 9, 20), AssessmentType.PERFORMANCE_EVALUATION, "Task 2", null, t.getId()));
                    assessmentDAO.insertItem(new AssessmentEntity("ROM2", AssessmentStatus.NOT_STARTED, t.expectedEnd, AssessmentType.PERFORMANCE_EVALUATION, "Task 3", null, t.getId()));
                    break; // P
                case "C192":
                    assessmentDAO.insertItem(new AssessmentEntity("ADO1", AssessmentStatus.NOT_STARTED, t.expectedEnd, AssessmentType.OBJECTIVE_ASSESSMENT, "", null, t.getId()));
                    break; // O
            }
        });
    }

    public Integer getMentorId() {
        return mentorId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = Values.asNonNullAndWsNormalized(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Values.asNonNullAndWsNormalized(title);
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = (null == status) ? CourseStatus.UNPLANNED : status;
    }

    public LocalDate getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(LocalDate expectedStart) {
        this.expectedStart = expectedStart;
    }

    public LocalDate getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDate actualStart) {
        this.actualStart = actualStart;
    }

    public LocalDate getExpectedEnd() {
        return expectedEnd;
    }

    public void setExpectedEnd(LocalDate expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    public LocalDate getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDate actualEnd) {
        this.actualEnd = actualEnd;
    }

    public void setMentorId(Integer mentorId) {
        this.mentorId = mentorId;
    }

    public int getCompetencyUnits() {
        return competencyUnits;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    @Ignore
    public LiveData<List<AssessmentEntity>> getAssessments(Context context) {
        if (null == assessments) {
            if (id < 1) {
                throw new IllegalStateException();
            }
            assessments = DbLoader.getInstance(context).getAssessmentsByCourseId(id);
        }
        return assessments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEntity that = (CourseEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id &&
                termId == that.termId &&
                mentorId == that.mentorId &&
                number.equals(that.number) &&
                title.equals(that.title) &&
                Objects.equals(expectedStart, that.expectedStart) &&
                Objects.equals(actualStart, that.actualStart) &&
                Objects.equals(expectedEnd, that.expectedEnd) &&
                Objects.equals(actualEnd, that.actualEnd) &&
                status == that.status &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, termId, mentorId, number, title, expectedStart, actualStart, expectedEnd, actualEnd, status, notes);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + id +
                ", termId=" + termId +
                ", mentorId=" + mentorId +
                ", number='" + number + '\'' +
                ", title='" + title + '\'' +
                ", expectedStart=" + expectedStart +
                ", actualStart=" + actualStart +
                ", expectedEnd=" + expectedEnd +
                ", actualEnd=" + actualEnd +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                '}';
    }

    public void setCompetencyUnits(int competencyUnits) {
        this.competencyUnits = competencyUnits;
    }

    class SampleData {
        private CourseEntity course;
        private MentorEntity mentor;
        private AssessmentEntity[] assessments;

        public CourseEntity getCourse() {
            return course;
        }

        public MentorEntity getMentor() {
            return mentor;
        }

        public AssessmentEntity[] getAssessments() {
            return assessments;
        }
    }
}
