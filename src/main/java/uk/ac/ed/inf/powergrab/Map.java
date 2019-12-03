package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import org.apache.commons.io.IOUtils;

public class Map {
    private ChargingStation[] chargingStations;
    private FeatureCollection mapFeatures;
    private ArrayList<Point> droneFlightPath;

    public Map(String day, String month, String year) {
        try {
            mapFeatures = loadGeoJSONMap(day, month, year);
        } catch (IOException e) {
            e.printStackTrace();
        }

        chargingStations = loadPowerStations(this.mapFeatures);
        droneFlightPath = new ArrayList<Point>();
    }

    private FeatureCollection loadGeoJSONMap(String day, String month, String year) throws IOException {
        String date = year + '/' + month + '/' + day;
        String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/"+date+"/powergrabmap.geojson";
        URL mapUrl = new URL(mapString);
        String mapJson = getJSONFromURL(mapUrl);

        return FeatureCollection.fromJson(mapJson);
    }

    private ChargingStation[] loadPowerStations(FeatureCollection mapFeatures){
        int numberOfStations = mapFeatures.features().size();
        ChargingStation[] chargingStations = new ChargingStation[numberOfStations];

        for(int i = 0 ; i < numberOfStations; i++){
            Feature mapFeature =  mapFeatures.features().get(i);

            double coins = mapFeature.getProperty("coins").getAsDouble();
            double power = mapFeature.getProperty("power").getAsDouble();

            double latitude = ((Point) mapFeature.geometry()).latitude();
            double longitude = ((Point) mapFeature.geometry()).longitude();
            Position position = new Position(latitude, longitude);

            ChargingStation chargingStation = new ChargingStation(position, coins, power);
            chargingStations[i] = chargingStation;
        }

        return chargingStations;
    }

    public ChargingStation getInRangePowerStation(Position dronePosition){
        ChargingStation nearestStation = null;
        double nearestDistance = 100000000;

        for(int i = 0; i < this.chargingStations.length; i++){
            double distanceToPowerStation = this.chargingStations[i].getDistanceToPosition(dronePosition);
            boolean inRange = this.chargingStations[i].inRange(dronePosition);

            if((inRange && distanceToPowerStation < nearestDistance)){
                nearestStation = this.chargingStations[i];
                nearestDistance = distanceToPowerStation;
            }
        }

        return nearestStation;
    }

    public ChargingStation[] getChargingStations(){
        // Returns all power stations on the map
        return this.chargingStations;
    }

    public void addFlightPathPoint(Position dronePosition){
        // Format coordinates to work with GeoJSON
        Point nextPoint = Point.fromLngLat(dronePosition.longitude, dronePosition.latitude);

        // Add new point to the flight path
        this.droneFlightPath.add(nextPoint);
    }

    public void saveMapToFile(String droneType, String day, String month, String year) throws IOException {
        // Get map with drone flight path
        FeatureCollection map = getMapWithFlightPath();

        // Convert map to JSON
        String fileContent = map.toJson();

        // Filename in the form: dronetype-DD-MM-YYYY.geojson
        String filename = String.format("./%s-%s-%s-%s.geojson", droneType, day, month, year);

        // Write content to GeoJSON file
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(fileContent);
        fileWriter.close();
    }

    private FeatureCollection getMapWithFlightPath(){
        //Create LineString Feature with drone flight path points
        LineString flightPathLineString = LineString.fromLngLats(this.droneFlightPath);
        Feature flightPathFeature = Feature.fromGeometry(flightPathLineString);

        //Add flight path feature to the map features
        ArrayList<Feature> features = (ArrayList<Feature>) this.mapFeatures.features();
        features.add(flightPathFeature);

        //Create the map from all the features
        FeatureCollection mapWithFlightPath = FeatureCollection.fromFeatures(features);
        return mapWithFlightPath;
    }

    private String getJSONFromURL(URL mapUrl) throws IOException {
        HttpURLConnection mapConn = (HttpURLConnection) mapUrl.openConnection();
        mapConn.setReadTimeout(10000); // milliseconds
        mapConn.setConnectTimeout(15000); // milliseconds
        mapConn.setRequestMethod("GET");
        mapConn.setDoInput(true);
        mapConn.connect();
        InputStream mapStream = mapConn.getInputStream();
        String json = IOUtils.toString(mapStream, "UTF-8");
        mapStream.close();
        mapConn.disconnect();
        return json;
    }
}
