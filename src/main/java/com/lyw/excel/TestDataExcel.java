package com.lyw.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author LiuYaoWen
 * @date 2022/9/7 22:39
 */
@Data
@ColumnWidth(16)
@HeadRowHeight(20)
@ContentRowHeight(18)
public class TestDataExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("区划编号")
    private String code;

    @ExcelProperty("父区划编号")
    private String parentCode;
}
