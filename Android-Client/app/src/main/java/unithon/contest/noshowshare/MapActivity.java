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
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.Reservation;
import unithon.contest.noshowshare.databinding.ActivityMapBinding;
import util.HttpRequester;
import util.NMapViewerResourceProvider;

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
	public void onLocationChanged(final Location location)
	{
		// 현재위치를 가져오자마자 위치추적을 중단한다.
		locationManager.removeUpdates(this);

		// 빙글뱅이를 숨긴다.
		binding.barLoading.setVisibility(View.GONE);

		// 현재 위치로 지도 이동
		mapController.animateTo(new NGeoPoint(location.getLongitude(), location.getLatitude()));
		mapController.setZoomLevel(13);

		// 현재 위치에 마커를 띄운다.
		NMapOverlayItem overlayItem = new NMapOverlayItem(new NGeoPoint(location.getLongitude(), location.getLatitude()),
				"title", "snippet", getResources().getDrawable(R.drawable.map_myplace));

		final NMapViewerResourceProvider provider = new NMapViewerResourceProvider(this);
		final NMapOverlayManager overlayManager = new NMapOverlayManager(this, binding.mapView, provider);

		final NMapPOIdata mapPOIdata = new NMapPOIdata(10, provider);
		mapPOIdata.beginPOIdata(1);
		mapPOIdata.addPOIitem(new NGeoPoint(location.getLongitude(), location.getLatitude()), null, 1, null, 1);
		mapPOIdata.endPOIdata();

		NMapPOIdataOverlay poiDataOverlay = overlayManager.createPOIdataOverlay(mapPOIdata, getResources().getDrawable(R.drawable.map_myplace));
		overlayManager.addOverlay(poiDataOverlay);

		// 서버에 현재 위치와 가까운 음식점 정보를 요청한다.
		new HttpRequester(String.format("http://52.78.44.216:3000/users/reservation/1500?lat=%f&lng=%f",
				location.getLatitude(), location.getLongitude())).request(HttpRequester.Method.GET,
				null, new HttpRequester.HttpRequestListener()
				{
					@Override
					public void onHttpResult(String data, HttpRequester.Error error)
					{
						if (error != HttpRequester.Error.OK)
							return;

						try
						{
							JSONObject jsonRoot = new JSONObject(data);
							if (jsonRoot.getString("result").equals("true"))
							{
								// json에서 음식점 예약 정보를 가져온다.
								JSONArray jsonRestaurants = jsonRoot.getJSONArray("list");

								NMapPOIdata mapPOIdata = new NMapPOIdata(10, provider);
								mapPOIdata.beginPOIdata(jsonRestaurants.length());
								for (int i = 0; i < jsonRestaurants.length(); i++)
								{
									JSONObject jsonRestaurant = jsonRestaurants.getJSONObject(i);
									Reservation reservation = Reservation.fromJson(jsonRestaurant);
									mapPOIdata.addPOIitem(new NGeoPoint(reservation.getRestaurant().getLng(),
											reservation.getRestaurant().getLat()), "title", i + 1, reservation, i + 1)
											.setHeadText("ASDF");
								}
								mapPOIdata.endPOIdata();

								NMapPOIdataOverlay poiDataOverlay = overlayManager.createPOIdataOverlay(mapPOIdata, getResources().getDrawable(R.drawable.map_store_none_clicked));
								poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener()
								{
									@Override
									public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem)
									{
									}

									@Override
									public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem)
									{
										binding.reservationInfo.reservationCard.setVisibility(View.VISIBLE);
										binding.reservationInfo.ribbon.setVisibility(View.GONE);
										Reservation reservation = (Reservation) nMapPOIitem.getTag();
										reservation.map(binding.reservationInfo.reservationCard, MapActivity.this);
									}
								});
								overlayManager.addOverlay(poiDataOverlay);
							}
						}
						catch (JSONException e)
						{
						}
					}
				});
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