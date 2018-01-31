package pl.llp.aircasting.sync;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import com.google.inject.Inject;
import pl.llp.aircasting.R;
import pl.llp.aircasting.android.Logger;
import pl.llp.aircasting.api.FixedSessionDriver;
import pl.llp.aircasting.model.Session;
import pl.llp.aircasting.model.ViewingSessionsManager;
import roboguice.service.RoboIntentService;

import java.util.Collection;

/**
 * Created by radek on 23/01/18.
 */
public class StreamingSessionsSyncService extends RoboIntentService {
    @Inject ConnectivityManager connectivityManager;
    @Inject ViewingSessionsManager viewingSessionsManager;
    @Inject FixedSessionDriver driver;

    public StreamingSessionsSyncService() {
        super(StreamingSessionsSyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (canDownload()) {
                 sync();
            }
        } catch (SessionSyncException exception) {
            Toast.makeText(getBaseContext(), R.string.measurement_sync_failed, Toast.LENGTH_LONG);
        }
    }

    private void sync() {
        Collection<Session> sessions = viewingSessionsManager.getFixedSessions();

        for (Session session : sessions) {
            Logger.w("syncing session " + session.getId());
            driver.downloadNewData(session);
        }
   }

    private boolean canDownload() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}