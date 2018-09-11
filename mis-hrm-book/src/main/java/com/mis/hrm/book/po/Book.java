package com.mis.hrm.book.po;

import com.mis.hrm.util.StringUtil;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book implements Serializable {
    private String companyId;
    private String bookId;
    private String bookName;
    private String category;
    private int quantity;
    private String version;

    public boolean baseRequied() {
        return StringUtil.notEmpty(companyId, bookName, category, version);
    }
}
