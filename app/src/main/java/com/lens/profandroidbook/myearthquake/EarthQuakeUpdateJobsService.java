package com.lens.profandroidbook.myearthquake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.SimpleJobService;
import com.firebase.jobdispatcher.Trigger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EarthQuakeUpdateJobsService extends SimpleJobService {
    private static final String TAG = "EarthquakeUpdateJob";
    private static final String UPDATE_JOB_TAG = "update_job";
    private static final String PERIODIC_JOB_TAG = "periodic_job";

    private static final String NOTIFICATION_CHANNEL = "earthquake";
    public static final int NOTIFICATION_ID = 1;

    public static void scheduleUpdateJob(Context context) {
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        jobDispatcher.schedule(jobDispatcher.newJobBuilder()
                .setTag(UPDATE_JOB_TAG)
                .setService(EarthQuakeUpdateJobsService.class)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build());
    }

    @Override
    public int onRunJob(final JobParameters job) {

        ArrayList<Earthquake> earthquakes = new ArrayList<>(0);

        URL url;
        try {
            String quakeFeed = getApplication().getString(R.string.earthquake_feed);
            url = new URL(quakeFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(inputStream);
                Element element = document.getDocumentElement();

                NodeList nodeList = element.getElementsByTagName("entry");
                if (nodeList != null && nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entryElement = (Element) nodeList.item(i);
                        Element idElement = (Element) entryElement.getElementsByTagName("id").item(0);
                        Element titleElement = (Element) entryElement.getElementsByTagName("title").item(0);
                        Element gElement = (Element) entryElement.getElementsByTagName("georss:point").item(0);
                        Element whenElement = (Element) entryElement.getElementsByTagName("updated").item(0);
                        Element linkElement = (Element) entryElement.getElementsByTagName("link").item(0);

                        String idString = idElement.getFirstChild().getNodeValue();
                        String detailsString = titleElement.getFirstChild().getNodeValue();
                        String hostnameString = "http://earthquake.usgs.gov";
                        String linkString = hostnameString + linkElement.getAttribute("href");
                        String pointString = gElement.getFirstChild().getNodeValue();
                        String dtString = whenElement.getFirstChild().getNodeValue();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        Date date = new GregorianCalendar(0, 0, 0).getTime();
                        try {
                            date = simpleDateFormat.parse(dtString);
                        } catch (ParseException p) {
                            Log.e(TAG, "Date parsing exception.", p);
                        }

                        String[] location = pointString.split(" ");
                        Location location1 = new Location("dummyGPS");
                        location1.setLatitude(Double.parseDouble(location[0]));
                        location1.setLongitude(Double.parseDouble(location[1]));

                        String magnudeString = detailsString.split(" ")[1];
                        int end = magnudeString.length() - 1;
                        double magnitude = Double.parseDouble(magnudeString.substring(0, end));

                        if (detailsString.contains("-")) {
                            detailsString = detailsString.split("-")[1].trim();
                        } else {
                            detailsString = "";
                        }

                        final Earthquake earthquake = new Earthquake(idString, date, detailsString, location1, magnitude, linkString);
                        earthquakes.add(earthquake);
                    }
                }
            }
            httpURLConnection.disconnect();

            if(job.getTag().equals(PERIODIC_JOB_TAG)){
                Earthquake largestNewEarthquake = findLargestNewEarthquake(earthquakes);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                int minimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG,"3"));

                if(largestNewEarthquake != null || largestNewEarthquake.getMagnitude() >= minimumMagnitude){
                    broadcastNotification(largestNewEarthquake);
                }
            }
            //insert newly parsed array into the DB
            EarthquakeDatabaseAccessor
                    .getInstance(getApplication())
                    .earthquakeDao()
                    .insertEarthquakes(earthquakes);

            scheduleNextUpdate(this, job);

            return RESULT_SUCCESS;

        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL Exception", e);
            return RESULT_FAIL_NORETRY;
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            return RESULT_FAIL_RETRY;
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Parser ConfigurationException,e");
            return RESULT_FAIL_NORETRY;
        } catch (SAXException e) {
            Log.e(TAG, "SAXException", e);
            return RESULT_FAIL_NORETRY;
        }


    }

    private void scheduleNextUpdate(Context context, JobParameters job) {
        if (job.getTag().equals(UPDATE_JOB_TAG)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
            boolean autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

            if (autoUpdateChecked) {
                FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

                jobDispatcher.schedule(jobDispatcher.newJobBuilder()
                        .setTag(PERIODIC_JOB_TAG)
                        .setService(EarthQuakeUpdateJobsService.class)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .setReplaceCurrent(true)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(
                                updateFreq * 60 / 2,
                                updateFreq * 60))
                        .setLifetime(Lifetime.FOREVER)
                        .build());

            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.earthquake_channel_name);

            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void broadcastNotification(Earthquake earthquake) {
        createNotificationChannel();

        Intent startActivityIntent = new Intent(this
                , MainActivity.class);

        PendingIntent launchIntent = PendingIntent.getActivity(this, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder earthQuakeNotificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);

        earthQuakeNotificationBuilder
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(launchIntent)
                .setAutoCancel(true)
                .setShowWhen(true);

        earthQuakeNotificationBuilder
                .setWhen(earthquake.getDate().getTime())
                .setContentTitle("M:" + earthquake.getMagnitude())
                .setContentText(earthquake.getDetails())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(earthquake.getDetails()));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(NOTIFICATION_ID, earthQuakeNotificationBuilder.build());
    }

    private Earthquake findLargestNewEarthquake(List<Earthquake> newEarthquakes) {
        List<Earthquake> earthquakes =
                EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDao()
                        .loadAllEarthquakesBlocking();

        Earthquake largestNewEarthquake = null;

        for(Earthquake earthquake : newEarthquakes){
            if(earthquakes.contains(earthquake)){
                continue;
            }
            if(largestNewEarthquake == null || earthquake.getMagnitude() > largestNewEarthquake.getMagnitude()){
                largestNewEarthquake = earthquake;
            }
        }
        return largestNewEarthquake;
    }

}
