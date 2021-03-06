package seanliu93.compass_android;

/**
 * Created by seanliu93 on 4/1/2015.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

public class Starter extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Required - Initialize the Parse SDK
        Parse.initialize(this, getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

    }
}