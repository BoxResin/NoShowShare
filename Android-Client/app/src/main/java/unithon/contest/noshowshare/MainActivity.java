package unithon.contest.noshowshare;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import data.Food;
import data.Reservation;
import data.Restaurant;
import unithon.contest.noshowshare.databinding.ActivityMainBinding;
import unithon.contest.noshowshare.databinding.ItemReservationInfoBinding;
import util.HttpRequester;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		// 지역 선택 스피너 초기화
		AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String firstArea_str = binding.spinFirstArea.getSelectedItem().toString();
				String secondArea_str = binding.spinSecondArea.getSelectedItem().toString();
				String thirdArea_str = binding.spinThirdArea.getSelectedItem().toString();

				if (!firstArea_str.equals("시/도") && !secondArea_str.equals("시/군/도") && !thirdArea_str.equals("동/읍/면"))
				{
					Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
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
							String storeName = jsonRestaurant.getString("store_name");
							String phone = jsonRestaurant.getString("phone");
							double lat = Double.parseDouble(jsonRestaurant.getString("lat"));
							double lng = Double.parseDouble(jsonRestaurant.getString("lng"));
							String foodName = jsonRestaurant.getString("food_name");
							int price = jsonRestaurant.getInt("price");
							int discountedPrice = jsonRestaurant.getInt("discount_price");
							int foodNum = jsonRestaurant.getInt("food_num");

							// 화면 갱신
							Reservation reservation = new Reservation(new Restaurant(storeName, phone, lat, lng, null),
									new Food(foodName, price), foodNum, discountedPrice);
							LinearLayout root = (LinearLayout) findViewById(R.id.best);
							root.setVisibility(View.VISIBLE);
							mapReservation(root, reservation);
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
									String storeName = jsonRestaurant.getString("store_name");
									String phone = jsonRestaurant.getString("phone");
									double lat = Double.parseDouble(jsonRestaurant.getString("lat"));
									double lng = Double.parseDouble(jsonRestaurant.getString("lng"));
									String foodName = jsonRestaurant.getString("food_name");
									int price = jsonRestaurant.getInt("price");
									int discountedPrice = jsonRestaurant.getInt("discount_price");
									int foodNum = jsonRestaurant.getInt("food_num");

									// 화면 갱신
									Reservation reservation = new Reservation(new Restaurant(storeName, phone, lat, lng, null),
											new Food(foodName, price), foodNum, discountedPrice);
									LinearLayout root = (LinearLayout) findViewById(R.id.recent);
									root.setVisibility(View.VISIBLE);
									mapReservation(root, reservation);
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

	private void mapReservation(View root, Reservation reservation)
	{
		ItemReservationInfoBinding itemBinding = DataBindingUtil.bind(root);
		itemBinding.txtRestaurantName.setText(reservation.getRestaurant().getName());
		itemBinding.txtRestaurantLocation.setText(reservation.getRestaurant().getLocationName());
		itemBinding.txtFoodName.setText(reservation.getFood().getName());
		itemBinding.txtPrice.setText(reservation.getFood().getPrice() + "원");
		itemBinding.txtDiscountedPrice.setText(reservation.getDiscountedPrice() + "원");

		double discountRate = 1 - (double) reservation.getDiscountedPrice() / reservation.getFood().getPrice();
		discountRate *= 100;
		itemBinding.txtDiscountRate.setText((int) discountRate + "%");
		itemBinding.txtRemained.setText(reservation.getRemained() + "");
	}
}