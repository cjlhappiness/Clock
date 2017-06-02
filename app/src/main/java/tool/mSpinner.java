package tool;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class mSpinner extends Spinner {

    public mSpinner(Context context) {
        super(context);
    }

    public mSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
    }
}
