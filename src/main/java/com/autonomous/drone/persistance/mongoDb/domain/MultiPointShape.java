package com.autonomous.drone.persistance.mongoDb.domain;

import com.autonomous.drone.customException.CoordinateArrayException;
import com.autonomous.drone.customException.Minimum2PointsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "shape")
public class MultiPointShape {
    @BsonId
    private String id;
    private GeoJsonMultiPoint geoJsonMultiPoint;

    @JsonCreator
    public MultiPointShape(
            @JsonProperty(value = "coordinates", required = true) JsonNode node) throws Minimum2PointsException {
        List<Point> pointList = new ArrayList<>();

        try {
            for (JsonNode coordinate:node) {
                Point point = new Point(
                        coordinate.get(0).doubleValue(),
                        coordinate.get(1).doubleValue());

                pointList.add(point);
            }
            geoJsonMultiPoint = new GeoJsonMultiPoint(pointList);
        }catch (IllegalArgumentException exception){
            exception.printStackTrace();
            throw new Minimum2PointsException();
        }catch (NullPointerException ex){
            ex.printStackTrace();
            throw new CoordinateArrayException();
        }

    }

    public MultiPointShape(GeoJsonMultiPoint geoJsonMultiPoint) {
        this.geoJsonMultiPoint = geoJsonMultiPoint;
    }

    public MultiPointShape() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoJsonMultiPoint getGeoJsonMultiPoint() {
        return geoJsonMultiPoint;
    }

    public void setGeoJsonMultiPoint(GeoJsonMultiPoint geoJsonMultiPoint) {
        this.geoJsonMultiPoint = geoJsonMultiPoint;
    }
}
