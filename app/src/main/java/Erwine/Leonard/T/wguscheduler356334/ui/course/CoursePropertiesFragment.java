package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

public class CoursePropertiesFragment extends Fragment {

    private static final String LOG_TAG = CoursePropertiesFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;
    private Button termButton;
    private EditText courseCodeEditText;
    private EditText competencyUnitsEditText;
    private EditText titleEditText;
    private Chip mentorChip;
    private Button statusButton;
    private Chip expectedStartChip;
    private Chip expectedEndChip;
    private Chip actualStartChip;
    private Chip actualEndChip;
    private EditText notesEditText;

    public CoursePropertiesFragment() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        termButton = view.findViewById(R.id.termButton);
        courseCodeEditText = view.findViewById(R.id.courseCodeEditText);
        competencyUnitsEditText = view.findViewById(R.id.competencyUnitsEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        mentorChip = view.findViewById(R.id.mentorChip);
        statusButton = view.findViewById(R.id.typeButton);
        expectedStartChip = view.findViewById(R.id.expectedStartChip);
        expectedEndChip = view.findViewById(R.id.expectedEndChip);
        actualStartChip = view.findViewById(R.id.actualStartChip);
        actualEndChip = view.findViewById(R.id.actualEndChip);
        notesEditText = view.findViewById(R.id.notesEditText);
        termButton.setOnClickListener(this::onTermButtonClick);
        mentorChip.setOnClickListener(this::onMentorChipClick);
        mentorChip.setOnCloseIconClickListener(this::onMentorChipCloseIconClick);
        statusButton.setOnClickListener(this::onStatusButtonClick);
        expectedStartChip.setOnClickListener(this::onExpectedStartChipClick);
        expectedStartChip.setOnCloseIconClickListener(this::onExpectedStartChipCloseIconClick);
        expectedEndChip.setOnClickListener(this::onExpectedEndChipClick);
        expectedEndChip.setOnCloseIconClickListener(this::onExpectedEndChipCloseIconClick);
        actualStartChip.setOnClickListener(this::onActualStartChipClick);
        actualStartChip.setOnCloseIconClickListener(this::onActualStartChipCloseIconClick);
        actualEndChip.setOnClickListener(this::onActualEndChipClick);
        actualEndChip.setOnCloseIconClickListener(this::onActualEndChipCloseIconClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getEntityLiveData().observe(viewLifecycleOwner, this::onEntityLoaded);
        viewModel.getTermsLiveData().observe(viewLifecycleOwner, this::onTermsLoaded);
        viewModel.getMentorsLiveData().observe(viewLifecycleOwner, this::onMentorsLoaded);
        viewModel.getTermValidLiveData().observe(viewLifecycleOwner, this::onTermValidChanged);
        viewModel.getNumberValidLiveData().observe(viewLifecycleOwner, this::onNumberValidChanged);
        viewModel.getTitleValidLiveData().observe(viewLifecycleOwner, this::onTitleValidChanged);
        viewModel.getExpectedStartErrorMessageLiveData().observe(viewLifecycleOwner, this::onExpectedStartErrorMessageChanged);
        viewModel.getExpectedStartWarningMessageLiveData().observe(viewLifecycleOwner, this::onExpectedStartWarningMessageChanged);
        viewModel.getExpectedEndMessageLiveData().observe(viewLifecycleOwner, this::onExpectedEndMessageChanged);
        viewModel.getActualStartErrorMessageLiveData().observe(viewLifecycleOwner, this::onActualStartErrorMessageChanged);
        viewModel.getActualStartWarningMessageLiveData().observe(viewLifecycleOwner, this::onActualStartWarningMessageChanged);
        viewModel.getActualEndMessageLiveData().observe(viewLifecycleOwner, this::onActualEndMessageChanged);
        viewModel.getCompetencyUnitsMessageLiveData().observe(viewLifecycleOwner, this::onCompetencyUnitsMessageChanged);
    }

    private void onTermValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onTermValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            termButton.setError(null);
        } else {
            termButton.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onNumberValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onNumberValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            courseCodeEditText.setError(null);
        } else {
            courseCodeEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onTitleValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onTitleValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            titleEditText.setError(null);
        } else {
            titleEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onExpectedStartErrorMessageChanged(@StringRes Integer id) {
        if (null != id) {
            Log.d(LOG_TAG, String.format("Enter onExpectedStartErrorMessageChanged(%d)", id));
            expectedStartChip.setError(getResources().getString(id));
        } else if (null == (id = viewModel.getExpectedStartWarningMessageLiveData().getValue())) {
            Log.d(LOG_TAG, "Enter onExpectedStartErrorMessageChanged(null); warning=null");
            expectedStartChip.setError(null);
        } else {
            Log.d(LOG_TAG, String.format("Enter onExpectedStartErrorMessageChanged(null); warning=%d", id));
            expectedStartChip.setError(getResources().getString(id), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
        }
    }

    private void onExpectedStartWarningMessageChanged(Integer id) {
        if (null == viewModel.getExpectedStartErrorMessageLiveData().getValue()) {
            if (null == id) {
                Log.d(LOG_TAG, "Enter onExpectedStartWarningMessageChanged(null)");
                expectedStartChip.setError(null);
            } else {
                Log.d(LOG_TAG, String.format("Enter onExpectedStartWarningMessageChanged(%d)", id));
                expectedStartChip.setError(getResources().getString(id), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
            }
        } else {
            Log.d(LOG_TAG, (null == id) ? "Enter onExpectedStartWarningMessageChanged(null); error != null" : String.format("Enter onExpectedStartWarningMessageChanged(%d); error != null", id));
        }
    }

    private void onExpectedEndMessageChanged(@StringRes Integer id) {
        if (null == id) {
            Log.d(LOG_TAG, "Enter onExpectedEndMessageChanged(null)");
            expectedEndChip.setError(null);
        } else {
            Log.d(LOG_TAG, String.format("Enter onExpectedEndMessageChanged(%d)", id));
            String message = getResources().getString(id);
            if (id == R.string.message_required) {
                expectedEndChip.setError(message);
            } else {
                expectedEndChip.setError(message, AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
            }
        }
    }

    private void onActualStartErrorMessageChanged(@StringRes Integer id) {
        if (null != id) {
            Log.d(LOG_TAG, String.format("Enter onActualStartErrorMessageChanged(%d)", id));
            actualStartChip.setError(getResources().getString(id));
        } else if (null == (id = viewModel.getActualStartWarningMessageLiveData().getValue())) {
            Log.d(LOG_TAG, "Enter onActualStartErrorMessageChanged(null); warning=null");
            actualStartChip.setError(null);
        } else {
            Log.d(LOG_TAG, String.format("Enter onActualStartErrorMessageChanged(null); warning=%d", id));
            actualStartChip.setError(getResources().getString(id), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
        }
    }

    private void onActualStartWarningMessageChanged(Integer id) {
        if (null == viewModel.getActualStartErrorMessageLiveData().getValue()) {
            if (null == id) {
                Log.d(LOG_TAG, "Enter onActualStartWarningMessageChanged(null)");
                actualStartChip.setError(null);
            } else {
                Log.d(LOG_TAG, String.format("Enter onActualStartWarningMessageChanged(%d)", id));
                actualStartChip.setError(getResources().getString(id), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
            }
        } else {
            Log.d(LOG_TAG, (null == id) ? "Enter onActualStartWarningMessageChanged(null); error != null" : String.format("Enter onActualStartWarningMessageChanged(%d); error != null", id));
        }
    }

    private void onActualEndMessageChanged(@StringRes Integer id) {
        if (null == id) {
            Log.d(LOG_TAG, "Enter onActualEndMessageChanged(null)");
            actualEndChip.setError(null);
        } else {
            Log.d(LOG_TAG, String.format("Enter onActualEndMessageChanged(%d)", id));
            String message = getResources().getString(id);
            if (id == R.string.message_required) {
                actualEndChip.setError(message);
            } else {
                actualEndChip.setError(message, AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_warning));
            }
        }
    }

    private void onCompetencyUnitsMessageChanged(@StringRes Integer id) {
        if (null == id) {
            Log.d(LOG_TAG, "Enter onCompetencyUnitsMessageChanged(null)");
            competencyUnitsEditText.setError(null);
        } else {
            Log.d(LOG_TAG, String.format("Enter onCompetencyUnitsMessageChanged(%d)", id));
            competencyUnitsEditText.setError(getResources().getString(id));
        }
    }

    private void onEntityLoaded(CourseDetails entity) {
        if (null == entity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Enter onEntityLoaded(%s)", entity));
        viewModel.initializeTermProperty(viewModel.getTermsLiveData().getValue());
        onTermChanged(viewModel.getSelectedTerm());
        viewModel.initializeMentorProperty(viewModel.getMentorsLiveData().getValue());
        onMentorChanged(viewModel.getSelectedMentor());
        courseCodeEditText.setText(viewModel.getNumber());
        competencyUnitsEditText.setText(viewModel.getCompetencyUnitsText());
        titleEditText.setText(viewModel.getTitle());
        onStatusChanged();
        courseCodeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNumber));
        competencyUnitsEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCompetencyUnitsText));
        titleEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setTitle));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        onExpectedStartChanged();
        onActualStartChanged();
        onExpectedEndChanged();
        onActualEndChanged();
        notesEditText.setText(viewModel.getNotes());
    }

    private void onTermsLoaded(List<TermListItem> termListItems) {
        Log.d(LOG_TAG, String.format("Loaded %d terms", termListItems.size()));
        AbstractTermEntity<?> term = viewModel.initializeTermProperty(termListItems);
        if (null != term) {
            onTermChanged(term);
        }
    }

    private void onMentorsLoaded(List<MentorListItem> mentorListItems) {
        Log.d(LOG_TAG, String.format("Loaded %d mentors", mentorListItems.size()));
        AbstractMentorEntity<?> mentor = viewModel.initializeMentorProperty(mentorListItems);
        if (null != mentor) {
            onMentorChanged(mentor);
        }
    }

    private void onTermButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onTermButtonClick");
        List<TermListItem> termListItems = viewModel.getTermsLiveData().getValue();
        if (null != termListItems) {
            AlertHelper.showSingleSelectDialog(R.string.title_select_term, viewModel.getSelectedTerm(), termListItems, requireContext(), AbstractTermEntity::getName, t -> {
                viewModel.setSelectedTerm(t);
                onTermChanged(t);
            });
        } else {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_not_ready, R.string.message_db_read_operation_in_progress, requireContext()).showDialog();
        }
    }

    private void onMentorChipClick(View view) {
        Log.d(LOG_TAG, "Enter onMentorChipClick");
        List<MentorListItem> mentorListItems = viewModel.getMentorsLiveData().getValue();
        if (null != mentorListItems) {
            AlertHelper.showSingleSelectDialog(R.string.title_select_mentor, viewModel.getSelectedMentor(), mentorListItems, requireContext(), AbstractMentorEntity::getName, t -> {
                viewModel.setSelectedMentor(t);
                onMentorChanged(t);
            });
        }
    }

    private void onMentorChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter onMentorChipCloseIconClick");
        viewModel.setSelectedMentor(null);
        onMentorChanged(null);
    }

    private void onStatusButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onStatusButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_status, viewModel.getStatus(), Arrays.asList(CourseStatus.values()), requireContext(), t -> resources.getString(t.displayResourceId()), t -> {
            if (viewModel.getStatus() != t) {
                viewModel.setStatus(t);
                onStatusChanged();
            }
        });
    }

    private void onExpectedStartChipClick(View view) {
        Log.d(LOG_TAG, "Enter onExpectedStartChipClick");
        LocalDate date = viewModel.getExpectedStart();
        if (null == date && null == (date = viewModel.getExpectedEnd())) {
            AbstractTermEntity<?> term = viewModel.getSelectedTerm();
            if (null != term) {
                Stream<TermCourseListItem> filtered = viewModel.getCoursesForTerm().stream();
                Long id = viewModel.getId();
                if (null != id) {
                    filtered = filtered.filter(t -> !Objects.equals(id, t.getId()));
                }
                date = EntityHelper.getLatestDate(term.getStart(), term.getEnd(), filtered).map(t -> t.plusDays(1L)).orElseGet(() -> {
                    LocalDate d = term.getEnd();
                    if (null == d && null == (d = term.getStart())) {
                        return LocalDate.now();
                    }
                    return d;
                });
            } else {
                date = LocalDate.now();
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getExpectedStart())) {
                viewModel.setExpectedStart(v);
                if (viewModel.getStatus() == CourseStatus.UNPLANNED) {
                    viewModel.setStatus(CourseStatus.PLANNED);
                    onStatusChanged();
                }
                onExpectedStartChanged();
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onExpectedStartChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter onExpectedStartChipCloseIconClick");
        if (null != viewModel.getExpectedStart()) {
            viewModel.setExpectedStart(null);
            if (viewModel.getStatus() == CourseStatus.PLANNED && null == viewModel.getExpectedEnd()) {
                viewModel.setStatus(CourseStatus.UNPLANNED);
                onStatusChanged();
            } else {
                onExpectedStartChanged();
            }
        }
    }

    private void onExpectedEndChipClick(View view) {
        Log.d(LOG_TAG, "Enter onExpectedEndChipClick");
        LocalDate date = viewModel.getExpectedEnd();
        if (null == date && null == (date = viewModel.getExpectedStart())) {
            AbstractTermEntity<?> term = viewModel.getSelectedTerm();
            if (null != term) {
                Stream<TermCourseListItem> filtered = viewModel.getCoursesForTerm().stream();
                Long id = viewModel.getId();
                if (null != id) {
                    filtered = filtered.filter(t -> !Objects.equals(id, t.getId()));
                }
                date = EntityHelper.getLatestDate(term.getStart(), term.getEnd(), filtered).map(t -> t.plusDays(1L)).orElseGet(() -> {
                    LocalDate d = term.getEnd();
                    if (null == d && null == (d = term.getStart())) {
                        return LocalDate.now();
                    }
                    return d;
                });
            } else {
                date = LocalDate.now();
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getExpectedEnd())) {
                viewModel.setExpectedEnd(v);
                if (viewModel.getStatus() == CourseStatus.UNPLANNED) {
                    viewModel.setStatus(CourseStatus.PLANNED);
                    onStatusChanged();
                } else {
                    onExpectedEndChanged();
                }
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onExpectedEndChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter onExpectedEndChipCloseIconClick");
        if (null != viewModel.getExpectedEnd()) {
            viewModel.setExpectedEnd(null);
            if (viewModel.getStatus() == CourseStatus.PLANNED && null == viewModel.getExpectedStart()) {
                viewModel.setStatus(CourseStatus.UNPLANNED);
                onStatusChanged();
            } else {
                onExpectedEndChanged();
            }
        }
    }

    private void onActualStartChipClick(View view) {
        Log.d(LOG_TAG, "Enter onActualStartChipClick");
        LocalDate date = viewModel.getActualStart();
        if (null == date && null == (date = viewModel.getExpectedStart()) && null == (date = viewModel.getActualEnd()) && null == (date = viewModel.getExpectedEnd())) {
            date = LocalDate.now();
            AbstractTermEntity<?> term = viewModel.getSelectedTerm();
            if (null != term) {
                LocalDate s = term.getStart();
                if ((null != s && date.compareTo(s) < 0) || (null != (s = term.getEnd()) && date.compareTo(s) > 0)) {
                    Stream<TermCourseListItem> filtered = viewModel.getCoursesForTerm().stream();
                    Long id = viewModel.getId();
                    if (null != id) {
                        filtered = filtered.filter(t -> !Objects.equals(id, t.getId()));
                    }
                    date = EntityHelper.getLatestDate(term.getStart(), term.getEnd(), filtered).map(t -> t.plusDays(1L)).orElseGet(() -> {
                        LocalDate d = term.getStart();
                        if (null == d && null == (d = term.getEnd())) {
                            return LocalDate.now();
                        }
                        return d;
                    });
                }
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getActualStart())) {
                viewModel.setActualStart(v);
                switch (viewModel.getStatus()) {
                    case PLANNED:
                    case UNPLANNED:
                        viewModel.setStatus(CourseStatus.IN_PROGRESS);
                        onStatusChanged();
                        break;
                    default:
                        onActualStartChanged();
                        break;
                }
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onActualStartChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter onActualStartChipCloseIconClick");
        if (null != viewModel.getActualStart()) {
            viewModel.setActualStart(null);
            if (viewModel.getStatus() == CourseStatus.IN_PROGRESS && null == viewModel.getActualEnd()) {
                viewModel.setStatus((null == viewModel.getExpectedStart() && null == viewModel.getExpectedEnd()) ?
                        CourseStatus.UNPLANNED : CourseStatus.PLANNED);
                onStatusChanged();
            } else {
                onActualStartChanged();
            }
        }
    }

    private void onActualEndChipClick(View view) {
        Log.d(LOG_TAG, "Enter onActualEndChipClick");
        LocalDate date = viewModel.getActualEnd();
        if (null == date && null == (date = viewModel.getExpectedEnd()) && null == (date = viewModel.getActualStart()) && null == (date = viewModel.getExpectedStart())) {
            date = LocalDate.now();
            AbstractTermEntity<?> term = viewModel.getSelectedTerm();
            if (null != term) {
                LocalDate s = term.getStart();
                if ((null != s && date.compareTo(s) < 0) || (null != (s = term.getEnd()) && date.compareTo(s) > 0)) {
                    Stream<TermCourseListItem> filtered = viewModel.getCoursesForTerm().stream();
                    Long id = viewModel.getId();
                    if (null != id) {
                        filtered = filtered.filter(t -> !Objects.equals(id, t.getId()));
                    }
                    date = EntityHelper.getLatestDate(term.getStart(), term.getEnd(), filtered).map(t -> t.plusDays(1L)).orElseGet(() -> {
                        LocalDate d = term.getEnd();
                        if (null == d && null == (d = term.getStart())) {
                            return LocalDate.now();
                        }
                        return d;
                    });
                }
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getActualEnd())) {
                viewModel.setActualEnd(v);
                switch (viewModel.getStatus()) {
                    case PLANNED:
                    case UNPLANNED:
                        viewModel.setStatus(CourseStatus.IN_PROGRESS);
                        onStatusChanged();
                        break;
                    default:
                        onActualEndChanged();
                        break;
                }
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onActualEndChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter onActualEndChipCloseIconClick");
        if (null != viewModel.getActualEnd()) {
            viewModel.setActualEnd(null);
            if (viewModel.getStatus() == CourseStatus.IN_PROGRESS && null == viewModel.getActualStart()) {
                viewModel.setStatus((null == viewModel.getExpectedStart() && null == viewModel.getExpectedEnd()) ?
                        CourseStatus.UNPLANNED : CourseStatus.PLANNED);
                onStatusChanged();
            } else {
                onActualEndChanged();
            }
        }
    }

    private void onTermChanged(AbstractTermEntity<?> term) {
        if (null == term) {
            Log.d(LOG_TAG, "Enter onTermChanged(null)");
            termButton.setText(R.string.label_none);
        } else {
            Log.d(LOG_TAG, String.format("Enter onTermChanged(%s)", term));
            termButton.setText(term.getName());
        }
    }

    private void onMentorChanged(AbstractMentorEntity<?> mentor) {
        if (null == mentor) {
            Log.d(LOG_TAG, "Enter onMentorChanged(null)");
            mentorChip.setText(R.string.label_none);
        } else {
            Log.d(LOG_TAG, String.format("Enter onMentorChanged(%s)", mentor));
            mentorChip.setText(mentor.getName());
        }
    }

    private void onStatusChanged() {
        Log.d(LOG_TAG, "Enter onStatusChanged");
        statusButton.setText(viewModel.getStatus().displayResourceId());
        onExpectedStartChanged();
        onExpectedEndChanged();
        onActualStartChanged();
        onActualEndChanged();
    }

    private void onExpectedStartChanged() {
        LocalDate d = viewModel.getExpectedStart();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onExpectedStartChanged(null)");
            expectedStartChip.setText("");
            expectedStartChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onExpectedStartChanged(%s)", d));
            expectedStartChip.setText(FORMATTER.format(d));
            expectedStartChip.setCloseIconVisible(true);
        }
    }

    private void onActualStartChanged() {
        LocalDate d = viewModel.getActualStart();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onActualStartChanged(null)");
            actualStartChip.setText("");
            actualStartChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onActualStartChanged(%s)", d));
            actualStartChip.setText(FORMATTER.format(d));
            actualStartChip.setCloseIconVisible(true);
        }
    }

    private void onExpectedEndChanged() {
        LocalDate d = viewModel.getExpectedEnd();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onExpectedEndChanged(null)");
            expectedEndChip.setText("");
            expectedEndChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onExpectedEndChanged(%s)", d));
            expectedEndChip.setText(FORMATTER.format(d));
            expectedEndChip.setCloseIconVisible(true);
        }
    }

    private void onActualEndChanged() {
        LocalDate d = viewModel.getActualEnd();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onActualEndChanged(null)");
            actualEndChip.setText("");
            actualEndChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onActualEndChanged(%s)", d));
            actualEndChip.setText(FORMATTER.format(d));
            actualEndChip.setCloseIconVisible(true);
        }
    }

}