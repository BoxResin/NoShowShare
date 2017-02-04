package unithon.contest.noshowshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by minhyeon on 2017-02-04.
 */

public class RestaurantDetail extends AppCompatActivity {

    private TextView foodName;
    private TextView foodNum;
    private ImageView restaurantImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        foodName = (TextView) findViewById(R.id.food_name);
        foodNum = (TextView) findViewById(R.id.food_num);
        restaurantImage = (ImageView) findViewById(R.id.restaurant_image);

        foodName.setText("");
        foodNum.setText("");


    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
        }
    }
}
