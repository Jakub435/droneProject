package com.autonomous.drone.router

import com.autonomous.drone.DroneApplication
import com.autonomous.drone.persistance.mongoDb.domain.MultiPointShape
import com.autonomous.drone.persistance.mongoDb.repository.MultiPointShapeRepository
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import static groovyx.net.http.ContentType.*

@SpringBootTest(
        classes = DroneApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles(value = "test")
@ContextConfiguration
class MultiPointShapeControllerTest extends Specification{

    private RESTClient restClient = new RESTClient("http://localhost:8080/api/shape/", JSON)

    @Autowired
    MultiPointShapeRepository multiPointShapeRepository;

    private def record_id

    def setup() {
        setup: "add record to DB"
            restClient.handler.failure = restClient.handler.success

            MultiPointShape multiPointShape = new MultiPointShape()
            List<Point> points = new ArrayList<>()

            points.add(new Point(1,100))
            points.add(new Point(2,200))
            points.add(new Point(3,300))

            GeoJsonMultiPoint geoJsonWith2Points = new GeoJsonMultiPoint(points)
            multiPointShape.setGeoJsonMultiPoint(geoJsonWith2Points)

            def record = multiPointShapeRepository.save(multiPointShape)
            record_id = record.id
    }

    def cleanup() {
        multiPointShapeRepository.deleteById(record_id)
    }

    def "saveMultiPointShape() Should save MultiPoint"(){
        setup:
            def count = multiPointShapeRepository.count()

        when:"with correct keys and values"
            def response = restClient.post(
                    path:"testName/",
                    requestContentType: JSON,
                    body:[
                            type: 'MultiPoint',
                            coordinates:"[ [100.0, 0.0], [101.0, 1.0] ]"
                    ]
            )

        then: "Status is 200"
            assert response.status == 200

        and: "Body contain correct value"
            def coordinate = response.data
                    .geoJsonMultiPoint
                    .coordinates

            assert coordinate[0].x == 100.0
            assert coordinate[0].y == 0.0
            assert coordinate[1].x == 101.0
            assert coordinate[1].y == 1.0

        and: "Number of record in DB should increased by 1"
            assert multiPointShapeRepository.count() == count+1

        cleanup: "delete record"
            def id = response.data.id
            multiPointShapeRepository.deleteById(id)
    }


    def "saveMultiPointShape() Should not save incorrect MultiPoint"(){
        setup:
            def count = multiPointShapeRepository.count()

        when: "Post request with different coordinates"
            def response = restClient.post(
                    path:"secondTestName/",
                    requestContentType: JSON,
                    body:[
                            type: 'MultiPoint',
                            coordinates: coordinates
                    ]
            )

        then: "Status is #expectedStatus and message is #message"
            assert response.status == expectedStatus
            assert response.data.message == message

        and: "Number of records in DB should be the same"
            assert count == multiPointShapeRepository.count()

        where:
            expectedStatus | coordinates        | message
            400            | "[[100.0, 0.0]]"   | "Minumum of 2 Points required"
            400            | ""                 | "Minumum of 2 Points required"
            400            | "[100.0, 0.0]"     | "Coordinates must be an array of Points"
    }

    def "GetMultiPointShapeById with correct path Should return proper body"(){
        when: "GET request with correct id"
            def response = restClient.get(
                    path: record_id + "/",
                    requestContentType: JSON
            )
        then: "Status is 200"
            assert response.status == 200
        and: "Body contain correct value"
            def coordinates = response.data
                    .geoJsonMultiPoint
                    .coordinates

            assert response.data.id == record_id

            assert coordinates[0].x == 1
            assert coordinates[0].y == 100
            assert coordinates[1].x == 2
            assert coordinates[1].y == 200
            assert coordinates[2].x == 3
            assert coordinates[2].y == 300
    }

    def "GetMultiPointShapeById with incorrect path Should not return data"(){
        when: "GET request with correct id"
            def response = restClient.get(
                    path: "incorrectPath/",
                    requestContentType: JSON
            )

        then: "Status is 404"
            assert response.status == 404

        and: "Body contain correct value"
            assert response.data.message == "Record not found"
    }


    def "UpdateMultiPointShapeById with correct path and coordinates"() {
        when: "PUT request with correct data"
            def response = restClient.put(
                    path: record_id + "/",
                    requestContentType: JSON,
                    body: [
                            type: 'MultiPoint',
                            coordinates: "[ [111.0, 1.0], [222.0, 2.0] ]"
                    ]
            )

        then: "Status is 200"
            assert response.status == 200

        and: "Multipoint should be save in DB"
            def coordinates = multiPointShapeRepository
                    .findById(record_id)
                    .get()
                    .geoJsonMultiPoint
                    .coordinates

            assert coordinates[0].x == 111.0
            assert coordinates[0].y == 1.0
            assert coordinates[1].x == 222.0
            assert coordinates[1].y == 2.0
    }

    def "UpdateMultiPointShapeById with incorrect path and coordinates"() {
        when: "PUT request with incorrect data and path"
            def response = restClient.put(
                    path: path + record_id + "/",
                    requestContentType: JSON,
                    body: [
                            type: 'MultiPoint',
                            coordinates: Sendingcoordinates
                    ]
            )

        then: "Check status"
            assert response.status == expectedStatus

        and: "Check message"
            assert response.data.message == expectedMessage

        and: "Multipoint at DB should not change"
            def coordinates = multiPointShapeRepository
                    .findById(record_id)
                    .get()
                    .geoJsonMultiPoint
                    .coordinates

            assert coordinates[0].x == 1
            assert coordinates[0].y == 100
            assert coordinates[1].x == 2
            assert coordinates[1].y == 200
            assert coordinates[2].x == 3
            assert coordinates[2].y == 300

        where:
        path            | Sendingcoordinates                | expectedStatus    | expectedMessage
        ""              | "[ [111.0, 1.0] ]"                | 400               | "Minumum of 2 Points required"
        ""              | "[111.0, 1.0]"                    | 400               | "Coordinates must be an array of Points"
        "incorrectId"   | "[ [111.0, 1.0], [222.0, 2.0] ]"  | 404               | "Record not found"
        "incorrectId"   | "[ [111.0, 1.0] ]"                | 400               | "Minumum of 2 Points required"
        "incorrectId"   | "[111.0, 1.0]"                    | 400               | "Coordinates must be an array of Points"
    }


    def "DeleteMultiPointShape with correct id"() {
        when:
            def response = restClient.delete(
                    path: record_id + "/"
            )

        then: "status is 200"
            assert response.status == 200

        and: "Record should be deleted from DB"
            assert multiPointShapeRepository.existsById(record_id) == false
    }

    def "DeleteMultiPointShape with incorrect id"() {
        when:
            def response = restClient.delete(
                    path: "incorrectPath/"
            )

        then: "Status is 404 NOT FOUND"
            assert response.status == 404

        and: "message is 'Record not found' "
            assert response.data.message == "Record not found"
    }
}