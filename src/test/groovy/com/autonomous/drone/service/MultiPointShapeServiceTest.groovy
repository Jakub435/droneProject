package com.autonomous.drone.service

import com.autonomous.drone.customException.NotFoundException
import com.autonomous.drone.persistance.mongoDb.domain.MultiPointShape
import com.autonomous.drone.persistance.mongoDb.repository.MultiPointShapeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint
import spock.lang.Specification

@SpringBootTest
class MultiPointShapeServiceTest extends Specification{

    @Autowired
    MultiPointShapeService multiPointShapeService

    @Autowired
    MultiPointShapeRepository multiPointShapeRepository

    private def document_id
    private GeoJsonMultiPoint geoJsonWith2Points

    def setup() {
        setup: "add record to DB"
            MultiPointShape multiPointShape = new MultiPointShape()
            List<Point> points = new ArrayList<>()

            points.add(new Point(1,100))
            points.add(new Point(2,200))

            geoJsonWith2Points = new GeoJsonMultiPoint(points)
            multiPointShape.setGeoJsonMultiPoint(geoJsonWith2Points)

            def record = multiPointShapeRepository.save(multiPointShape)
            document_id = record.id
    }

    def cleanup() {
        multiPointShapeRepository.deleteById(document_id)
    }

    def"saveMultiPointShape() Should save MultiPointShape in DB"(){
        setup:
            def count = multiPointShapeRepository.count()
            MultiPointShape multiPointShape = new MultiPointShape()

        when:"saving at least two points"
            multiPointShape.setGeoJsonMultiPoint(geoJsonWith2Points)

            def response = multiPointShapeService.saveMultiPointShape(multiPointShape)

        then:"Number of record in DB should increased by 1"
            assert multiPointShapeRepository.count() == count+1

        cleanup:"delete shape from DB"
            def id = response.id
            multiPointShapeRepository.deleteById(id)
    }

    def"saveMultiPointShape() Should not save incorrect MultiPointShape in DB"(){
        setup:
            def count = multiPointShapeRepository.count()
            MultiPointShape multiPointShape = new MultiPointShape()
            List<Point> points = new ArrayList<>()

        when:"trying save one point"
            points.add(new Point(100,1))

            GeoJsonMultiPoint geoJsonMultiPoint = new GeoJsonMultiPoint(points)
            multiPointShape.setGeoJsonMultiPoint(geoJsonMultiPoint)

            multiPointShapeService.saveMultiPointShape(multiPointShape)

        then:"Should thrown IllegalArgumentException"
            thrown(IllegalArgumentException.class)

        and:"Number of record in DB should be the same"
            assert multiPointShapeRepository.count() == count
    }

    def "GetMultiPointShapeById with correct id should return data"() {
        when: "Should get data"
            def coordinates = multiPointShapeService
                    .getMultiPointShapeById(document_id)
                    .geoJsonMultiPoint
                    .coordinates
        then: "Should return correct coordinates"
            assert coordinates[0].x == 1
            assert coordinates[0].y == 100
            assert coordinates[1].x == 2
            assert coordinates[1].y == 200
    }

    def "GetMultiPointShapeById with incorrect id should thrown exception"() {
        when:
            multiPointShapeService.getMultiPointShapeById("incorrectId")

        then: "Should thrown NotFoundException"
            thrown(NotFoundException)
    }

    def "UpdateMultiPointShapeById with correct Id should update data"() {
        setup: "change points coordinate"
        List<Point> points = new ArrayList<>()
        points.add(new Point(3,300))
        points.add(new Point(4,400))
        GeoJsonMultiPoint geoJson = new GeoJsonMultiPoint(points)

        when: "update document with new points"
            multiPointShapeService.updateMultiPointShapeById(document_id, geoJson)

        then: "document should have new points"
            def document = multiPointShapeRepository.findById(document_id).get()
            def savePoint = document.geoJsonMultiPoint.coordinates

            assert savePoint[0].y == 300
            assert savePoint[0].x == 3
            assert savePoint[1].y == 400
            assert savePoint[1].x == 4
    }

    def "UpdateMultiPointShapeById with incorrect Id should thrown exception"() {
        setup: "change points coordinate"
            List<Point> points = new ArrayList<>()
            points.add(new Point(3,300))
            points.add(new Point(4,400))
            GeoJsonMultiPoint geoJson = new GeoJsonMultiPoint(points)

            MultiPointShape multiPointShape = new MultiPointShape(geoJsonWith2Points)

        when: "trying update not existing document"
            multiPointShapeService.updateMultiPointShapeById("incorrectId", geoJson)

        then: "Should thrown NotFoundException"
            thrown(NotFoundException)

        and: "document should have old values"
            def document = multiPointShapeRepository.findById(document_id).get()
            def firstPoint = document.geoJsonMultiPoint.coordinates[0]

            assert firstPoint.x == 1
            assert  firstPoint.y == 100
    }

    def "DeleteMultiPointShape with correct Id should delete document"() {
        when: "Delete existing document"
            multiPointShapeService.deleteMultiPointShape(document_id)

        then: "Should be delete from DB"
            assert multiPointShapeRepository.existsById(document_id) == false
    }

    def "DeleteMultiPointShape with incorrect Id should thrown exception"() {
        when: "Delete not existing document"
            multiPointShapeService.deleteMultiPointShape("incorrectId")

        then: "Shpuld thrown NotFoundException"
            thrown(NotFoundException)
    }

}