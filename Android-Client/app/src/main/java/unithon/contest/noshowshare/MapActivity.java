package unithon.contest.noshowshare;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.nhn.android.maps.NMapActivity;

import unithon.contest.noshowshare.databinding.ActivityMapBinding;

/**
 * 지도 액티비티
 */
public class MapActivity extends NMapActivity
{
	private ActivityMapBinding binding;

	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

		binding.mapView.setClientId("3nB9Z5hdDo2ezI1EeRrQ");
		binding.mapView.setClickable(true);


	}

	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_to_reservation:
				Intent intent = new Intent(this, MyReservationActivity.class);
				startActivity(intent);

		}
	}
}