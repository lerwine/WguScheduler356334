package Erwine.Leonard.T.wguscheduler356334.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryOptional;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.MEDIUM_FORMATTER;

public class DatePickerEditView extends ConstraintLayout {

    private static final String LOG_TAG = MainActivity.getLogTag(DatePickerEditView.class);
    public static final Function<String, String> NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private final EditText dateEditText;
    private final ImageButton pickerImageButton;
    private final MutableLiveData<BinaryOptional<LocalDate, Throwable>> selectedDateLiveData;
    private final ArrayList<WeakReference<ObserverProxy<?>>> observerCache;

    @Nullable
    private LocalDate selectedDate;
    @Nullable
    private DateTimeException parseError;
    @NonNull
    private String normalizedText = "";
    @NonNull
    private String dateString = "";

    public DatePickerEditView(Context context) {
        this(context, null, 0, 0);
    }

    public DatePickerEditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public DatePickerEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DatePickerEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        dateEditText = new EditText(context, null, 0, R.style.WGU_Scheduler_TextInputEditText);
        pickerImageButton = new ImageButton(context, null, 0, R.style.WGU_Scheduler_ImageButtonStyle);
        dateEditText.setId(View.generateViewId());
        pickerImageButton.setId(View.generateViewId());
        super.addView(dateEditText);
        super.addView(pickerImageButton);
        pickerImageButton.setImageResource(R.drawable.date_picker_icon);
        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(this);
        int pickerImageButtonId = pickerImageButton.getId();
        mConstraintSet.connect(pickerImageButtonId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        mConstraintSet.connect(pickerImageButtonId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mConstraintSet.constrainWidth(pickerImageButtonId, ConstraintSet.WRAP_CONTENT);
        mConstraintSet.constrainHeight(pickerImageButtonId, ConstraintSet.WRAP_CONTENT);
        int dateEditTextId = dateEditText.getId();
        mConstraintSet.connect(dateEditTextId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        mConstraintSet.connect(dateEditTextId, ConstraintSet.END, pickerImageButtonId, ConstraintSet.START);
        mConstraintSet.connect(dateEditTextId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mConstraintSet.connect(dateEditTextId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mConstraintSet.constrainWidth(dateEditTextId, ConstraintSet.MATCH_CONSTRAINT);
        mConstraintSet.constrainHeight(dateEditTextId, ConstraintSet.WRAP_CONTENT);
        mConstraintSet.applyTo(this);
        selectedDateLiveData = new MutableLiveData<>(BinaryOptional.empty());
        observerCache = new ArrayList<>();
        dateEditText.addTextChangedListener(new DateEditTextChangedListener());
        pickerImageButton.setOnClickListener(this::onPickerImageButtonClick);
        boolean enabled = isEnabled();
        dateEditText.setEnabled(enabled);
        pickerImageButton.setEnabled(enabled);
    }

    @Nullable
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(@Nullable LocalDate selectedDate) {
        if (Objects.equals(this.selectedDate, selectedDate)) {
            return;
        }
        Log.d(LOG_TAG, "Enter setSelectedDate(" + ToStringBuilder.toEscapedString(selectedDate, true) + "); oldValue = " + ToStringBuilder.toEscapedString(this.selectedDate, true));
        parseError = null;
        if (null == (this.selectedDate = selectedDate)) {
            if (!dateString.isEmpty()) {
                normalizedText = dateString = "";
                dateEditText.setText(dateString);
            }
            selectedDateLiveData.postValue(BinaryOptional.empty());
        } else {
            applySelectedDateChange(selectedDate);
        }
    }

    @NonNull
    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        String s = (null == dateString) ? "" : dateString;
        if (!this.dateString.equals(s)) {
            Log.d(LOG_TAG, "Enter setSelectedDate(" + ToStringBuilder.toEscapedString(dateString) + "); oldValue = " + ToStringBuilder.toEscapedString(this.dateString));
            this.dateString = s;
            dateEditText.setText(s);
            applyDateStringChange();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateEditText.setEnabled(enabled);
        pickerImageButton.setEnabled(enabled);
    }

    private synchronized boolean onObserverCacheItemDisposing(@NonNull ObserverProxy<?> item) {
        Iterator<WeakReference<ObserverProxy<?>>> iterator = observerCache.iterator();
        while (iterator.hasNext()) {
            ObserverProxy<?> next = iterator.next().get();
            if (null == next) {
                iterator.remove();
            } else if (next == item) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    private synchronized void removeObserverByTarget(@NonNull Object target) {
        Iterator<WeakReference<ObserverProxy<?>>> iterator = observerCache.iterator();
        while (iterator.hasNext()) {
            ObserverProxy<?> next = iterator.next().get();
            if (null == next) {
                iterator.remove();
            } else if (next.target == target) {
                iterator.remove();
                selectedDateLiveData.removeObserver(next);
                break;
            }
        }
    }

    @MainThread
    public synchronized void observeLocalDateChange(@NonNull LifecycleOwner owner, @NonNull Observer<LocalDate> observer) {
        ObserverProxy<Observer<LocalDate>> proxy = new ObserverProxy<Observer<LocalDate>>(observer) {
            @Override
            protected void onSuccess(@NonNull LocalDate localDate) {
                observer.onChanged(localDate);
            }
        };
        observerCache.add(new WeakReference<>(proxy));
        selectedDateLiveData.observe(owner, proxy);
    }

    @MainThread
    public synchronized Disposable observeLocalDateChange(@NonNull LifecycleOwner owner, @NonNull Consumer<LocalDate> onSuccess, @NonNull Consumer<? super Throwable> onError) {
        ObserverProxy<Consumer<LocalDate>> proxy = new ObserverProxy<Consumer<LocalDate>>(onSuccess) {
            @Override
            protected void onSuccess(@NonNull LocalDate localDate) {
                try {
                    onSuccess.accept(localDate);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }

            @Override
            protected void onError(@NonNull Throwable throwable) {
                try {
                    onError.accept(throwable);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        };
        observerCache.add(new WeakReference<>(proxy));
        selectedDateLiveData.observe(owner, proxy);
        return new Remover(proxy);
    }

    @MainThread
    public synchronized Disposable observeLocalDateChange(@NonNull LifecycleOwner owner, @NonNull Consumer<LocalDate> onSuccess, @NonNull Consumer<? super Throwable> onError, Action onComplete) {
        ObserverProxy<Consumer<LocalDate>> proxy = new ObserverProxy<Consumer<LocalDate>>(onSuccess) {
            @Override
            protected void onSuccess(@NonNull LocalDate localDate) {
                try {
                    onSuccess.accept(localDate);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }

            @Override
            protected void onError(@NonNull Throwable throwable) {
                try {
                    onError.accept(throwable);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }

            @Override
            protected void onEmpty() {
                try {
                    onComplete.run();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        };
        observerCache.add(new WeakReference<>(proxy));
        selectedDateLiveData.observe(owner, proxy);
        return new Remover(proxy);
    }

    @MainThread
    public synchronized void observeLocalDateChange(@NonNull LifecycleOwner owner, @NonNull MaybeObserver<LocalDate> observer) {
        ObserverProxy<MaybeObserver<LocalDate>> proxy = new ObserverProxy<MaybeObserver<LocalDate>>(observer) {
            @Override
            protected void onSuccess(@NonNull LocalDate localDate) {
                Log.d(LOG_TAG, "Enter observeLocalDateChange.onSuccess(" + ToStringBuilder.toEscapedString(localDate, false) + ")");
                try {
                    observer.onSuccess(localDate);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }

            @Override
            protected void onError(@NonNull Throwable throwable) {
                Log.d(LOG_TAG, "Enter observeLocalDateChange.onError(" + ToStringBuilder.toEscapedString(throwable.getMessage()) + ")");
                try {
                    observer.onError(throwable);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }

            @Override
            protected void onEmpty() {
                Log.d(LOG_TAG, "Enter observeLocalDateChange.onEmpty()");
                try {
                    observer.onComplete();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        };
        observerCache.add(new WeakReference<>(proxy));
        selectedDateLiveData.observe(owner, proxy);
    }

    @MainThread
    public void removeLocalDateChangeObserver(@NonNull Observer<LocalDate> observer) {
        removeObserverByTarget(observer);
    }

    @MainThread
    public void removeLocalDateChangeObserver(@NonNull MaybeObserver<LocalDate> observer) {
        removeObserverByTarget(observer);
    }

    @MainThread
    public void removeLocalDateChangeObservers(@NonNull LifecycleOwner owner) {
        selectedDateLiveData.removeObservers(owner);
    }

    public void setError(CharSequence error) {
        dateEditText.setError(error);
    }

    public void setError(CharSequence error, Drawable icon) {
        dateEditText.setError(error, icon);
    }

    private void applySelectedDateChange(@NonNull LocalDate date) {
        if (!(normalizedText = MEDIUM_FORMATTER.format(date)).equals(dateString)) {
            dateString = normalizedText;
            dateEditText.setText(dateString);
        }
        selectedDateLiveData.postValue(BinaryOptional.ofPrimary(date));
    }

    private void applyDateStringChange() {
        Log.d(LOG_TAG, "Enter applyDateStringChange(); dateString = " + ToStringBuilder.toEscapedString(dateString));
        String t = NORMALIZER.apply(dateString);
        if (t.equals(normalizedText)) {
            return;
        }
        normalizedText = t;
        LocalDate date;
        try {
            date = LocalDateConverter.fromString(normalizedText);
        } catch (DateTimeException e0) {
            Log.d(LOG_TAG, e0.getClass().getName() + " parsing " + ToStringBuilder.toEscapedString(normalizedText) + ": " + e0);
            selectedDate = null;
            if (null == parseError || !Objects.equals(parseError.getMessage(), e0.getMessage())) {
                parseError = e0;
                selectedDateLiveData.postValue(BinaryOptional.ofSecondary(parseError));
            }
            return;
        }
        parseError = null;
        if (!Objects.equals(date, selectedDate)) {
            selectedDate = date;
            selectedDateLiveData.postValue(BinaryOptional.ofPrimaryNullable(selectedDate));
        }
    }

    public synchronized void onPickerImageButtonClick(View view) {
        LocalDate date = (null == initialPickerDateFactory) ? selectedDate : initialPickerDateFactory.apply(selectedDate);
        if (null == date) {
            date = LocalDate.now();
        }
        new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
            LocalDate d = LocalDate.of(year, month + 1, dayOfMonth);
            Log.d(LOG_TAG, "onPickerImageButtonClick: Selected " + ToStringBuilder.toEscapedString(d, true));
            if (d.equals(selectedDate)) {
                return;
            }
            parseError = null;
            selectedDate = d;
            applySelectedDateChange(selectedDate);
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    @Nullable
    private Function<LocalDate, LocalDate> initialPickerDateFactory;

    @Nullable
    public Function<LocalDate, LocalDate> getInitialPickerDateFactory() {
        return initialPickerDateFactory;
    }

    public synchronized void setInitialPickerDateFactory(@Nullable Function<LocalDate, LocalDate> initialPickerDateFactory) {
        this.initialPickerDateFactory = initialPickerDateFactory;
    }

    private class DateEditTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(LOG_TAG, "Enter onTextChanged(" + ToStringBuilder.toEscapedString(s) + ", " + start + ", " + before + ", " + count + ")");
            CharSequence charSequence = dateEditText.getText();
            String t = (null == charSequence) ? "" : charSequence.toString();
            if (!dateString.equals(t)) {
                dateString = t;
                applyDateStringChange();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class Remover implements Disposable {
        private WeakReference<ObserverProxy<?>> proxyRef;

        Remover(ObserverProxy<?> proxy) {
            proxyRef = new WeakReference<>(proxy);
        }

        @Override
        public synchronized void dispose() {
            if (null != proxyRef) {
                ObserverProxy<?> proxy = proxyRef.get();
                if (null != proxy) {
                    if (onObserverCacheItemDisposing(proxy)) {
                        selectedDateLiveData.removeObserver(proxy);
                    }
                }
                proxyRef = null;
            }
        }

        @Override
        public boolean isDisposed() {
            return null == proxyRef;
        }
    }

    public abstract static class ObserverProxy<T> implements Observer<BinaryOptional<LocalDate, Throwable>> {
        @NonNull
        private final T target;

        ObserverProxy(@NonNull T target) {
            this.target = target;
        }

        @Override
        public void onChanged(BinaryOptional<LocalDate, Throwable> value) {
            value.switchPresence(
                    localDate -> Log.d(LOG_TAG, "Enter ObserverProxy.onChanged.success(" + ToStringBuilder.toEscapedString(localDate, false) + ")"),
                    throwable -> Log.d(LOG_TAG, "Enter ObserverProxy.onChanged.onError(" + ToStringBuilder.toEscapedString(throwable.getMessage()) + ")"),
                    () -> Log.d(LOG_TAG, "Enter ObserverProxy.onChanged.complete()")
            );

            value.switchPresence(this::onSuccess, this::onError, this::onEmpty);
        }

        protected abstract void onSuccess(@NonNull LocalDate localDate);

        protected void onError(@NonNull Throwable throwable) {
        }

        protected void onEmpty() {
        }
    }
}
