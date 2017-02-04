package unithon.contest.noshowshare;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class SelectNumberOfPeopleDialog extends Dialog implements View.OnClickListener{
        private TextView confirmBtn;

    public SelectNumberOfPeopleDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_select_number_of_people);
        confirmBtn = (TextView) findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
