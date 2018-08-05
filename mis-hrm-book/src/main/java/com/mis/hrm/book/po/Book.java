package com.mis.hrm.book.po;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    private String companyId;
    private String bookId;
    private String bookName;
    private String category;
    private int quantity;
    private String version;
}
