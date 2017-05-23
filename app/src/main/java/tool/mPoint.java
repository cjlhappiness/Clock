package tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class mPoint extends View {

    private int size;
    private int x, y;
    private int[] color = new int[]{0xFF000000, 0xFF444444, 0xFF888888, 0xFFCCCCCC, 0xFFFF0000, 0xFF00FF00,
            0xFF0000FF, 0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF};

    public mPoint(Context context) {
        super(context);
    }
    public mPoint(Context context, int size, int x, int y) {
        super(context);
        this.size = size;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(color[(int)(Math.random() * 10)]);
        canvas.drawCircle(x ,y ,size, paint);
    }


}
