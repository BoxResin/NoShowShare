package unithon.contest.noshowshare;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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

    private TextView txtFoodName;
    private TextView txtPrice;
    private TextView txtDiscountedPrice;
    private TextView txtDiscountRate;
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

        txtFoodName = (TextView) findViewById(R.id.txt_food_name);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtDiscountedPrice = (TextView) findViewById(R.id.txt_discounted_price);
        txtDiscountRate = (TextView) findViewById(R.id.txt_discount_rate);

        txtFoodName.setText(reservation.getFood().getName());
        txtPrice.setText(reservation.getFood().getPrice() + "원");
        txtDiscountedPrice.setText(reservation.getDiscountedPrice() + "원");

        // 원가에 취소선 긋기
        SpannableString txtPriceSpan = new SpannableString(reservation.getFood().getPrice() + "원");
        txtPriceSpan.setSpan(new StrikethroughSpan(), 0, txtPriceSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        txtPrice.setText(txtPriceSpan);

        // 할인율 계산
        double discountRate = 1 - (double) reservation.getDiscountedPrice() / reservation.getFood().getPrice();
        discountRate *= 100;
        txtDiscountRate.setText((int) discountRate + "%");

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
