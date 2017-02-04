package unithon.contest.noshowshare;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import data.Food;
import data.Reservation;
import data.Restaurant;
import unithon.contest.noshowshare.databinding.FragmentReservationListBinding;
import unithon.contest.noshowshare.databinding.ItemReservationInfoBinding;

/**
 * 예약 리스트 프래그먼트
 */
public class ReservationListFragment extends Fragment
{
	private FragmentReservationListBinding binding;

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation_list, container, true);
		ArrayAdapter<Reservation> reservationAdapter = new ArrayAdapter<Reservation>(getContext(), R.layout.item_reservation_info)
		{
			@NonNull
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				ItemReservationInfoBinding itemBinding;
				if (convertView == null)
				{
					itemBinding = DataBindingUtil.inflate(inflater, R.layout.item_reservation_info, parent, false);
					convertView = itemBinding.getRoot();
				}
				else
					itemBinding = DataBindingUtil.getBinding(convertView);

				Reservation reservation = getItem(position);
				itemBinding.txtRestaurantName.setText(reservation.getRestaurant().getName());
				itemBinding.txtFoodName.setText(reservation.getFood().getName());
				itemBinding.txtPrice.setText(reservation.getFood().getPrice() + "");
				itemBinding.txtRemained.setText(reservation.getRemained() + "");
				return convertView;
			}
		};
		binding.listRestaurant.setAdapter(reservationAdapter);

		reservationAdapter.add(new Reservation(new Restaurant("한솥도시락", "010-1234-5678", 35.0, 40.6, "none"), new Food("어떤 도시락", 3000)));
		reservationAdapter.add(new Reservation(new Restaurant("ㅇㅇ뷔페", "010-4321-8765", 36, 40.3, "none"), new Food("도시락", 1200)));

		return binding.getRoot();
	}
}