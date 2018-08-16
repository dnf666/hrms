package com.mis.hrm.web.work.controller;

import com.mis.hrm.project.util.ConstantValue;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.exception.InfoNotFullyExpection;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hrms")
public class WorkController {
    @Autowired
    private WorkService workService;

    @PostMapping("/work")
    public Map insertOneWorker(@RequestBody Whereabout whereabout) throws InfoNotFullyExpection {
        if (workService.insert(whereabout) > 0) {
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @DeleteMapping("/work")
    public Map deleteOneWorker(@RequestBody Whereabout whereabout) throws InfoNotFullyExpection {
        if(workService.deleteByPrimaryKey(whereabout) > 0){
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @PutMapping("/work")
    public Map updateOneWorker(@RequestBody Whereabout whereabout) throws InfoNotFullyExpection {
        if (workService.updateByPrimaryKey(whereabout) > 0) {
            return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,null);
        } else {
            return ToMap.toMap(ConstantValue.FALSE_CODE,ConstantValue.FALSE,null);
        }
    }

    @GetMapping("/work")
    public Map selectOneWorker(@RequestParam String companyId,
                               @RequestParam String num) throws InfoNotFullyExpection {
        Whereabout whereabout = new Whereabout(companyId,num);
        whereabout.setNum(num);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,workService.selectByPrimaryKey(whereabout));
    }

    @GetMapping("/work/count")
    public Map countWorkers(){
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,workService.countWorkers());
    }

    @GetMapping("/work/byGrade/{page}")
    public Map findByGrade(@RequestParam String grade,
                           @PathVariable Integer page,
                           Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,workService.findByGrade(pager,grade));
    }

    @GetMapping("/work/byName/{page}")
    public Map findByName(@RequestParam String name,
                          @PathVariable Integer page,
                          Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,workService.findByName(pager, name));
    }

    @GetMapping("/work/all/{page}")
    public Map getAllWorkers(@PathVariable Integer page,
                             Pager<Whereabout> pager){
        pager.setCurrentPage(page);
        return ToMap.toMap(ConstantValue.SUCCESS_CODE,ConstantValue.SUCCESS,workService.getAllGraduates(pager));
    }

}
