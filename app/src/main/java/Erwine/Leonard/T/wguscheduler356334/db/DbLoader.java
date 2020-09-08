package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorEditState;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringLineIterator;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A singleton helper object for database I/O.
 */
public class DbLoader {

    //<editor-fold defaultstate="collapsed" desc="Fields">

    /**
     * 1=title, 2=start, 3=end, 4=notes?
     */
    private static final Pattern PATTERN_SAMPLE_TERM_DATA = Pattern.compile("^\\s*([^,]+),([^,]+),([^,]+),(\\S+(?:\\s+\\S+)*)?\\s*$");

    /**
     * 1=name; 2=notes?; 3=phone_numbers?; 4=email_addresses?
     */
    private static final Pattern PATTERN_SAMPLE_MENTOR_DATA = Pattern.compile("^([^\\t]+)\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?$");

    /**
     * 1=termId; 2=number; 3=title; 4=status; 5=expectedStart?; 6=actualStart?; 7=expectedEnd?; 8=actualEnd?; 9=competencyUnits?; 10=notes?; 11=mentorId?
     */
    private static final Pattern PATTERN_SAMPLE_COURSE_DATA = Pattern.compile("^(\\d+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?" +
            "\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?$");
    /**
     * 1=courseNumber; 2=code; 3=status; (4=yyyy-mm-dd | 5=expectedEnd | 6=actualEnd)=goalDate?; 7=type; 8=notes?; (9=yyyy-mm-dd | 10=expectedEnd | 11=actualEnd)=evaluationDate?
     */
    private static final Pattern PATTERN_SAMPLE_ASSESSMENT_DATA = Pattern.compile("^([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t(?:(\\d{4}-\\d\\d-\\d\\d)|(expectedEnd)|(actualEnd))?" +
            "\\t([^\\t]+)\\t([^\\t]+)?\\t(?:(\\d{4}-\\d\\d-\\d\\d)|(expectedEnd)|(actualEnd))?$");

    private static DbLoader instance;
    private final CompositeDisposable compositeDisposable;
    private final AppDb appDb;
    private final TempDb tempDb;
    private final Scheduler scheduler;
    private final Executor dataExecutor;
    private final CurrentEditedMentor mEditedMentorLiveData;
    private final MentorEditState currentEditedMentor;
    private LiveData<List<TermEntity>> allTerms;
    private LiveData<List<MentorEntity>> allMentors;
    private LiveData<List<CourseEntity>> allCourses;
    private LiveData<List<AssessmentEntity>> allAssessments;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static methods">

    /**
     * Gets the singleton {@code DbLoader} instance.
     *
     * @param context The {@link Context} to use for creating the underling {@link AppDb} and {@link TempDb} instances if they were not yet already created.
     * @return The singleton {@code DbLoader} instance.
     */
    public static DbLoader getInstance(Context context) {
        if (null == instance) {
            instance = new DbLoader(context);
        }

        return instance;
    }

    private static <T> void applyInsertedIds(List<Long> ids, List<T> entities, BiConsumer<T, Long> setId) {
        for (int n = 0; n < ids.size() && n < entities.size(); n++) {
            Long i = ids.get(n);
            if (null != i) {
                setId.accept(entities.get(n), i);
            }
        }
    }

    private static ArrayList<String> parseSampleDataCells(String csvText, Integer expectedCellCount) {
        try {
            ArrayList<ArrayList<String>> rows = StringHelper.parseCsv(csvText.trim());
            if (rows.size() != 1)
                throw new RuntimeException(String.format("Expected 1 parsed CSV row; Actual: %d", rows.size()));
            ArrayList<String> r = rows.get(0);
            if (r.size() != expectedCellCount)
                throw new RuntimeException(String.format("Expected %d parsed CSV cells; Actual: %d", expectedCellCount, r.size()));
            return r;
        } catch (RuntimeException ex) {
            throw new RuntimeException(String.format("Error parsing sample data %s", csvText), ex);
        }
    }

    private static LocalDate sampleCellToLocalDate(String t, CourseEntity u) {
        switch (t) {
            case "expectedEnd":
                return u.getExpectedEnd();
            case "actualEnd":
                return u.getActualEnd();
            case "":
                return null;
            default:
                return LocalDate.parse(t);
        }
    }

    //</editor-fold>

    private DbLoader(Context context) {
        compositeDisposable = new CompositeDisposable();
        mEditedMentorLiveData = new CurrentEditedMentor();
        appDb = AppDb.getInstance(context);
        tempDb = TempDb.getInstance(context);
        currentEditedMentor = new MentorEditState(mEditedMentorLiveData, tempDb.phoneNumberDAO().getAll(), tempDb.emailAddressDAO().getAll());
        dataExecutor = Executors.newSingleThreadExecutor();
        scheduler = Schedulers.from(dataExecutor);
    }

    //<editor-fold defaultstate="collapsed" desc="TermEntity methods">

    /**
     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link TermEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
     */
    public Single<TermEntity> getTermByRowId(int rowId) {
        return appDb.termDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link TermEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
     */
    public Single<TermEntity> getTermById(int id) {
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link TermEntity} objects retrieved from the underlying {@link AppDb}.
     */
    public LiveData<List<TermEntity>> getAllTerms() {
        if (null == allTerms) {
            allTerms = appDb.termDAO().getAll();
        }
        return allTerms;
    }

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     */
    public Single<Integer> getTermCount() {
        return appDb.termDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously save the specified {@link TermEntity} object into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     * If {@link TermEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link TermEntity} has been successfully inserted, the value returned by {@link TermEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link TermEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveTerm(TermEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.termDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> TermEntity.applyInsertedId(entity, id)));
        }
        return appDb.termDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link TermEntity} objects into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable insertAllTerms(List<TermEntity> list) {
        return Completable.fromSingle(appDb.termDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, TermEntity::applyInsertedId)));
    }

    /**
     * Asynchronously deletes a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link TermEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deleteTerm(TermEntity entity) {
        return appDb.termDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CourseEntity methods">

    /**
     * Asynchronously gets a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link MentorEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link MentorEntity} object.
     */
    public Single<MentorEntity> getMentorByRowId(int rowId) {
        return appDb.mentorDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link MentorEntity} objects retrieved from the underlying {@link AppDb}.
     */
    public LiveData<List<MentorEntity>> getAllMentors() {
        if (null == allMentors) {
            allMentors = appDb.mentorDAO().getAll();
        }
        return allMentors;
    }

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     */
    public Single<Integer> getMentorCount() {
        return appDb.mentorDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets the {@link MentorEntity} currently being edited by the {@link Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity}.
     *
     * @return The {@link MentorEntity} currently being edited by the {@link Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity}.
     */
    public MentorEditState getEditedMentor() {
        return currentEditedMentor;
    }

    /**
     * Ensures that {@link #getEditedMentor()} will return the {@link MentorEntity} object associated with the specified unique identifier. This is to signify that the
     * associated {@link MentorEntity} is being edited by the {@link Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity}. After the {@link MentorEntity}
     * is changed the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} and {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data tables within the
     * {@link TempDb} will be updated with the lines from {@link MentorEntity#getPhoneNumbers()} and {@link MentorEntity#getEmailAddresses()}.
     *
     * @param mentorId The unique identifier of the {@link MentorEntity} object that is being edited by the {@link Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity}.
     * @return The {@link Single} object that can be used to observe the {@link MentorEntity} object loaded from {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within
     * the underlying {@link AppDb}.
     */
    public Single<MentorEntity> ensureEditedMentorId(long mentorId) {
        return mEditedMentorLiveData.ensureEditedMentorId(mentorId);
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     *
     * @param clearCurrent      If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getNameAndNotes   An {@link Supplier} that returns the name and notes to be applied to the {@link MentorEntity} before saving. This parameter can be {@code null} if
     *                          the name and notes for the {@link MentorEntity} already contain the intended values.
     * @param getPhoneNumbers   An {@link Supplier} that returns the string containing the phone numbers to be applied to the {@link MentorEntity} before saving. This can be
     *                          {@code null} if the {@link MentorEntity} already contains the intended phone numbers.
     * @param getEmailAddresses An {@link Supplier} that returns the string containing the email addresses to be applied to the {@link MentorEntity} before saving. This can be
     *                          {@code null} if the {@link MentorEntity} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes, Supplier<String> getPhoneNumbers, Supplier<String> getEmailAddresses) {
        return mEditedMentorLiveData.saveEditedMentor(
                clearCurrent,
                getNameAndNotes,
                getPhoneNumbers,
                getEmailAddresses
        );
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     *
     * @param clearCurrent                If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getNameAndNotes             An {@link Supplier} that returns the name and notes to be applied to the {@link MentorEntity} before saving. This parameter can be {@code null} if
     *                                    the name and notes for the {@link MentorEntity} already contain the intended values.
     * @param getPhoneNumbers             An {@link Supplier} that returns the string containing the phone numbers to be applied to the {@link MentorEntity} before saving. This can be
     *                                    {@code null} if the {@link MentorEntity} already contains the intended phone numbers.
     * @param getEmailAddressesFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setEmailAddresses(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getEmailAddresses()} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes, Supplier<String> getPhoneNumbers, boolean getEmailAddressesFromTempDb) {
        return mEditedMentorLiveData.saveEditedMentor(
                clearCurrent,
                getNameAndNotes,
                getPhoneNumbers,
                (getEmailAddressesFromTempDb) ? () -> tempDb.emailAddressDAO().getAllSynchronous().stream().sorted().map(EmailAddressEntity::getValue).filter(t -> !t.isEmpty())
                        .collect(Collectors.joining("\n")) : null
        );
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     *
     * @param clearCurrent              If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getNameAndNotes           An {@link Supplier} that returns the name and notes to be applied to the {@link MentorEntity} before saving. This parameter can be {@code null} if
     *                                  the name and notes for the {@link MentorEntity} already contain the intended values.
     * @param getPhoneNumbersFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table rows within the
     *                                  {@link TempDb} will be applied to {@link MentorEntity#setPhoneNumbers(String)}; otherwise, it wil be assumed that
     *                                  {@link MentorEntity#getPhoneNumbers()} already contains the intended phone numbers.
     * @param getEmailAddresses         An {@link Supplier} that returns the string containing the email addresses to be applied to the {@link MentorEntity} before saving. This can be
     *                                  {@code null} if the {@link MentorEntity} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes, boolean getPhoneNumbersFromTempDb, Supplier<String> getEmailAddresses) {
        return saveEditedMentor(
                clearCurrent,
                getNameAndNotes,
                (getPhoneNumbersFromTempDb) ? () -> tempDb.phoneNumberDAO().getAllSynchronous().stream().sorted().map(PhoneNumberEntity::getValue).filter(t -> !t.isEmpty())
                        .collect(Collectors.joining("\n")) : null,
                getEmailAddresses
        );
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     *
     * @param clearCurrent                If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getNameAndNotes             An {@link Supplier} that returns the name and notes to be applied to the {@link MentorEntity} before saving. This parameter can be {@code null} if
     *                                    the name and notes for the {@link MentorEntity} already contain the intended values.
     * @param getPhoneNumbersFromTempDb   If {@code true}, then the values from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setPhoneNumbers(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getPhoneNumbers()} already contains the intended phone numbers.
     * @param getEmailAddressesFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setEmailAddresses(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getEmailAddresses()} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes, boolean getPhoneNumbersFromTempDb, boolean getEmailAddressesFromTempDb) {
        return saveEditedMentor(
                clearCurrent,
                getNameAndNotes,
                (getPhoneNumbersFromTempDb) ? () -> tempDb.phoneNumberDAO().getAllSynchronous().stream().sorted().map(PhoneNumberEntity::getValue).filter(t -> !t.isEmpty())
                        .collect(Collectors.joining("\n")) : null,
                getEmailAddressesFromTempDb
        );
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     * This method assumes that the name and notes for the {@link MentorEntity} already contain the intended values.
     *
     * @param clearCurrent      If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getPhoneNumbers   An {@link Supplier} that returns the string containing the phone numbers to be applied to the {@link MentorEntity} before saving. This can be
     *                          {@code null} if the {@link MentorEntity} already contains the intended phone numbers.
     * @param getEmailAddresses An {@link Supplier} that returns the string containing the email addresses to be applied to the {@link MentorEntity} before saving. This can be
     *                          {@code null} if the {@link MentorEntity} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<String> getPhoneNumbers, Supplier<String> getEmailAddresses) {
        return saveEditedMentor(clearCurrent, null, getPhoneNumbers, getEmailAddresses);
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     * This method assumes that the name and notes for the {@link MentorEntity} already contain the intended values.
     *
     * @param clearCurrent                If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getPhoneNumbers             An {@link Supplier} that returns the string containing the phone numbers to be applied to the {@link MentorEntity} before saving. This can be
     *                                    {@code null} if the {@link MentorEntity} already contains the intended phone numbers.
     * @param getEmailAddressesFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setEmailAddresses(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getEmailAddresses()} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, Supplier<String> getPhoneNumbers, boolean getEmailAddressesFromTempDb) {
        return saveEditedMentor(clearCurrent, null, getPhoneNumbers, getEmailAddressesFromTempDb);
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     * This method assumes that the name and notes for the {@link MentorEntity} already contain the intended values.
     *
     * @param clearCurrent              If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getPhoneNumbersFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table rows within the
     *                                  {@link TempDb} will be applied to {@link MentorEntity#setPhoneNumbers(String)}; otherwise, it wil be assumed that
     *                                  {@link MentorEntity#getPhoneNumbers()} already contains the intended phone numbers.
     * @param getEmailAddresses         An {@link Supplier} that returns the string containing the email addresses to be applied to the {@link MentorEntity} before saving. This can be
     *                                  {@code null} if the {@link MentorEntity} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, boolean getPhoneNumbersFromTempDb, Supplier<String> getEmailAddresses) {
        return saveEditedMentor(clearCurrent, null, getPhoneNumbersFromTempDb, getEmailAddresses);
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     * This method assumes that the name and notes for the {@link MentorEntity} already contain the intended values.
     *
     * @param clearCurrent                If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @param getPhoneNumbersFromTempDb   If {@code true}, then the values from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setPhoneNumbers(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getPhoneNumbers()} already contains the intended phone numbers.
     * @param getEmailAddressesFromTempDb If {@code true}, then the values from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table rows within the
     *                                    {@link TempDb} will be applied to {@link MentorEntity#setEmailAddresses(String)}; otherwise, it wil be assumed that
     *                                    {@link MentorEntity#getEmailAddresses()} already contains the intended email addresses.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent, boolean getPhoneNumbersFromTempDb, boolean getEmailAddressesFromTempDb) {
        return saveEditedMentor(clearCurrent, null, getPhoneNumbersFromTempDb, getEmailAddressesFromTempDb);
    }

    /**
     * Asynchronously saves the {@link MentorEntity} returned by {@link #getEditedMentor()} into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying
     * {@link AppDb}.
     * This method assumes that the {@link MentorEntity} already contain the intended values.
     *
     * @param clearCurrent If {@code true}, then the value stored by {@link #getEditedMentor()} will be set to {@code null} after the operation is successful.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEditedMentor(boolean clearCurrent) {
        return saveEditedMentor(clearCurrent, false, false);
    }

    /**
     * Asynchronously saves the specified {@link MentorEntity} object into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     * If {@link MentorEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link MentorEntity} has been successfully inserted, the value returned by {@link MentorEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link MentorEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveMentor(MentorEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.mentorDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> MentorEntity.applyInsertedId(entity, id)));
        }
        return appDb.mentorDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link MentorEntity} objects into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable insertAllMentors(List<MentorEntity> list) {
        return Completable.fromSingle(appDb.mentorDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, MentorEntity::applyInsertedId)));
    }

    /**
     * Asynchronously deletes a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link MentorEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deleteMentor(MentorEntity entity) {
        return appDb.mentorDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link MentorEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link MentorEntity} object.
     */
    public Single<MentorEntity> getMentorById(long id) {
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PhoneNumberEntity methods">

    /**
     * Asynchronously deletes a {@link PhoneNumberEntity} from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table of the underlying {@link TempDb}.
     *
     * @param entity The {@link PhoneNumberEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deletePhoneNumber(PhoneNumberEntity entity) {
        return tempDb.phoneNumberDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table within the underlying {@link TempDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link PhoneNumberEntity} objects retrieved from the underlying {@link TempDb}.
     */
    public LiveData<List<PhoneNumberEntity>> getPhoneNumbers() {
        return tempDb.phoneNumberDAO().getAll();
    }

    /**
     * Asynchronously saves the specified {@link PhoneNumberEntity} object into the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table of the underlying {@link TempDb}.
     * If {@link PhoneNumberEntity#getId()} is null, then it will be inserted into the {@link TempDb#TABLE_NAME_PHONE_NUMBERS "phone_numbers"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link PhoneNumberEntity} has been successfully inserted, the value returned by {@link PhoneNumberEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link PhoneNumberEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable savePhoneNumber(PhoneNumberEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(tempDb.phoneNumberDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> PhoneNumberEntity.applyInsertedId(entity, id)));
        }
        return tempDb.phoneNumberDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EmailAddressEntity methods">

    /**
     * Asynchronously deletes a {@link EmailAddressEntity} from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table of the underlying {@link TempDb}.
     *
     * @param entity The {@link EmailAddressEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deleteEmailAddress(EmailAddressEntity entity) {
        return tempDb.emailAddressDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table within the underlying {@link TempDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link EmailAddressEntity} objects retrieved from the underlying {@link TempDb}.
     */
    public LiveData<List<EmailAddressEntity>> getEmailAddresses() {
        return tempDb.emailAddressDAO().getAll();
    }

    /**
     * Asynchronously saves the specified {@link EmailAddressEntity} object into the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table of the underlying {@link TempDb}.
     * If {@link EmailAddressEntity#getId()} is null, then it will be inserted into the {@link TempDb#TABLE_NAME_EMAIL_ADDRESSES "email_addresses"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link EmailAddressEntity} has been successfully inserted, the value returned by {@link EmailAddressEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link EmailAddressEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveEmailAddress(EmailAddressEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(tempDb.emailAddressDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> EmailAddressEntity.applyInsertedId(entity, id)));
        }
        return tempDb.emailAddressDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CourseEntity methods">

    /**
     * Asynchronously deletes a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link CourseEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deleteCourse(CourseEntity entity) {
        return appDb.courseDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link CourseEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseEntity} object.
     */
    public Single<CourseEntity> getCourseByRowId(int rowId) {
        return appDb.courseDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link CourseEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseEntity} object.
     */
    public Single<CourseEntity> getCourseById(int id) {
        return appDb.courseDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb}.
     */
    public LiveData<List<CourseEntity>> getAllCourses() {
        if (null == allCourses) {
            allCourses = appDb.courseDAO().getAll();
        }
        return allCourses;
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
     *
     * @param termId The unique identifier of a {@link TermEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link TermEntity}.
     */
    public LiveData<List<CourseEntity>> getCoursesByTermId(int termId) {
        return appDb.courseDAO().getByTermId(termId);
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table.
     *
     * @param mentorId The unique identifier of a {@link MentorEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link MentorEntity}.
     */
    public LiveData<List<CourseEntity>> getCoursesByMentorId(int mentorId) {
        return appDb.courseDAO().getByMentorId(mentorId);
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that have not been completed and are expected to start
     * on or before a specified date.
     *
     * @param date The {@link LocalDate} value representing the inclusive end range of the expected start date.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that have not been completed and
     * are expected to start on or before a specified date.
     */
    public LiveData<List<CourseEntity>> getUnterminatedCoursesOnOrBefore(LocalDate date) {
        return appDb.courseDAO().getUnterminatedOnOrBefore(date);
    }

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     */
    public Single<Integer> getCourseCount() {
        return appDb.courseDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously save the specified {@link CourseEntity} object into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     * If {@link CourseEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link CourseEntity} has been successfully inserted, the value returned by {@link CourseEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link CourseEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveCourse(CourseEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.courseDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> CourseEntity.applyInsertedId(entity, id)));
        }
        return appDb.courseDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link CourseEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link CourseEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable insertAllCourses(List<CourseEntity> list) {
        return Completable.fromSingle(appDb.courseDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, CourseEntity::applyInsertedId)));
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="AssessmentEntity methods">

    /**
     * Asynchronously deletes a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link AssessmentEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable deleteAssessment(AssessmentEntity entity) {
        return appDb.assessmentDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    public Single<AssessmentEntity> getAssessmentByRowId(int rowId) {
        return appDb.assessmentDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    public Single<AssessmentEntity> getAssessmentById(int id) {
        return appDb.assessmentDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table.
     *
     * @param courseId The unique identifier of a {@link CourseEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link AssessmentEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link CourseEntity}.
     */
    public LiveData<List<AssessmentEntity>> getAssessmentsByCourseId(int courseId) {
        return appDb.assessmentDAO().getByCourseId(courseId);
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link AssessmentEntity} objects retrieved from the underlying {@link AppDb}.
     */
    public LiveData<List<AssessmentEntity>> getAllAssessments() {
        if (null == allAssessments) {
            allAssessments = appDb.assessmentDAO().getAll();
        }
        return allAssessments;
    }

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     */
    public Single<Integer> getAssessmentCount() {
        return appDb.assessmentDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously save the specified {@link AssessmentEntity} object into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table of the underlying {@link AppDb}.
     * If {@link AssessmentEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link AssessmentEntity} has been successfully inserted, the value returned by {@link AssessmentEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link AssessmentEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable saveAssessment(AssessmentEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.assessmentDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> AssessmentEntity.applyInsertedId(entity, id)));
        }
        return appDb.assessmentDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link AssessmentEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link AssessmentEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    public Completable insertAllAssessments(List<AssessmentEntity> list) {
        return Completable.fromSingle(appDb.assessmentDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AssessmentEntity::applyInsertedId)));
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Bulk DB Operations">

    private void resetDb() {
        appDb.clearAllTables();
        for (String t : new String[]{
                AppDb.TABLE_NAME_ASSESSMENTS,
                AppDb.TABLE_NAME_COURSES,
                AppDb.TABLE_NAME_TERMS,
                AppDb.TABLE_NAME_MENTORS
        }) {
            appDb.query(String.format("UPDATE sqlite_sequence SET seq = 1 WHERE name = '%s'", t), null).close();
        }
    }

    public Completable resetDatabase() {
        return Completable.fromAction(this::resetDb).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private HashMap<Integer, Long> createSampleTerms(Resources resources) {
        List<TermEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_terms)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            entities.add(new TermEntity(cells.get(0), LocalDate.parse(cells.get(1)), LocalDate.parse(cells.get(2)), cells.get(3)));
        }
        List<Long> ids = appDb.termDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            TermEntity.applyInsertedId(entities.get(index), id);
            result.put(++index, id);
        }
        return result;
    }

    @NonNull
    private HashMap<Integer, Long> createSampleMentors(Resources resources) {
        List<MentorEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_mentors)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            entities.add(new MentorEntity(cells.get(0), cells.get(1), cells.get(2), cells.get(3)));
        }
        List<Long> ids = appDb.mentorDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            MentorEntity.applyInsertedId(entities.get(index), id);
            result.put(++index, id);
        }
        return result;
    }

    @NonNull
    private HashMap<Integer, CourseEntity> createSampleCourses(Resources resources, @NonNull HashMap<Integer, Long> sampleTerms, @NonNull HashMap<Integer, Long> sampleMentors) {
        List<CourseEntity> entities = new ArrayList<>();
        HashMap<String, CourseStatus> statusMap = new HashMap<>();
        for (CourseStatus cs : CourseStatus.values()) {
            statusMap.put(cs.name(), cs);
        }
        Function<String, LocalDate> parseDateCell = t -> (t.isEmpty()) ? null : LocalDate.parse(t);
        for (String csv : resources.getStringArray(R.array.sample_courses)) {
            List<String> cells = parseSampleDataCells(csv, 11);
            String c = cells.get(8);
            String m = cells.get(10);
            entities.add(new CourseEntity(cells.get(1), cells.get(2), statusMap.get(cells.get(3)), parseDateCell.apply(cells.get(4)), parseDateCell.apply(cells.get(5)),
                    parseDateCell.apply(cells.get(6)), parseDateCell.apply(cells.get(7)), (c.isEmpty()) ? null : Integer.parseInt(c), cells.get(9),
                    Objects.requireNonNull(sampleTerms.get(Integer.parseInt(cells.get(0)))), (m.isEmpty()) ? null : sampleMentors.get(Integer.parseInt(m))));
        }
        List<Long> ids = appDb.courseDAO().insertAllSynchronous(entities);
        HashMap<Integer, CourseEntity> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            CourseEntity e = entities.get(index);
            CourseEntity.applyInsertedId(e, ids.get(index));
            result.put(++index, e);
        }
        return result;
    }

    public Completable populateSampleData(Resources resources) {
        return Completable.fromAction(() -> {
            resetDb();
            HashMap<Integer, Long> sampleTerms = createSampleTerms(resources);
            HashMap<Integer, Long> sampleMentors = createSampleMentors(resources);
            HashMap<Integer, CourseEntity> sampleCourses = createSampleCourses(resources, sampleTerms, sampleMentors);

            AssessmentDAO assessmentDAO = appDb.assessmentDAO();
            HashMap<String, AssessmentStatus> am = new HashMap<>();
            for (AssessmentStatus a : AssessmentStatus.values()) {
                am.put(a.name(), a);
            }
            HashMap<String, AssessmentType> at = new HashMap<>();
            for (AssessmentType a : AssessmentType.values()) {
                at.put(a.name(), a);
            }
            assessmentDAO.insertAllSynchronous(Arrays.stream(resources.getStringArray(R.array.sample_assessments)).map(t -> {
                // 0=courseId, 1=code, 2=status, 3=goalDate, 4=type, 5=notes, 6=evaluationDate
                List<String> cells = parseSampleDataCells(t, 7);
                CourseEntity course = Objects.requireNonNull(sampleCourses.get(Integer.parseInt(cells.get(0))));
                return new AssessmentEntity(cells.get(1), am.get(cells.get(2)), sampleCellToLocalDate(cells.get(3), course), at.get(cells.get(4)), cells.get(5),
                        sampleCellToLocalDate(cells.get(6), course), course.getId());
            }).collect(Collectors.toList()));
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    private class CurrentEditedMentor extends LiveData<MentorEntity> {
        private MentorEntity mPostedValue = null;
        private Object changeKey;

        protected synchronized void onPostedValueChanged(Object key) {
            if (key != changeKey) {
                return;
            }
            changeKey = new Object();
            MentorEntity value = mPostedValue;
            if (null == value) {
                tempDb.emailAddressDAO().deleteAllSynchronous();
                tempDb.phoneNumberDAO().deleteAllSynchronous();
                return;
            }
            tempDb.emailAddressDAO().deleteAllSynchronous();
            tempDb.phoneNumberDAO().deleteAllSynchronous();
            String phoneNumbers = value.getPhoneNumbers();
            if (!phoneNumbers.trim().isEmpty()) {
                List<PhoneNumberEntity> list = StringLineIterator.getLines(phoneNumbers).filter(t -> !t.isEmpty()).map(new Function<String, PhoneNumberEntity>() {
                    int order = -1;

                    @Override
                    public PhoneNumberEntity apply(String t) {
                        return new PhoneNumberEntity(t, ++order);
                    }
                }).collect(Collectors.toList());
                if (!list.isEmpty()) {
                    applyInsertedIds(tempDb.phoneNumberDAO().insertAllSynchronous(list), list, PhoneNumberEntity::applyInsertedId);
                }
            }
            String emailAddresses = value.getEmailAddresses();
            if (emailAddresses.isEmpty()) {
                return;
            }
            List<EmailAddressEntity> emailAddressEntities = StringLineIterator.getLines(emailAddresses).filter(t -> !t.isEmpty()).map(new Function<String, EmailAddressEntity>() {
                int order = -1;

                @Override
                public EmailAddressEntity apply(String t) {
                    return new EmailAddressEntity(t, ++order);
                }
            }).collect(Collectors.toList());
            if (!emailAddressEntities.isEmpty()) {
                applyInsertedIds(tempDb.emailAddressDAO().insertAllSynchronous(emailAddressEntities), emailAddressEntities, EmailAddressEntity::applyInsertedId);
            }
        }

        @Override
        protected synchronized void postValue(MentorEntity value) {
            if ((null == value) ? null != mPostedValue : null == mPostedValue || mPostedValue != value) {
                Object key = new Object();
                changeKey = key;
                mPostedValue = value;
                dataExecutor.execute(() -> onPostedValueChanged(key));
            }
            super.postValue(value);
        }

        private synchronized Single<MentorEntity> ensureEditedMentorId(long mentorId) {
            MentorEntity entity = mPostedValue;
            if (null != entity) {
                Long id = entity.getId();
                if (null != id && id == mentorId) {
                    return Single.fromCallable(() -> entity).observeOn(AndroidSchedulers.mainThread());
                }
                postValue(null);
            }

            return appDb.mentorDAO().getById(mentorId).subscribeOn(scheduler).doOnSuccess(this::postValue).observeOn(AndroidSchedulers.mainThread());
        }

        private synchronized Single<MentorEntity> ensureNewEditedMentor() {
            MentorEntity entity = mPostedValue;
            if (null != entity) {
                if (null == entity.getId()) {
                    return Single.fromCallable(() -> entity).observeOn(AndroidSchedulers.mainThread());
                }
            }
            MentorEntity newEntity = new MentorEntity();
            postValue(newEntity);
            return Single.fromCallable(() -> newEntity).observeOn(AndroidSchedulers.mainThread());
        }

        private synchronized Completable saveEditedMentor(boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes, Supplier<String> getPhoneNumbers,
                                                          Supplier<String> getEmailAddresses) {
            MentorEntity entity = mPostedValue;
            if (null == entity) {
                throw new IllegalStateException();
            }
            Object key = new Object();
            changeKey = key;
            return Completable.fromAction(() -> onSaveEditedMentor(entity, clearCurrent, getNameAndNotes, getPhoneNumbers, getEmailAddresses, key))
                    .subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }

        private synchronized void onSaveEditedMentor(MentorEntity entity, boolean clearCurrent, Supplier<Pair<String, String>> getNameAndNotes,
                                                     Supplier<String> getPhoneNumbers, Supplier<String> getEmailAddresses, Object key) {
            if (key != changeKey) {
                return;
            }
            if (null != getNameAndNotes) {
                Pair<String, String> nameAndNotes = getNameAndNotes.get();
                entity.setName(nameAndNotes.first);
                entity.setNotes(nameAndNotes.second);
            }
            if (null != getPhoneNumbers) {
                entity.setPhoneNumbers(getPhoneNumbers.get());
            }
            if (null != getEmailAddresses) {
                entity.setEmailAddresses(getEmailAddresses.get());
            }
            if (null == entity.getId()) {
                long id = appDb.mentorDAO().insertSynchronous(entity);
                MentorEntity.applyInsertedId(entity, id);
            } else {
                appDb.mentorDAO().updateSynchronous(entity);
            }
            if (clearCurrent) {
                postValue(null);
            }
        }

    }

}
