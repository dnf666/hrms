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

    @GetMapping("/count")
    public Map countWorkers(){
        return ToMap.toSuccessMap(workService.countWorkers());
    }

    @GetMapping("/all")
    public Map getAllWorkers(@RequestParam Integer page,
                             @RequestParam Integer size,
                             Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        pager.setPageSize(size);
        return ToMap.toSuccessMap(workService.getAllGraduates(pager));
    }

    @PostMapping("/filter")
    public Map workFilter(@RequestBody Whereabout whereabout,
                            @RequestParam Integer page,
                            @RequestParam Integer size,
                            Pager<Whereabout> pager){
        pager.setPageSize(size);
        pager.setCurrentPage(page);
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.filter(pager, whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }

}
