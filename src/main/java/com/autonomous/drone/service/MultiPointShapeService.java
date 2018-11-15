package com.autonomous.drone.service;

import com.autonomous.drone.customException.NotFoundException;
import com.autonomous.drone.persistance.mongoDb.domain.MultiPointShape;
import com.autonomous.drone.persistance.mongoDb.repository.MultiPointShapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.stereotype.Service;

@Service
public class MultiPointShapeService {
    @Autowired
    private MultiPointShapeRepository multiPointShapeRepository;

    public MultiPointShape saveMultiPointShape(MultiPointShape multiPointShape){
            return multiPointShapeRepository.save(multiPointShape);
    }

    public MultiPointShape getMultiPointShapeById(String id) throws NotFoundException {
        return getShapeById(id);
    }

    public MultiPointShape updateMultiPointShapeById(String id, GeoJsonMultiPoint newGeoJsonMultiPoint)
            throws NotFoundException {
        MultiPointShape multiPointShape = getShapeById(id);
        multiPointShape.setGeoJsonMultiPoint(newGeoJsonMultiPoint);

        return multiPointShapeRepository.save(multiPointShape);
    }

    public void deleteMultiPointShape(String id) throws NotFoundException{
        if (multiPointShapeRepository.existsById(id))
            multiPointShapeRepository.deleteById(id);
        else
            throw new NotFoundException();
    }


    private MultiPointShape getShapeById(String id) throws NotFoundException {
        if(multiPointShapeRepository.existsById(id))
            return multiPointShapeRepository.findById(id).get();
        else
            throw new NotFoundException();
    }
}
