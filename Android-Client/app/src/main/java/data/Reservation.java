package data;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import unithon.contest.noshowshare.RestaurantDetailActivity;
import unithon.contest.noshowshare.databinding.ItemReservationInfoBinding;

/**
 * 예약 정보 클래스
 */
public class Reservation implements Serializable
{
	private String date; // 예약이 게시된 날짜
	private Restaurant restaurant; // 음식점 정보
	private ArrayList<User> users = new ArrayList<>(); // 이 예약에 참여하는 사용자 목록
	private Food food;
	private int remained; // 남은 수량
	private int discountedPrice; // 할인된 가격
	private String imgUrl; // 대표 이미지 url

	/**
	 * JSON 오브젝트를 Reservation 객체로 파싱하는 메서드
	 */
	public static Reservation fromJson(JSONObject json) throws JSONException
	{
		String storeName = json.getString("store_name");
		String phone = json.getString("phone");
		double lat = Double.parseDouble(json.getString("lat"));
		double lng = Double.parseDouble(json.getString("lng"));
		String foodName = json.getString("food_name");
		int price = json.getInt("price");
		int discountedPrice = json.getInt("discount_price");
		int foodNum = json.getInt("food_num");

//		String storeLocation = String.format("%s %s %s",
//				json.getString("city"), json.getString("goo"),
//				json.getString("dong"));
		String storeLocation = "";

		String foodImgUrl = json.getString("img");

		return new Reservation(new Restaurant(storeName, phone, storeLocation, lat, lng, null),
				new Food(foodName, price), foodNum, discountedPrice, foodImgUrl);
	}

	/**
	 * Reservation 객체를 뷰에 연결하는 메서드
	 */
	public void map(View root, final Context context)
	{
		ItemReservationInfoBinding itemBinding = DataBindingUtil.bind(root);
		itemBinding.txtRestaurantName.setText(getRestaurant().getName());
		itemBinding.txtRestaurantLocation.setText(getRestaurant().getLocationName());
		itemBinding.txtFoodName.setText(getFood().getName());
		itemBinding.txtRemained.setText(getRemained() + "");

		// 클릭 리스너 등록
		itemBinding.reservationCard.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, RestaurantDetailActivity.class);
				intent.putExtra("reservation", Reservation.this);
				context.startActivity(intent);
			}
		});

		// 원가에 취소선 긋기
		SpannableString txtPriceSpan = new SpannableString(getFood().getPrice() + "원");
		txtPriceSpan.setSpan(new StrikethroughSpan(), 0, txtPriceSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		itemBinding.txtPrice.setText(txtPriceSpan);
		itemBinding.txtDiscountedPrice.setText(getDiscountedPrice() + "원");

		// 이미지 url 불러오기
		if (getImgUrl() != null)
		{
			Picasso.with(context).load(getImgUrl()).into(itemBinding.imgFood);
		}

		// 할인율 계산
		double discountRate = 1 - (double) getDiscountedPrice() / getFood().getPrice();
		discountRate *= 100;
		itemBinding.txtDiscountRate.setText((int) discountRate + "%");
	}

	public int getRemained()
	{
		return remained;
	}

	public void setRemained(int remained)
	{
		this.remained = remained;
	}

	public Reservation(Restaurant restaurant, Food food, int remained, int discountedPrice, String imgUrl)
	{
		this.restaurant = restaurant;
		this.food = food;
		this.remained = remained;
		this.discountedPrice = discountedPrice;
		this.imgUrl = imgUrl;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public Restaurant getRestaurant()
	{
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant)
	{
		this.restaurant = restaurant;
	}

	public ArrayList<User> getUsers()
	{
		return users;
	}

	public void setUsers(ArrayList<User> users)
	{
		this.users = users;
	}

	public Food getFood()
	{
		return food;
	}

	public void setFood(Food food)
	{
		this.food = food;
	}

	public int getDiscountedPrice()
	{
		return discountedPrice;
	}

	public void setDiscountedPrice(int discountedPrice)
	{
		this.discountedPrice = discountedPrice;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}
}