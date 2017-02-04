package unithon.contest.noshowshare;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import unithon.contest.noshowshare.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
	}

	public void onClick(View view)
	{
		if (view == binding.btnUserInfo)
		{

		}

		else if (view == binding.btnMapMode)
		{
			startActivity(new Intent(this, MapActivity.class));
		}
	}
}