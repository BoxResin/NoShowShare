package util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/** Http 프로토콜로 서버에 데이터를 요청하는 유틸리티 클래스 */
public class HttpRequester
{
	/** 데이터를 수신하는 도중 발생할 수 있는 오류 */
	public enum Error
	{
		OK,           // 오류없음
		TIMEOUT,      // 시간초과
		NETWORK,      // 네트워크 문제 (주로 인터넷에 연결되지 않았을 때 발생)
		INCORRECT_URL // 잘못된 서버 주소
	}

	/** 데이터 요청방법 */
	public enum Method
	{
		GET, // Get 방식
		POST // Post 방식
	}

	/** 요청했던 데이터를 받기위한 리스너 */
	public interface HttpRequestListener
	{
		/**
		 * 데이터를 받았을 때 호출되는 메서드
		 * @param data  : 전달된 데이터
		 * @param error : 데이터 수신 도중 발생한 오류
		 */
		void onHttpResult(String data, Error error);
	}

	private HttpClient client = new DefaultHttpClient();
	private String address;          // 연결된 서버주소
	private RequestTask currentTask; // 현재 진행중인 데이터 요청작업

	/** 빈 주소를 가지는 생성자 */
	public HttpRequester()
	{
		this("");
	}

	/**
	 * 데이터를 요청할 서버의 주소를 전달받는 생성자
	 * @param address : 서버의 주소
	 */
	public HttpRequester(String address)
	{
		setAddress(address);
	}

	/**
	 * 서버에 데이터를 요청하는 메서드
	 * @param method   : 요청방법
	 * @param params   : 서버에 전달할 매개변수
	 * @param listener : 요청한 데이터를 받을 리스너
	 */
	public void request(Method method, @Nullable List<NameValuePair> params, HttpRequestListener listener)
	{
		request(method, params, 0, listener);
	}

	/**
	 * 서버에 데이터를 요청하는 메서드
	 * @param method   : 요청방법
	 * @param params   : 서버에 전달할 매개변수
	 * @param timeout  : 제한시간
	 * @param listener : 요청한 데이터를 받을 리스너
	 */
	public void request(Method method, @Nullable List<NameValuePair> params, int timeout, HttpRequestListener listener)
	{
		// 현재 진행중인 요청작업이 완료되지 않았으면 취소한다.
		if (currentTask != null && currentTask.getStatus() != AsyncTask.Status.FINISHED)
			currentTask.cancel(true);

		// 요청작업을 수행한다.
		currentTask = new RequestTask(method, params, timeout, listener);
		currentTask.execute();
	}

	/** 백그라운드에서 요청작업을 수행하는 클래스 */
	private class RequestTask extends AsyncTask<Void, Void, String>
	{
		private Method method;
		private List<NameValuePair> params;
		private int timeout;
		private HttpRequestListener listener;

		private Error error = Error.OK; // 작업 도중 발생한 오류

		/** HttpRequester.request의 매개변수를 전달받기 위한 생성자 */
		private RequestTask(Method method, List<NameValuePair> params, int timeout, HttpRequestListener listener)
		{
			this.method = method;
			this.params = params;
			this.timeout = timeout;
			this.listener = listener;
		}

		/**
		 * 백그라운드 스레드에서 실행되는 메서드
		 * @return : 서버에 요청한 데이터
		 */
		@Override
		protected String doInBackground(Void... v)
		{
			try
			{
				// 제한시간을 설정한다.
				HttpParams httpParams = client.getParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);

				// 통신방법에 따라 처리를 다르게 한다.
				HttpUriRequest request = null;
				switch (method)
				{
				case GET: // Get 방식
					// 통신할 서버의 주소와 서버에 전달할 매개변수를 합친다.
					String uri = address + "?";
					if (params != null)
						for (NameValuePair i : params)
							uri += i.toString() + "&";

					uri = uri.substring(0, uri.length() - 1);
					request = new HttpGet(uri);
					break;

				case POST: // Post 방식
					// 서버에 전달할 매개변수를 집어넣는다.
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					HttpPost postRequest = new HttpPost(address);
					postRequest.setEntity(entity);
					request = postRequest;
					break;
				}

				// 요청을 실행한다.
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();

				if (entity != null)
					return EntityUtils.toString(entity);
				else
					return null;
			}

			// 네트워크 연결시간초과 예외
			catch (ConnectTimeoutException e)
			{
				error = Error.TIMEOUT;
			}

			// 잘못된 URL 주소 예외
			catch (IllegalArgumentException e)
			{
				error = Error.INCORRECT_URL;
			}
			catch (IllegalStateException e)
			{
				error = Error.INCORRECT_URL;
			}

			// 기타 네트워크 예외
			catch (IOException e)
			{
				error = Error.NETWORK;
			}

			return null;
		}

		/**
		 * UI 스레드에서 실행되는 메서드
		 * @param result : doInBackground가 반환한 결과 값
		 */
		@Override
		protected void onPostExecute(String result)
		{
			// 작업이 취소되지 않았을때만 리스너에 결과데이터와 작업도중 발생했던 오류코드를 전달한다.
			if (!isCancelled())
				listener.onHttpResult(result, error);
		}
	}

	/**
	 * 현재 연결된 서버의 주소를 가져오는 메서드
	 * @return : 현재 연결된 서버의 주소
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * 데이터를 요청할 서버의 주소를 설정하는 메서드
	 * @param address : 데이터를 요청할 서버의 주소
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
}