package unithon.contest.noshowshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class MyReservationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);


    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reserve:

                WarningReservationDialog dialog = new WarningReservationDialog(this);
                WindowManager.LayoutParams wm = dialog.getWindow().getAttributes();
                wm.copyFrom(dialog.getWindow().getAttributes());
                wm.width = 1200;
                wm.height = 1700;
                dialog.getWindow().setAttributes(wm);
                dialog.show();
        }
    }
}
