package com.marceme.hpifitness.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.notification.NotificationBroadcaster;
import com.marceme.hpifitness.util.PrefControlUtil;
import com.marceme.hpifitness.util.Helper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.messageLabel) TextView mMessage;
    @BindView(R.id.dailyDistanceData) TextView mTotalDist;
    @BindView(R.id.dailyTimeData) TextView mTotalTime;
    @BindView(R.id.dailyPaceData) TextView mCurrentPace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();
        String id = PrefControlUtil.getID(PrefControlUtil.USER_ID);
        User user = realm.where(User.class).equalTo("id",id).findFirst();
        if(user != null){;
            setDailyStat(user);
            showAchieveMilestone(user.getDistanceCovered());
        }
        scheduleNotification();
    }

    private void setDailyStat(User user) {
        String message = String.format(getString(R.string.message_label), user.getFirstName());
        String dailyDist = String.format(getString(R.string.daily_dist_data), Helper.meterToMileConverter(user.getDistanceCovered()));
        String dailyTime = String.format(getString(R.string.daily_time_data), Helper.secondToMinuteConverter(user.getTotalTimeWalk()));
        String dailyPace = String.format(getString(R.string.daily_pace_data), user.getPace());

        mMessage.setText(message);
        mTotalDist.setText(dailyDist);
        mTotalTime.setText(dailyTime);
        mCurrentPace.setText(dailyPace);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            PrefControlUtil.setID(PrefControlUtil.USER_ID, null);
            goToDispatchScreen();
            return true;
        }else if (id == R.id.action_cancel_notification){
            cancelNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.walkBtn)
    public void goToWalkEvent(Button button) {
        Intent intent = new Intent(this, WalkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showAchieveMilestone(float distanceCovered) {
        int numberOfMilestones = Helper.getNumberOfMilestones(distanceCovered);
        if(numberOfMilestones > 0){
            String title = getString(R.string.achievement_title);
            String message = String.format(getString(R.string.achievement_message), numberOfMilestones);
            Helper.displayMessageToUser(this,title,message).show();
        }
    }

    private void goToDispatchScreen() {
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Assume user controls the periodic reminder: no reminder at office if user turn off notification
    private void scheduleNotification() {

        Notification notification = createNotification(getString(R.string.app_name), getString(R.string.notification_message));
        Intent notificationIntent = getIntent(notification);
        PendingIntent pendingIntent = getBroadcast(notificationIntent);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 20);

        AlarmManager alarmManager = getSystemService();

        // Reminder every 1 hour
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    private AlarmManager getSystemService() {
        return (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent getBroadcast(Intent notificationIntent) {
        return PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    private Intent getIntent(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_KEY, notification);
        return notificationIntent;
    }

    private Notification createNotification(String title, String message) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent resultIntent = new Intent(this, DispatchActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }
    private void cancelNotification(){
        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = getBroadcast(notificationIntent);
        AlarmManager alarmManager = getSystemService();
        alarmManager.cancel(pendingIntent);
    }
}
