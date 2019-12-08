package com.lens.profandroidbook.myearthquake;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String TAG = "EarthquakeUpdate";

    //private MutableLiveData<List<Earthquake>> earthquakes;

    private LiveData<List<Earthquake>> earthquakes;

    public EarthquakeViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        if (earthquakes == null) {
            //earthquakes = new MutableLiveData<>();
            earthquakes =
                    EarthquakeDatabaseAccessor
                            .getInstance(getApplication())
                            .earthquakeDao()
                            .loadAllEarthQuakes();

            loadEarthquakes();
        }
        return earthquakes;
    }

    @SuppressLint("StaticFieldLeak")
    public void loadEarthquakes() {
        new AsyncTask<Void, Void, List<Earthquake>>() {
            @Override
            protected List<Earthquake> doInBackground(Void... voids) {
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
                                if (isCancelled()) {
                                    Log.d(TAG, "Loading Cancelled");
                                    return earthquakes;
                                }
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
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Malformed URL Exception", e);
                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                } catch (ParserConfigurationException e) {
                    Log.e(TAG, "Parser ConfigurationException,e");
                } catch (SAXException e) {
                    Log.e(TAG, "SAXException", e);
                }


                //insert newly parsed array into the DB
                EarthquakeDatabaseAccessor
                        .getInstance(getApplication())
                        .earthquakeDao()
                        .insertEarthquakes(earthquakes);

                return earthquakes;
            }

            @Override
            protected void onPostExecute(List<Earthquake> data) {
                //data will not be directly applied to LiveData field now but the Mutable Live Data will be replaced by a DB query
                //earthquakes.setValue(data);
            }
        }.execute();
    }
}
