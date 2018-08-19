package com.mis.hrm.web.work.controller;

import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/work")
public class WorkController {
    @Autowired
    private WorkService workService;

    @PostMapping
    public Map insertOneWorker(@RequestBody Whereabout whereabout) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.insert(whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @DeleteMapping
    public Map deleteOneWorker(@RequestBody Whereabout whereabout) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.deleteByPrimaryKey(whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @PutMapping
    public Map updateOneWorker(@RequestBody Whereabout whereabout) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.updateByPrimaryKey(whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (RuntimeException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping
    public Map selectOneWorker(@RequestParam String companyId,
                               @RequestParam String num) {
        Whereabout whereabout = new Whereabout(companyId,num);
        whereabout.setNum(num);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.selectByPrimaryKey(whereabout));
        } catch (NullPointerException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("/count")
    public Map countWorkers(){
        return ToMap.toSuccessMap(workService.countWorkers());
    }

    @GetMapping("/byGrade/{page}")
    public Map findByGrade(@RequestParam String grade,
                           @PathVariable Integer page,
                           Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.findByGrade(pager, grade));
        } catch (InfoNotFullyException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("/byName/{page}")
    public Map findByName(@RequestParam String name,
                          @PathVariable Integer page,
                          Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.findByName(pager, name));
        } catch (InfoNotFullyException e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("/all/{page}")
    public Map getAllWorkers(@PathVariable Integer page,
                             Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        return ToMap.toSuccessMap(workService.getAllGraduates(pager));
    }

}
