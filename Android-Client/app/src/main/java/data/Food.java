package data;

import java.io.Serializable;

/**
 * 음식 정보 클래스
 */
public class Food implements Serializable
{
	private String name; // 음식 이름
	private int price; // 음식 가격

	public Food(String name, int price)
	{
		this.name = name;
		this.price = price;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getPrice()
	{
		return price;
	}

	public void setPrice(int price)
	{
		this.price = price;
	}
}