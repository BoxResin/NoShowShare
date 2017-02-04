package unithon.contest.noshowshare;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

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
}