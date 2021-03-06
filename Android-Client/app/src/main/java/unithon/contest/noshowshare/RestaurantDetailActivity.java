package unithon.contest.noshowshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.WindowManager;

import data.Reservation;
import unithon.contest.noshowshare.databinding.ActivityRestaurantDetailBinding;
import util.G;

/**
 * 예약 상세정보 액티비티
 */
public class RestaurantDetailActivity extends AppCompatActivity
{

	private ActivityRestaurantDetailBinding binding;
	private Reservation reservation;

	@Override
	protected void onResume()
	{
		super.onResume();
		long now = System.currentTimeMillis();
		SharedPreferences pref = getSharedPreferences("past", Context.MODE_PRIVATE);

		// 두 시간이 안 지났을 때
		long past = pref.getLong("key", 0);
		if (now - past < G.VALID_RESERVATION_TIME_MS)
		{
			binding.btnChange.setText("두 시간이 지난 후에 예약 가능");
			binding.btnReserve.setEnabled(false);
		}

		// 두 시간이 지났을 때
		else
		{
			binding.btnChange.setText("예약하기");
			binding.btnReserve.setEnabled(true);

			SharedPreferences.Editor editor = pref.edit();
			editor.putString("reservation", "");
			editor.commit();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_detail);

		// 전달받은 예약 정보를 가져온다.
		reservation = (Reservation) getIntent().getSerializableExtra("reservation");
		init();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		// 전달받은 예약 정보를 가져온다.
		reservation = (Reservation) intent.getSerializableExtra("reservation");
		init();
	}

	private void init()
	{
		// 음식점 정보 출력
		binding.txtRestaurantName.setText(reservation.getRestaurant().getName());
		binding.txtRestaurantLocation.setText(reservation.getRestaurant().getLocationName());
		binding.txtPhone.setText(reservation.getRestaurant().getPhone());

		// 음식 정보 출력
		binding.txtFoodName.setText(reservation.getFood().getName());
		binding.txtRemained.setText(reservation.getRemained() + "");

		// 원가에 취소선 긋기
		SpannableString txtPriceSpan = new SpannableString(reservation.getFood().getPrice() + "원");
		txtPriceSpan.setSpan(new StrikethroughSpan(), 0, txtPriceSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		binding.txtPrice.setText(txtPriceSpan);
		binding.txtDiscountedPrice.setText(reservation.getDiscountedPrice() + "원");

		// 할인율 계산
		double discountRate = 1 - (double) reservation.getDiscountedPrice() / reservation.getFood().getPrice();
		discountRate *= 100;
		binding.txtDiscountRate.setText((int) discountRate + "%");
	}

	public void onClick(View view)
	{
		if (view == binding.btnBack)
		{
			finish();
		}
		else if (view == binding.btnReserve)
		{


			SelectNumberOfPeopleDialog dialog = new SelectNumberOfPeopleDialog(this, reservation);
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.copyFrom(dialog.getWindow().getAttributes());

			params.width = ActionBar.LayoutParams.MATCH_PARENT;
			params.height = ActionBar.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(params);
			dialog.show();
		}
	}
}
