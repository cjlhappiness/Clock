package tool;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class mSpinner extends Spinner {
    public mSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
