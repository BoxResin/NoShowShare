package unithon.contest.noshowshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import data.Reservation;
import unithon.contest.noshowshare.databinding.ActivityMainBinding;
import util.HttpRequester;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private ArrayAdapter<Reservation> reservationAdapter;

	private Reservation bestReservation; // 최고 할인 정보
	private Reservation recentReservation; // 최신 정보

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		// 예약 리스트뷰 초기화
		reservationAdapter = new ArrayAdapter<Reservation>(this, R.layout.item_reservation_info)
		{
			@NonNull
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = getLayoutInflater().inflate(R.layout.item_reservation_info, parent, false);

				Reservation reservation = getItem(position);
				reservation.map(convertView, MainActivity.this);

				return convertView;
			}
		};
		binding.listReservation.setAdapter(reservationAdapter);

		// 지역 선택 스피너 초기화
		AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String firstArea_str = binding.spinFirstArea.getSelectedItem().toString();
				String secondArea_str = binding.spinSecondArea.getSelectedItem().toString();
				String thirdArea_str = binding.spinThirdArea.getSelectedItem().toString();

				// 사용자가 지역(시/군/구)을 모두 선택했을 때
				if (!firstArea_str.equals("시/도") && !secondArea_str.equals("시/군/도") && !thirdArea_str.equals("동/읍/면"))
				{
					binding.bestRecent.setVisibility(View.GONE);
					binding.listReservation.setVisibility(View.VISIBLE);

					// 해당 지역의 모든 예약 정보를 가져온다.
					reservationAdapter.clear();
					new HttpRequester(String.format("http://52.78.44.216:3000/users/%s/%s/%s",
							firstArea_str, secondArea_str, thirdArea_str))
							.request(HttpRequester.Method.GET, null, new HttpRequester.HttpRequestListener()
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

											for (int i = 0; i < jsonRestaurants.length(); i++)
											{
												JSONObject jsonRestaurant = jsonRestaurants.getJSONObject(i);
												reservationAdapter.add(Reservation.fromJson(jsonRestaurant));
											}
										}
									}
									catch (JSONException e)
									{
									}
								}
							});
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		};
		binding.spinFirstArea.setOnItemSelectedListener(itemSelectedListener);
		binding.spinSecondArea.setOnItemSelectedListener(itemSelectedListener);
		binding.spinThirdArea.setOnItemSelectedListener(itemSelectedListener);

		// 최고 할인률 정보 가져오기
		new HttpRequester("http://52.78.44.216:3000/users/best").request(HttpRequester.Method.GET,
				null, 7000, new HttpRequester.HttpRequestListener()
				{
					@Override
					public void onHttpResult(String data, HttpRequester.Error error)
					{
						try
						{
							if (error == HttpRequester.Error.OK)
							{
								JSONObject jsonRoot = new JSONObject(data);
								if (jsonRoot.getString("result").equals("true"))
								{
									// json에서 음식점 예약 정보를 가져온다.
									JSONObject jsonRestaurant = jsonRoot.getJSONArray("list").getJSONObject(0);

									// 화면 갱신
									View root = findViewById(R.id.best);
									root.setVisibility(View.VISIBLE);
									bestReservation = Reservation.fromJson(jsonRestaurant);
									bestReservation.map(root, MainActivity.this);
								}

							}
						}
						catch (JSONException e)
						{
						}
					}
				});

		// 최신 등록 정보 가져오기
		new HttpRequester("http://52.78.44.216:3000/users/recent").request(HttpRequester.Method.GET,
				null, 7000, new HttpRequester.HttpRequestListener()
				{
					@Override
					public void onHttpResult(String data, HttpRequester.Error error)
					{
						try
						{
							if (error == HttpRequester.Error.OK)
							{
								JSONObject jsonRoot = new JSONObject(data);
								if (jsonRoot.getString("result").equals("true"))
								{
									// json에서 음식점 예약 정보를 가져온다.
									JSONObject jsonRestaurant = jsonRoot.getJSONArray("list").getJSONObject(0);

									// 화면 갱신
									View root = findViewById(R.id.recent);
									root.setVisibility(View.VISIBLE);
									recentReservation = Reservation.fromJson(jsonRestaurant);
									recentReservation.map(root, MainActivity.this);
								}

							}
						}
						catch (JSONException e)
						{
						}
					}
				});
	}

	public void onClick(View view)
	{
		if (view == binding.btnUserInfo)
		{
			startActivity(new Intent(this, MyReservationActivity.class));
		}

		else if (view == binding.btnMapMode)
		{
			// GPS 권한이 있는지 확인한다.
			new TedPermission(this)
					.setPermissionListener(new PermissionListener()
					{
						@Override
						public void onPermissionGranted()
						{
							LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
							if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
							{
								Toast.makeText(MainActivity.this, "현재 위치제공자를 사용할 수 없습니다.\n설정에서 '위치'를 켜주세요.", Toast.LENGTH_SHORT).show();
								return;
							}

							startActivity(new Intent(MainActivity.this, MapActivity.class));
						}

						@Override
						public void onPermissionDenied(ArrayList<String> deniedPermissions)
						{
						}
					}).setRationaleMessage("현재 위치를 가져오려면 GPS 권한을 승인하셔야 합니다.")
					.setDeniedMessage("GPS 권한을 승인하지 않으면 현재 위치 정보를 가져올 수 없습니다.")
					.setPermissions(Manifest.permission.READ_CONTACTS)
					.check();
		}
	}
}