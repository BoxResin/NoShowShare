package unithon.contest.noshowshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class MyReservationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);


    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.btn_back)
            finish();
    }
}
