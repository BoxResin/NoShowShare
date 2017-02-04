package unithon.contest.noshowshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import util.G;

/**
 * Created by BoxResin on 2017-02-05.
 */

public class SplashActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		long now = System.currentTimeMillis();
		SharedPreferences pref = getSharedPreferences("past", Context.MODE_PRIVATE);

		// 마지막 예약으로부터 2시간 이상 경과
		long past = pref.getLong("key", 0);
		if (now - past >= G.VALID_RESERVATION_TIME_MS)
		{
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("reservation", "");
			editor.commit();
		}

		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				finish();
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
			}
		}, 1500);
	}
}