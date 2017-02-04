package data;

import java.util.ArrayList;

/**
 * 예약 정보 클래스
 */
public class Reservation
{
	private String date; // 예약이 게시된 날짜
	private Restaurant restaurant; // 음식점 정보
	private ArrayList<User> users = new ArrayList<>(); // 이 예약에 참여하는 사용자 목록
	private Food food;
	private int remained; // 남은 수량

	public int getRemained()
	{
		return remained;
	}

	public void setRemained(int remained)
	{
		this.remained = remained;
	}

	public Reservation(Restaurant restaurant, Food food)
	{
		this.restaurant = restaurant;
		this.food = food;
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
}