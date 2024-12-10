package bd.com.taspi.apps;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CustomAlertDialog extends AlertDialog.Builder {
    private final Context context;
    private String title, message;
    private List<String> items;
    private boolean cancelable = true;
    private @DrawableRes int icon = 0;
    private @ColorInt int iconColor;

    private DialogInterface.OnClickListener positiveClickListener, negativeClickListener, neutralClickListener, onItemSelectedListener;
    // Add listeners for other buttons if needed

    private CharSequence negativeButtonText, positiveButtonText, neutralButtonText;
    // Add text for other buttons if needed

    private View view;
    private DialogInterface.OnCancelListener onCancelListener;

    public CustomAlertDialog(Context context) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
    }


    public CustomAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public CustomAlertDialog setTitle(int titleID) {
        return setTitle(context.getString(titleID));
    }

    public CustomAlertDialog setMessage(String message) {
        this.message = message;
        return this;
    }


    @Override
    public CustomAlertDialog setMessage(int messageId) {
        return setMessage(context.getString(messageId));
    }

    @Override
    public CustomAlertDialog setIcon(@DrawableRes int icon) {
        this.icon = icon;
        return this;
    }


    @Override
    public CustomAlertDialog setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveClickListener = listener;
        return this;
    }

    @Override
    public CustomAlertDialog setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        return setPositiveButton(context.getString(textId), listener);
    }

    @Override
    public CustomAlertDialog setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeClickListener = listener;
        return this;
    }

    @Override
    public CustomAlertDialog setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        return setNegativeButton(context.getString(textId), listener);
    }

    @Override
    public CustomAlertDialog setView(View view) {
        this.view = view;
        return this;
    }


    @Override
    public CustomAlertDialog setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.neutralButtonText = text;
        this.neutralClickListener = listener;
        return this;
    }

    @Override
    public CustomAlertDialog setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
        return setNeutralButton(context.getString(textId), listener);
    }

    @Override
    public CustomAlertDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    public CustomAlertDialog setIconColor(@ColorInt int iconColor) {
        this.iconColor = iconColor;
        return this;
    }

    @Override
    public CustomAlertDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    @Override
    public AlertDialog.Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onItemSelectedListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.items = Arrays.stream(items)
                    .map(CharSequence::toString)
                    .collect(Collectors.toList());
        }
        this.onItemSelectedListener = onItemSelectedListener;
        return this;
    }

    public CustomAlertDialog setItems(List<String> items, DialogInterface.OnClickListener onItemSelectedListener) {
        this.items = items;
        this.onItemSelectedListener = onItemSelectedListener;
        return this;
    }

    // Add methods for setting listeners and text for other buttons



    @Override
    public AlertDialog create() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.view_custom_alert_dialog, null);

        //        find views
        Button btnNegative = customView.findViewById(R.id.btnNegative);
        Button btnPositive = customView.findViewById(R.id.btnPositive);
        Button btnNeutral = customView.findViewById(R.id.btnNeutral);
        ImageView iconImg = customView.findViewById(R.id.iconImg);
        TextView titleTV = customView.findViewById(R.id.titleTV);
        TextView messageTV = customView.findViewById(R.id.messageTV);
        LinearLayout customV = customView.findViewById(R.id.customV);
        ListView itemsListView = customView.findViewById(R.id.itemsListView);
        TextInputEditText itemsListViewSearch = customView.findViewById(R.id.itemsListViewSearch);


        super.setView(customView).setCancelable(cancelable);
        AlertDialog dialog = super.create();

        // set items
        if (items != null && !items.isEmpty()) {
            itemsListView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner_alert_dialog, android.R.id.text1, items);
            itemsListView.setAdapter(adapter);
            if (onItemSelectedListener != null) {
                itemsListView.setOnItemClickListener((parent, view, position, id) -> {
                    onItemSelectedListener.onClick(dialog, items.indexOf(adapter.getItem(position)));
                    dialog.dismiss();
                });
            }
            if (items.size() > 5) {
                itemsListViewSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                itemsListViewSearch.setVisibility(View.VISIBLE);
            }
        } else {
            itemsListView.setVisibility(View.GONE);
        }

        // button handler
        if (negativeButtonText != null) {
            btnNegative.setText(negativeButtonText);
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setOnClickListener(v -> {
                if (negativeClickListener != null) {
                    negativeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                }
                dialog.dismiss();
            });
        } else {
            btnNegative.setVisibility(View.GONE);
        }

        if (positiveButtonText != null) {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(positiveButtonText);
            btnPositive.setOnClickListener(v -> {
                if (positiveClickListener != null) {
                    positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
                dialog.dismiss();
            });
        } else {
            btnPositive.setVisibility(View.GONE);
        }

        if (neutralButtonText != null) {
            btnNeutral.setText(neutralButtonText);
            btnNeutral.setVisibility(View.VISIBLE);
            btnNeutral.setOnClickListener(v -> {
                if (neutralClickListener != null) {
                    neutralClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                    btnNeutral.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            });
        } else {
            btnNeutral.setVisibility(View.GONE);
        }

        if (onCancelListener != null) {
            dialog.setOnCancelListener(onCancelListener);
        }

        // adding other custom view
        if (view != null) {
            customV.addView(view);
            customV.setVisibility(View.VISIBLE);
        } else {
            customV.setVisibility(View.GONE);
        }

        // general views handler
        if (icon != 0) {
            iconImg.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), icon, null));
            iconImg.setVisibility(View.VISIBLE);
            if (iconColor != 0) {
                iconImg.getDrawable().setTint(iconColor);
            }
        } else {
            iconImg.setVisibility(View.GONE);
        }
        if (title != null) {
            titleTV.setText(title);
            titleTV.setVisibility(View.VISIBLE);
        } else {
            titleTV.setVisibility(View.VISIBLE);
        }
        if (message != null) {
            messageTV.setText(message);
            messageTV.setVisibility(View.VISIBLE);
        } else {
            messageTV.setVisibility(View.VISIBLE);
        }






        return dialog;
    }
}