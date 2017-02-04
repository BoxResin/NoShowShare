package unithon.contest.noshowshare;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import unithon.contest.noshowshare.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		
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
}