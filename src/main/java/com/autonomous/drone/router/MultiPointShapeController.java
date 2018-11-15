package com.autonomous.drone.router;

import com.autonomous.drone.customException.NotFoundException;
import com.autonomous.drone.persistance.mongoDb.domain.MultiPointShape;
import com.autonomous.drone.service.MultiPointShapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/api/shape")
public class MultiPointShapeController {
    @Autowired
    private MultiPointShapeService multiPointShapeService;

    @PostMapping(path = "/{shapeName}", consumes = "application/json", produces = "application/json")
    public @ResponseBody MultiPointShape saveMultiPointShape(
            @PathVariable String shapeName,
            @RequestBody MultiPointShape multiPointShape){
        return multiPointShapeService.saveMultiPointShape(multiPointShape);
    }

    @GetMapping(path = "/{shapeId}", produces = "application/json")
    public @ResponseBody MultiPointShape getMultiPointShapeById(
            @PathVariable String shapeId) throws NotFoundException {

        return multiPointShapeService.getMultiPointShapeById(shapeId);
    }

    @PutMapping(path = "/{shapeId}", consumes = "application/json", produces = "application/json")
    public @ResponseBody MultiPointShape updateMultiPointShapeById(
            @PathVariable String shapeId,
            @RequestBody MultiPointShape multiPointShape) throws NotFoundException {

        return multiPointShapeService
                .updateMultiPointShapeById(shapeId, multiPointShape.getGeoJsonMultiPoint());
    }

    @DeleteMapping(path = "/{shapeId}")
    public @ResponseBody ResponseEntity<String> deleteMultiPointShape(
            @PathVariable String shapeId) throws NotFoundException{
        multiPointShapeService.deleteMultiPointShape(shapeId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
