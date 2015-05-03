package seanliu93.compass_android;

/**
 * Created by seanliu93 on 4/1/2015.
 */


import com.parse.ui.ParseLoginDispatchActivity;



public class DispatchLogin extends ParseLoginDispatchActivity {

    @Override
    protected Class<?> getTargetClass() {
        return MapsActivity.class;
    }
}