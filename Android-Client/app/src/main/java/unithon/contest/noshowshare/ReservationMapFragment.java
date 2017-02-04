package unithon.contest.noshowshare;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import unithon.contest.noshowshare.databinding.FragmentReservationMapBinding;

/**
 * 예약 지도화면 프래그먼트
 */
public class ReservationMapFragment extends Fragment
{
	private FragmentReservationMapBinding binding;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation_map, container, false);
		return binding.getRoot();
	}
}