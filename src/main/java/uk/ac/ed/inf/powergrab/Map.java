package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.apache.commons.io.IOUtils;

public class Map {
    private PowerStation[] powerStations;
    private FeatureCollection mapFeatures;

    public Map(String day, String month, String year) {
        try {
            mapFeatures = loadMap(day, month, year);
        } catch (IOException e) {
            e.printStackTrace();
        }

        powerStations = loadPowerStations(this.mapFeatures);
    }

    public FeatureCollection loadMap(String day, String month, String year) throws IOException {
        String date = year + '/' + month + '/' + day;
        String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/"+date+"/powergrabmap.geojson";
        URL mapUrl = new URL(mapString);
        String mapJson = getJSON(mapUrl);

        return FeatureCollection.fromJson(mapJson);
    }

    public PowerStation[] loadPowerStations(FeatureCollection mapFeatures){
        int numberOfStations = mapFeatures.features().size();
        PowerStation[] powerStations = new PowerStation[numberOfStations];

        for(int i = 0 ; i < numberOfStations; i++){
            Feature mapFeature =  mapFeatures.features().get(i);

            double coins = mapFeature.getProperty("coins").getAsDouble();
            double power = mapFeature.getProperty("power").getAsDouble();

            double latitude = ((Point) mapFeature.geometry()).latitude();
            double longitude = ((Point) mapFeature.geometry()).longitude();
            Position position = new Position(latitude, longitude);

            PowerStation powerStation = new PowerStation(position, coins, power);
            powerStations[i] = powerStation;
        }

        return powerStations;
    }

    public PowerStation[] getPowerStationsInRange(Position dronePosition){
        ArrayList<PowerStation> powerStationsList = new ArrayList<PowerStation>();
        for(PowerStation ps : this.powerStations){
            if(ps.inRange(dronePosition)){
                powerStationsList.add(ps);
            }
        }

        return powerStationsList.toArray(new PowerStation[powerStationsList.size()]);
    }

    public void addPath(Position before, Position after){
        Point beforePoint = Point.fromLngLat(before.longitude, before.latitude);
        Point afterPoint = Point.fromLngLat(after.longitude, after.latitude);

        //Format coordinates to work with GeoJSON
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(beforePoint);
        points.add(afterPoint);

        //Create LineString feature from coordinates
        Feature lineStringFeature = Feature.fromGeometry(LineString.fromLngLats(points));

        //Add LineString feature to our map
        ArrayList<Feature> features = (ArrayList<Feature>) this.mapFeatures.features();
        features.add(lineStringFeature);
        this.mapFeatures = FeatureCollection.fromFeatures(features);
    }

    private String getJSON(URL mapUrl) throws IOException {
        HttpURLConnection mapConn = (HttpURLConnection) mapUrl.openConnection();
        mapConn.setReadTimeout(10000); // milliseconds
        mapConn.setConnectTimeout(15000); // milliseconds
        mapConn.setRequestMethod("GET");
        mapConn.setDoInput(true);
        mapConn.connect();
        InputStream mapStream = mapConn.getInputStream();
        String json = IOUtils.toString(mapStream, "UtF-8");
        //TODO Confirm this
        mapStream.close();
        mapConn.disconnect();
        return json;
    }
}
