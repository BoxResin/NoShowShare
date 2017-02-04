package unithon.contest.noshowshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import data.Reservation;
import unithon.contest.noshowshare.databinding.ActivityMyReservationBinding;
import util.NMapViewerResourceProvider;

/**
 * Created by minhyeon on 2017-02-04.
 */
public class MyReservationActivity extends NMapActivity
{
	private ActivityMyReservationBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_my_reservation);

		SharedPreferences pref = getSharedPreferences("past", Context.MODE_PRIVATE);
		String serializedReservation = pref.getString("reservation", "");

		// 저장된 예약 정보가 있을 때
		if (!serializedReservation.equals(""))
		{
			Gson gson = new Gson();
			Reservation reservation = gson.fromJson(serializedReservation, Reservation.class);

			// 정보 업데이트
			binding.ifNoReservation.setVisibility(View.GONE);
			binding.ifReservationExists.setVisibility(View.VISIBLE);

			binding.txtRestaurantName.setText(reservation.getRestaurant().getName());
			binding.txtRestaurantLocation.setText(reservation.getRestaurant().getLocationName());
			binding.txtPhone.setText(reservation.getRestaurant().getPhone());

			binding.mapView.setClientId("3nB9Z5hdDo2ezI1EeRrQ");
			double lng = reservation.getRestaurant().getLng();
			double lat = reservation.getRestaurant().getLat();
			NMapController mapController = binding.mapView.getMapController();
			mapController.setZoomLevel(13);
			mapController.setMapCenter(new NGeoPoint(lng, lat));

			// 지도에 마커 추가
			final NMapViewerResourceProvider provider = new NMapViewerResourceProvider(this);
			final NMapOverlayManager overlayManager = new NMapOverlayManager(this, binding.mapView, provider);

			final NMapPOIdata mapPOIdata = new NMapPOIdata(10, provider);
			mapPOIdata.beginPOIdata(1);
			mapPOIdata.addPOIitem(new NGeoPoint(lng, lat), null, 1, null, 1);
			mapPOIdata.endPOIdata();

			NMapPOIdataOverlay poiDataOverlay = overlayManager.createPOIdataOverlay(mapPOIdata, getResources().getDrawable(R.drawable.map_myplace));
			overlayManager.addOverlay(poiDataOverlay);
		}
	}

	public void onClick(View view)
	{
		if (view.getId() == R.id.btn_back)
			finish();
	}
}
