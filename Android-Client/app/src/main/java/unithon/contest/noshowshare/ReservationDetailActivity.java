package unithon.contest.noshowshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class ReservationDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);
        setSupportActionBar((Toolbar)findViewById(R.id.toolBar));


    }
}
