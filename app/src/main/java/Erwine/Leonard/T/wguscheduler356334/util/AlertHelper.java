package Erwine.Leonard.T.wguscheduler356334.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.R;

public class AlertHelper {
    private final AlertDialog.Builder builder;

    @NonNull
    private static DialogInterface.OnCancelListener createOnCancelListener(@Nullable Runnable onCancel) {
        return (null == onCancel) ? DialogInterface::dismiss : dialog -> {
            onCancel.run();
            dialog.dismiss();
        };
    }

    @NonNull
    private static DialogInterface.OnClickListener createOnClickListener(@Nullable Runnable runnable) {
        if (null == runnable) {
            return (dialog, which) -> dialog.dismiss();
        }
        return (dialog, which) -> {
            runnable.run();
            dialog.dismiss();
        };
    }

    public static void showEditMultiLineTextDialog(@StringRes int titleId, @NonNull String text, @NonNull Context context, @NonNull Consumer<String> onChanged) {
        EditText editText = new EditText(context);
        editText.setText(text);
        editText.setSingleLine(false);
        editText.setVerticalScrollBarEnabled(true);
        editText.setBackgroundResource(android.R.drawable.edit_text);
        AlertDialog dlg = new AlertDialog.Builder(context).setTitle(titleId).setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String s = editText.getText().toString();
                    if (!text.equals(s)) {
                        onChanged.accept(s);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(false).create();
        dlg.show();
    }

    public AlertHelper(@DrawableRes int iconId, @StringRes int titleId, @NonNull View view, @NonNull Context context) {
        builder = new AlertDialog.Builder(context).setIcon(iconId).setTitle(titleId).setView(view);
    }

    public AlertHelper(@DrawableRes int iconId, @StringRes int titleId, @StringRes int messageId, @NonNull Context context) {
        builder = new AlertDialog.Builder(context).setIcon(iconId).setTitle(titleId).setMessage(messageId);
    }

    public AlertHelper(@DrawableRes int iconId, @StringRes int titleId, @NonNull String message, @NonNull Context context) {
        builder = new AlertDialog.Builder(context).setIcon(iconId).setTitle(titleId).setMessage(message);
    }

    public AlertHelper(@DrawableRes int iconId, @StringRes int titleId, @NonNull Context context, @StringRes int messageId, Object... formatArgs) {
        builder = new AlertDialog.Builder(context).setIcon(iconId).setTitle(titleId).setMessage(context.getResources().getString(messageId, formatArgs));
    }

    private AlertDialog createYesNoCancelDialog(@Nullable Runnable onYes, @Nullable Runnable onNo, @Nullable Runnable onCancel) {
        return builder.setPositiveButton(R.string.response_yes, createOnClickListener(onYes))
                .setNegativeButton(R.string.response_no, createOnClickListener(onNo))
                .setCancelable(true).setOnCancelListener(createOnCancelListener(onCancel)).create();
    }

    public void showYesNoCancelDialog(@Nullable Runnable onYes, @Nullable Runnable onNo, @Nullable Runnable onCancel) {
        createYesNoCancelDialog(onYes, onNo, onCancel).show();
    }

    public AlertDialog createYesNoCancelDialog(@NonNull BooleanConsumer onYesOrNo, @Nullable Runnable onCancel) {
        return builder.setPositiveButton(R.string.response_yes, (dialog, which) -> {
            onYesOrNo.accept(true);
            dialog.dismiss();
        }).setNegativeButton(R.string.response_no, (dialog, which) -> {
            onYesOrNo.accept(false);
            dialog.dismiss();
        }).setCancelable(true).setOnCancelListener(createOnCancelListener(onCancel)).create();
    }

    public void showYesNoCancelDialog(@NonNull BooleanConsumer onYesOrNo, @Nullable Runnable onCancel) {
        createYesNoCancelDialog(onYesOrNo, onCancel).show();
    }

    public AlertDialog createYesNoCancelDialog(@NonNull Consumer<Boolean> onClosed) {
        return builder.setPositiveButton(R.string.response_yes, (dialog, which) -> {
            onClosed.accept(true);
            dialog.dismiss();
        }).setNegativeButton(R.string.response_no, (dialog, which) -> {
            onClosed.accept(false);
            dialog.dismiss();
        }).setCancelable(true).setOnCancelListener(dialog -> {
            onClosed.accept(null);
            dialog.dismiss();
        }).create();
    }

    public void showYesNoCancelDialog(@NonNull Consumer<Boolean> onClosed) {
        createYesNoCancelDialog(onClosed).show();
    }

    public AlertDialog createYesNoDialog(@Nullable Runnable onYes, @Nullable Runnable onNo) {
        return builder.setPositiveButton(R.string.response_yes, createOnClickListener(onYes)).setNegativeButton(R.string.response_no, createOnClickListener(onNo)).setCancelable(false).create();
    }

    public void showYesNoDialog(@Nullable Runnable onYes, @Nullable Runnable onNo) {
        createYesNoDialog(onYes, onNo).show();
    }

    public AlertDialog createYesNoDialog(@NonNull BooleanConsumer onClosed) {
        return builder.setPositiveButton(R.string.response_yes, (dialog, which) -> {
            onClosed.accept(true);
            dialog.dismiss();
        }).setNegativeButton(R.string.response_no, (dialog, which) -> {
            onClosed.accept(false);
            dialog.dismiss();
        }).setCancelable(false).create();
    }

    public void showYesNoDialog(@NonNull BooleanConsumer onClosed) {
        createYesNoDialog(onClosed).show();
    }

    public AlertDialog createDialog(@NonNull Runnable onClose) {
        return builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            onClose.run();
            dialog.dismiss();
        }).setCancelable(false).create();
    }

    public void showDialog(@NonNull Runnable onClose) {
        createDialog(onClose).show();
    }

    public AlertDialog createDialog() {
        return builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).setCancelable(false).create();
    }

    public void showDialog() {
        createDialog().show();
    }
}
