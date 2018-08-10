package com.mis.hrm.book.po;

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
}
