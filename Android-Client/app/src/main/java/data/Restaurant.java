package data;

/**
 * 식당 정보 클래스
 */
public class Restaurant
{
	private String name; // 식당 이름
	private String phone; // 식당 전화번호
	private String locationName; // 식당 위치
	private double lat; // 식당 위도
	private double lng; // 식당 경도
	private String imgUrl; // 대표 이미지 url
	private String date; // 가게 정보 등록일

	public Restaurant()
	{
	}

	/**
	 * 생성자
	 * @param name 식당 이름
	 * @param phone 식당 전화번호
	 * @param lat 식당 위도
	 * @param lng 식당 경도
	 * @param imgUrl 대표 이미지 url
	 */
	public Restaurant(String name, String phone, String locationName, double lat, double lng, String imgUrl)
	{
		this.name = name;
		this.phone = phone;
		this.locationName = locationName;
		this.lat = lat;
		this.lng = lng;
		this.imgUrl = imgUrl;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public double getLat()
	{
		return lat;
	}

	public void setLat(double lat)
	{
		this.lat = lat;
	}

	public double getLng()
	{
		return lng;
	}

	public void setLng(double lng)
	{
		this.lng = lng;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}
}