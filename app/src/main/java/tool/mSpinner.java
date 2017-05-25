package tool;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.Spinner;

public class mSpinner extends Spinner {

    public mSpinner(Context context) {
        super(context);
    }

    public mSpinner(Context context, int mode) {
        super(context, mode);
    }

    public mSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(),
                    position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(),
                    position, getSelectedItemId());
        }
    }
}
