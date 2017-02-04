package unithon.contest.noshowshare;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import data.Reservation;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class SelectNumberOfPeopleDialog extends Dialog implements View.OnClickListener{
        private TextView confirmBtn;

    private Reservation reservation;
    private int price;
    private int maxCount;

    private Button countup;
    private Button countdown;

    private TextView peopleCount;
    private TextView priceSum;
    private int currentCount;


    public SelectNumberOfPeopleDialog(Context context, Reservation reservation) {
        super(context);
        currentCount = 1;

        this.reservation = reservation;
        this.price = reservation.getDiscountedPrice();
        this.maxCount = reservation.getRemained();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_number_of_people);
        priceSum = (TextView) findViewById(R.id.price_sum);
        priceSum.setText("" + currentCount * price);
        confirmBtn = (TextView) findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);

        countup = (Button) findViewById(R.id.btn_up);
        countdown = (Button) findViewById(R.id.btn_down);

        peopleCount = (TextView) findViewById(R.id.txt_remained);

        countup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCount < maxCount) {
                    currentCount++;
                    priceSum.setText("" + currentCount * price);
                    peopleCount.setText(""+ currentCount);
                }
            }
        });
        countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCount > 1) {
                    currentCount--;
                    priceSum.setText("" + currentCount * price);
                    peopleCount.setText(""+ currentCount);
                }
            }
        });


    }



    @Override
    public void onClick(View v) {


        WarningReservationDialog dialog = new WarningReservationDialog(getContext(), reservation);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        params.height = ActionBar.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();
        dismiss();


    }
}
