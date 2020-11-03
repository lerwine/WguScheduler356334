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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditCourseFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(EditCourseFragment.class);
    public static final NumberFormat NUMBER_FORMATTER = NumberFormat.getIntegerInstance();

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

    public EditCourseFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_edit_course, container, false);
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
        viewModel.getTermValidLiveData().observe(viewLifecycleOwner, this::onTermValidChanged);
        viewModel.getNumberValidLiveData().observe(viewLifecycleOwner, this::onNumberValidChanged);
        viewModel.getTitleValidLiveData().observe(viewLifecycleOwner, this::onTitleValidChanged);
        viewModel.getExpectedStartValidationMessageLiveData().observe(viewLifecycleOwner, this::onExpectedStartErrorMessageChanged);
        viewModel.getExpectedEndValidationMessageLiveData().observe(viewLifecycleOwner, this::onExpectedEndMessageChanged);
        viewModel.getActualStartValidationMessageLiveData().observe(viewLifecycleOwner, this::onActualStartErrorMessageChanged);
        viewModel.getActualEndValidationMessageLiveData().observe(viewLifecycleOwner, this::onActualEndMessageChanged);
        viewModel.getCompetencyUnitsValidationMessageLiveData().observe(viewLifecycleOwner, this::onCompetencyUnitsMessageChanged);
        ObserverHelper.subscribeOnce(viewModel.getInitializedCompletable(), viewLifecycleOwner, this::onViewModelInitialized);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onTermValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onTermValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            termButton.setError(null);
        } else {
            termButton.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    private void onNumberValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onNumberValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            courseCodeEditText.setError(null);
        } else {
            courseCodeEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    private void onTitleValidChanged(Boolean isValid) {
        Log.d(LOG_TAG, String.format("Enter onTitleValidChanged(%s)", isValid));
        if (null == isValid || isValid) {
            titleEditText.setError(null);
        } else {
            titleEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onExpectedStartErrorMessageChanged(@NonNull Optional<ResourceMessageFactory> messageFactory) {
        if (messageFactory.isPresent()) {
            ResourceMessageFactory message = messageFactory.get();
            expectedStartChip.setError(message.apply(getResources()), AppCompatResources.getDrawable(requireContext(), message.getLevel().getErrorIcon()));
        } else {
            expectedStartChip.setError(null);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onExpectedEndMessageChanged(@NonNull Optional<ResourceMessageFactory> messageFactory) {
        if (messageFactory.isPresent()) {
            ResourceMessageFactory message = messageFactory.get();
            expectedEndChip.setError(message.apply(getResources()), AppCompatResources.getDrawable(requireContext(), message.getLevel().getErrorIcon()));
        } else {
            expectedEndChip.setError(null);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onActualStartErrorMessageChanged(@NonNull Optional<ResourceMessageFactory> messageFactory) {
        if (messageFactory.isPresent()) {
            ResourceMessageFactory message = messageFactory.get();
            actualStartChip.setError(message.apply(getResources()), AppCompatResources.getDrawable(requireContext(), message.getLevel().getErrorIcon()));
        } else {
            actualStartChip.setError(null);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onActualEndMessageChanged(@NonNull Optional<ResourceMessageFactory> messageFactory) {
        if (messageFactory.isPresent()) {
            ResourceMessageFactory message = messageFactory.get();
            actualEndChip.setError(message.apply(getResources()), AppCompatResources.getDrawable(requireContext(), message.getLevel().getErrorIcon()));
        } else {
            actualEndChip.setError(null);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onCompetencyUnitsMessageChanged(@NonNull Optional<ResourceMessageFactory> messageFactory) {
        if (messageFactory.isPresent()) {
            ResourceMessageFactory message = messageFactory.get();
            competencyUnitsEditText.setError(message.apply(getResources()), AppCompatResources.getDrawable(requireContext(), message.getLevel().getErrorIcon()));
        } else {
            competencyUnitsEditText.setError(null);
        }
    }

    private void onViewModelInitialized() {
        onTermChanged(viewModel.getSelectedTerm());
        onMentorChanged(viewModel.getSelectedMentor());
        courseCodeEditText.setText(viewModel.getNumber());
        competencyUnitsEditText.setText(viewModel.getCompetencyUnitsText());
        titleEditText.setText(viewModel.getTitle());
        onStatusChanged();
        onExpectedStartChanged();
        onActualStartChanged();
        onExpectedEndChanged();
        onActualEndChanged();
        notesEditText.setText(viewModel.getNotes());
        courseCodeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNumber));
        competencyUnitsEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCompetencyUnitsText));
        titleEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setTitle));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
    }

    private void onTermButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onTermButtonClick");
        ObserverHelper.observeOnce(viewModel.getTermOptionsLiveData(), getViewLifecycleOwner(), termListItems -> {
            if (null == termListItems || termListItems.isEmpty()) {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_not_ready, R.string.message_db_read_operation_in_progress, requireContext()).showDialog();
            } else {
                AlertHelper.showSingleSelectDialog(R.string.title_select_term, viewModel.getSelectedTerm(), termListItems, requireContext(), AbstractTermEntity::getName, t -> {
                    viewModel.setSelectedTerm(t);
                    onTermChanged(t);
                });
            }
        });
    }

    private void onMentorChipClick(View view) {
        Log.d(LOG_TAG, "Enter onMentorChipClick");
        ObserverHelper.observeOnce(viewModel.getMentorOptionsLiveData(), getViewLifecycleOwner(), mentorListItems -> {
            if (null == mentorListItems || mentorListItems.isEmpty()) {
                AlertHelper.showSingleSelectDialog(R.string.title_select_mentor, viewModel.getSelectedMentor(), mentorListItems, requireContext(), AbstractMentorEntity::getName, t -> {
                    viewModel.setSelectedMentor(t);
                    onMentorChanged(t);
                });
            } else {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_not_ready, R.string.message_no_mentors, requireContext()).showDialog();
            }
        });
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
                long id = viewModel.getId();
                if (ID_NEW != id) {
                    filtered = filtered.filter(t -> id != t.getId());
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
                long id = viewModel.getId();
                if (ID_NEW != id) {
                    filtered = filtered.filter(t -> id != t.getId());
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
                    long id = viewModel.getId();
                    if (ID_NEW != id) {
                        filtered = filtered.filter(t -> id != t.getId());
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
                    long id = viewModel.getId();
                    if (ID_NEW != id) {
                        filtered = filtered.filter(t -> id != t.getId());
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
            expectedStartChip.setText(FULL_FORMATTER.format(d));
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
            actualStartChip.setText(FULL_FORMATTER.format(d));
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
            expectedEndChip.setText(FULL_FORMATTER.format(d));
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
            actualEndChip.setText(FULL_FORMATTER.format(d));
            actualEndChip.setCloseIconVisible(true);
        }
    }

}