package unithon.contest.noshowshare;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.Reservation;
import unithon.contest.noshowshare.databinding.ActivityMainBinding;
import unithon.contest.noshowshare.databinding.ItemReservationInfoBinding;
import util.HttpRequester;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;
	private ArrayAdapter<Reservation> reservationAdapter;

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
				mapReservation(convertView, reservation);

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
							LinearLayout root = (LinearLayout) findViewById(R.id.best);
							root.setVisibility(View.VISIBLE);
							mapReservation(root, Reservation.fromJson(jsonRestaurant));
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
									LinearLayout root = (LinearLayout) findViewById(R.id.recent);
									root.setVisibility(View.VISIBLE);
									mapReservation(root, Reservation.fromJson(jsonRestaurant));
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
			startActivity(new Intent(this, MapActivity.class));
		}
	}

	/**
	 * Reservation 객체를 뷰에 연결하는 메서드
	 */
	private void mapReservation(View root, Reservation reservation)
	{
		ItemReservationInfoBinding itemBinding = DataBindingUtil.bind(root);
		itemBinding.txtRestaurantName.setText(reservation.getRestaurant().getName());
		itemBinding.txtRestaurantLocation.setText(reservation.getRestaurant().getLocationName());
		itemBinding.txtFoodName.setText(reservation.getFood().getName());
		itemBinding.txtPrice.setText(reservation.getFood().getPrice() + "원");
		itemBinding.txtDiscountedPrice.setText(reservation.getDiscountedPrice() + "원");
		if (reservation.getImgUrl() != null)
		{
			Picasso.with(this).load(reservation.getImgUrl()).resize(460,
					860).noFade().into(itemBinding.imgFood);
		}

		double discountRate = 1 - (double) reservation.getDiscountedPrice() / reservation.getFood().getPrice();
		discountRate *= 100;
		itemBinding.txtDiscountRate.setText((int) discountRate + "%");
		itemBinding.txtRemained.setText(reservation.getRemained() + "");
	}
}