package unithon.contest.noshowshare;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.maplib.NGeoPoint;

import unithon.contest.noshowshare.databinding.ActivityMapBinding;

/**
 * 지도 액티비티
 */
public class MapActivity extends NMapActivity implements LocationListener
{
	private ActivityMapBinding binding;
	private LocationManager locationManager;
	private NMapController mapController;

	@SuppressWarnings({"ResourceType"})
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

		binding.mapView.setClientId("3nB9Z5hdDo2ezI1EeRrQ");
		binding.mapView.setClickable(true);
		mapController = binding.mapView.getMapController();

		// 현재위치를 가져온다.
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0.0f, this);
	}

	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_to_reservation:
				Intent intent = new Intent(this, MyReservationActivity.class);
				startActivity(intent);

		}
	}

	@SuppressWarnings({"ResourceType"})
	@Override
	public void onLocationChanged(Location location)
	{
		// 현재위치를 가져오자마자 위치추적을 중단한다.
		locationManager.removeUpdates(this);

		// 빙글뱅이를 숨긴다.
		binding.barLoading.setVisibility(View.GONE);

		// 현재 위치로 지도 이동
		mapController.animateTo(new NGeoPoint(location.getLongitude(), location.getLatitude()));
		mapController.setZoomLevel(13);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}
}