package com.mis.hrm.web.work.controller;

import com.mis.hrm.util.Pager;
import com.mis.hrm.util.ToMap;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.exception.InfoNotFullyException;
import com.mis.hrm.work.model.Whereabout;
import com.mis.hrm.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/work")
public class WorkController {
    @Autowired
    private WorkService workService;

    @PostMapping("work")
    public Map insertOneWorker(@RequestBody Whereabout whereabout) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.insert(whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (Exception e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @PostMapping("delWork")
    public Map deleteByNums(@RequestBody List<String> nums , String companyId) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.deleteByNums(nums,companyId));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (Exception e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @PutMapping("work")
    public Map updateOneWorker(@RequestBody Whereabout whereabout) {
        Map<String,Object> map;
        try {
            map = ToMap.toSuccessMap(workService.updateByPrimaryKey(whereabout));
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        } catch (Exception e){
            map = ToMap.toFalseMap(e.getMessage());
        }
        return map;
    }

    @GetMapping("count")
    public Map countWorkers(Whereabout whereabout)
    {
        if (whereabout == null){
            return ToMap.toFalseMap(ErrorCode.NOT_BLANK.getDescription());
        }
        return ToMap.toSuccessMap(workService.countWorkers());
    }

    @GetMapping("/all")
    public Map getAllWorkers(@RequestParam Integer page,
                             @RequestParam Integer size,
                             @RequestParam String companyId){
        Pager<Whereabout> pager = new Pager<>();
        pager.setCurrentPage(page);
        pager.setPageSize(size);
        List<Whereabout> whereaboutList = workService.getAllGraduates(pager,companyId);
        pager.setData(whereaboutList);
        return ToMap.toSuccessMap(workService.getAllGraduates(pager,companyId));
    }

    @PostMapping("/filter")
    public Map workFilter(@RequestBody Whereabout whereabout,
                            @RequestParam Integer page,
                            @RequestParam Integer size){
        Pager<Whereabout> pager = new Pager<>();
        pager.setPageSize(size);
        pager.setCurrentPage(page);
        Map<String,Object> map;
        List<Whereabout> list = workService.filter(pager, whereabout);
        pager.setData(list);
        try {
            map = ToMap.toSuccessMap(pager);
        } catch (InfoNotFullyException infoNotFullyException){
            map = ToMap.toFalseMap(infoNotFullyException.getMessage());
        }
        return map;
    }
    @PostMapping("Excel")
    public Map importMemberFromExcel(MultipartFile file){
        try {
            workService.importWorkerFromExcel(file);
        }catch(IOException e){
            return null;
        }
        return null;
    }

}
