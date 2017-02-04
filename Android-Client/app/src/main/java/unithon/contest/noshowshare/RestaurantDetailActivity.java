package unithon.contest.noshowshare;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import unithon.contest.noshowshare.databinding.ActivityRestaurantDetailBinding;

/**
 * 예약 상세정보 액티비티
 */
public class RestaurantDetailActivity extends AppCompatActivity
{
	private ActivityRestaurantDetailBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_detail);
	}

	public void onClick(View view)
	{
		if (view == binding.btnBack)
		{
			finish();
		}
	}
}
