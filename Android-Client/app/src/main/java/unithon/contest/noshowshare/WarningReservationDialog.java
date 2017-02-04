package unithon.contest.noshowshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class WarningReservationDialog extends Dialog implements View.OnClickListener {
    TextView textView;
    public WarningReservationDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_warning_reservation);

        textView = (TextView) findViewById(R.id.btn_confirm);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == textView){
            SharedPreferences prefs = getContext().getSharedPreferences("past", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("key", System.currentTimeMillis());
            editor.commit();

            dismiss();
            Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getContext().startActivity(intent);
        }
    }
}
