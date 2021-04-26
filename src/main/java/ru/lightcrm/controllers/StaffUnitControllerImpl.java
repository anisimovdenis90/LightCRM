package ru.lightcrm.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.lightcrm.controllers.interfaces.StaffUnitController;
import ru.lightcrm.entities.dtos.StaffUnitDto;
import ru.lightcrm.services.interfaces.StaffUnitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StaffUnitControllerImpl implements StaffUnitController {

    private final StaffUnitService staffUnitService;

    @Override
    public List<String> getAllStaffUnitNames() {
        return staffUnitService.getNames();
    }

    @Override
    public StaffUnitDto saveStaffUnit(StaffUnitDto staffUnitDto) {
        return staffUnitService.saveOrUpdate(staffUnitDto);
    }

    @Override
    public StaffUnitDto updateStuffUnit(StaffUnitDto staffUnitDto) {
        return staffUnitService.saveOrUpdate(staffUnitDto);
    }


    @Override
    public void deleteStaffUnitById(Long id) {
        staffUnitService.deleteById(id);
    }
}
