package com.mis.hrm.book.po;

import com.mis.hrm.util.StringUtil;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookLendInfo implements Serializable {
    private String companyId;
    private String bookRecord;
    private String bookName;
    private String lendTime;
    private String returnTime;
    private String borrower;

    public boolean baseRequired(){
        return StringUtil.notEmpty(companyId,bookName,lendTime,borrower);
    }
}
