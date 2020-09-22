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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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
import java.util.function.Supplier;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

/**
 * Fragment for editing the properties of a {@link Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity}.
 * This assumes that the parent activity ({@link Erwine.Leonard.T.wguscheduler356334.AddCourseActivity} or {@link Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity})
 * calls {@link EditCourseViewModel#initializeViewModelState(Bundle, Supplier)}.
 */
public class EditCourseFragment extends Fragment {

    private static final String LOG_TAG = EditCourseFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

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
    private TextView notesTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditCourseFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCreateView");
        return inflater.inflate(R.layout.fragment_edit_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        termButton = view.findViewById(R.id.termButton);
        courseCodeEditText = view.findViewById(R.id.courseCodeEditText);
        competencyUnitsEditText = view.findViewById(R.id.competencyUnitsEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        mentorChip = view.findViewById(R.id.mentorChip);
        statusButton = view.findViewById(R.id.statusButton);
        expectedStartChip = view.findViewById(R.id.expectedStartChip);
        expectedEndChip = view.findViewById(R.id.expectedEndChip);
        actualStartChip = view.findViewById(R.id.actualStartChip);
        actualEndChip = view.findViewById(R.id.actualEndChip);
        notesTextView = view.findViewById(R.id.notesTextView);
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
        view.findViewById(R.id.editNotesFloatingActionButton).setOnClickListener(this::onEditNotesFloatingActionButtonClick);
        view.findViewById(R.id.saveCourseButton).setOnClickListener(this::onSaveCourseButtonClick);
        view.findViewById(R.id.deleteCourseButton).setOnClickListener(this::onDeleteCourseButtonClick);
        view.findViewById(R.id.cancelButton).setOnClickListener(this::onCancelButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onActivityCreated");
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
        if (null == isValid || isValid) {
            termButton.setError(null);
        } else {
            termButton.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onNumberValidChanged(Boolean isValid) {
        if (null == isValid || isValid) {
            courseCodeEditText.setError(null);
        } else {
            courseCodeEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onTitleValidChanged(Boolean isValid) {
        if (null == isValid || isValid) {
            titleEditText.setError(null);
        } else {
            titleEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onExpectedStartErrorMessageChanged(@StringRes Integer id) {
        if (null != id) {
            expectedStartChip.setError(getResources().getString(id));
        } else if (null == (id = viewModel.getExpectedStartWarningMessageLiveData().getValue())) {
            expectedStartChip.setError(null);
        } else {
            expectedStartChip.setError(getResources().getString(id), getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
        }
    }

    private void onExpectedStartWarningMessageChanged(Integer id) {
        if (null == viewModel.getExpectedStartErrorMessageLiveData().getValue()) {
            if (null == id) {
                expectedStartChip.setError(null);
            } else {
                expectedStartChip.setError(getResources().getString(id), getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
            }
        }
    }

    private void onExpectedEndMessageChanged(@StringRes Integer id) {
        if (null == id) {
            expectedEndChip.setError(null);
        } else {
            String message = getResources().getString(id);
            if (id == R.string.message_required) {
                expectedEndChip.setError(message);
            } else {
                expectedEndChip.setError(message, getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
            }
        }
    }

    private void onActualStartErrorMessageChanged(@StringRes Integer id) {
        if (null != id) {
            actualStartChip.setError(getResources().getString(id));
            actualStartChip.setError(null);
        } else if (null == (id = viewModel.getActualStartWarningMessageLiveData().getValue())) {
            actualStartChip.setError(null);
        } else {
            actualStartChip.setError(getResources().getString(id), getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
        }
    }

    private void onActualStartWarningMessageChanged(Integer id) {
        if (null == viewModel.getActualStartErrorMessageLiveData().getValue()) {
            if (null == id) {
                actualStartChip.setError(null);
            } else {
                actualStartChip.setError(getResources().getString(id), getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
            }
        }
    }

    private void onActualEndMessageChanged(@StringRes Integer id) {
        if (null == id) {
            actualEndChip.setError(null);
        } else {
            String message = getResources().getString(id);
            if (id == R.string.message_required) {
                actualEndChip.setError(message);
            } else {
                actualEndChip.setError(message, getResources().getDrawable(R.drawable.dialog_warning, requireActivity().getTheme()));
            }
        }
    }

    private void onCompetencyUnitsMessageChanged(@StringRes Integer id) {
        if (null == id) {
            competencyUnitsEditText.setError(null);
        } else {
            competencyUnitsEditText.setError(getResources().getString(id));
        }
    }

    private void onEntityLoaded(CourseDetails entity) {
        if (null == entity) {
            return;
        }
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
        onExpectedStartChanged();
        onActualStartChanged();
        onExpectedEndChanged();
        onActualEndChanged();
        notesTextView.setText(viewModel.getNotes());
    }

    private void onTermsLoaded(List<TermListItem> termListItems) {
        AbstractTermEntity<?> term = viewModel.initializeTermProperty(termListItems);
        if (null != term) {
            onTermChanged(term);
        }
    }

    private void onMentorsLoaded(List<MentorListItem> mentorListItems) {
        AbstractMentorEntity<?> mentor = viewModel.initializeMentorProperty(mentorListItems);
        if (null != mentor) {
            onMentorChanged(mentor);
        }
    }

    private void onTermButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onTermButtonClick");
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
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onMentorChipClick");
        List<MentorListItem> mentorListItems = viewModel.getMentorsLiveData().getValue();
        if (null != mentorListItems) {
            AlertHelper.showSingleSelectDialog(R.string.title_select_mentor, viewModel.getSelectedMentor(), mentorListItems, requireContext(), AbstractMentorEntity::getName, t -> {
                viewModel.setSelectedMentor(t);
                onMentorChanged(t);
            });
        }
    }

    private void onMentorChipCloseIconClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onMentorChipCloseIconClick");
        viewModel.setSelectedMentor(null);
        onMentorChanged(null);
    }

    private void onStatusButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onStatusButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_status, viewModel.getStatus(), Arrays.asList(CourseStatus.values()), requireContext(), t -> resources.getString(t.displayResourceId()), t -> {
            if (viewModel.getStatus() != t) {
                viewModel.setStatus(t);
                statusButton.setText(t.displayResourceId());
                onStatusChanged();
            }
        });
    }

    private void onExpectedStartChipClick(View view) {
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
                onExpectedStartChanged();
            }
        }, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onExpectedStartChipCloseIconClick(View view) {
        if (null != viewModel.getExpectedStart()) {
            viewModel.setExpectedStart(null);
            onExpectedStartChanged();
        }
    }

    private void onExpectedEndChipClick(View view) {
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
                onExpectedEndChanged();
            }
        }, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onExpectedEndChipCloseIconClick(View view) {
        if (null != viewModel.getExpectedEnd()) {
            viewModel.setExpectedEnd(null);
            onExpectedEndChanged();
        }
    }

    private void onActualStartChipClick(View view) {
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
                onExpectedStartChanged();
            }
        }, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onActualStartChipCloseIconClick(View view) {
        if (null != viewModel.getActualStart()) {
            viewModel.setActualStart(null);
            onActualStartChanged();
        }
    }

    private void onActualEndChipClick(View view) {
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
                onActualEndChanged();
            }
        }, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onActualEndChipCloseIconClick(View view) {
        if (null != viewModel.getActualEnd()) {
            viewModel.setActualEnd(null);
            onActualEndChanged();
        }
    }

    private void onEditNotesFloatingActionButtonClick(View view) {
        AlertHelper.showEditMultiLineTextDialog(R.string.title_edit_notes, viewModel.getNotes(), requireContext(), s -> {
            notesTextView.setText(s);
            viewModel.setNotes(s);
        });
    }

    private void onSaveCourseButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onSaveCourseButtonClick");
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onSaveCourseButtonClick
    }

    private void onDeleteCourseButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onDeleteCourseButtonClick");
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onDeleteCourseButtonClick
    }

    private void onCancelButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCancelButtonClick");
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCancelButtonClick
    }

    private void onTermChanged(AbstractTermEntity<?> term) {
        if (null == term) {
            termButton.setText(R.string.label_none);
        } else {
            termButton.setText(term.getName());
        }
    }

    private void onMentorChanged(AbstractMentorEntity<?> mentor) {
        if (null == mentor) {
            mentorChip.setText(R.string.label_none);
        } else {
            mentorChip.setText(mentor.getName());
        }
    }

    private void onStatusChanged() {
        statusButton.setText(viewModel.getStatus().displayResourceId());
        onExpectedStartChanged();
        onExpectedEndChanged();
        onActualStartChanged();
        onActualEndChanged();
    }

    private void onExpectedStartChanged() {
        LocalDate d = viewModel.getExpectedStart();
        if (null == d) {
            expectedStartChip.setText("");
            expectedStartChip.setCloseIconVisible(false);
        } else {
            expectedStartChip.setText(FORMATTER.format(d));
            expectedStartChip.setCloseIconVisible(true);
        }
    }

    private void onActualStartChanged() {
        LocalDate d = viewModel.getActualStart();
        if (null == d) {
            actualStartChip.setText("");
            actualStartChip.setCloseIconVisible(false);
        } else {
            actualStartChip.setText(FORMATTER.format(d));
            actualStartChip.setCloseIconVisible(true);
        }
    }

    private void onExpectedEndChanged() {
        LocalDate d = viewModel.getExpectedEnd();
        if (null == d) {
            expectedEndChip.setText("");
            expectedEndChip.setCloseIconVisible(false);
        } else {
            expectedEndChip.setText(FORMATTER.format(d));
            expectedEndChip.setCloseIconVisible(true);
        }
    }

    private void onActualEndChanged() {
        LocalDate d = viewModel.getActualEnd();
        if (null == d) {
            actualEndChip.setText("");
            actualEndChip.setCloseIconVisible(false);
        } else {
            actualEndChip.setText(FORMATTER.format(d));
            actualEndChip.setCloseIconVisible(true);
        }
    }

}