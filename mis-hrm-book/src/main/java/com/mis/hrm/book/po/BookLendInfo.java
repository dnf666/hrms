package com.mis.hrm.book.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookLendInfo {
    private String companyId;
    private String bookId;
    private String bookName;
    private String lentTime;
    private String returnTime;
    private String borrower;
}
