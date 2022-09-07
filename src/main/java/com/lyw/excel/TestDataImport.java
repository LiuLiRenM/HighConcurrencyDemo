package com.lyw.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import org.apache.commons.codec.Charsets;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试数据导出到excel
 *
 * @author LiuYaoWen
 * @date 2022/9/7 22:03
 */
public class TestDataImport {

    private static String getPath() {
        return System.getProperty("user.dir") + "/" + System.currentTimeMillis() + ".xlsx";
    }

    public <T> void export(String fileName, String sheetName, List<T> dataList, Class<T> clazz, Integer relativeHeadRowIndex, WriteHandler... writeHandlers) throws UnsupportedEncodingException {
        fileName = URLEncoder.encode(fileName, Charsets.UTF_8.name());
        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.write(new File(fileName), clazz).sheet(sheetName);
        // 设置write handler
        for (WriteHandler writeHandler : writeHandlers) {
            sheetBuilder.registerWriteHandler(writeHandler);
        }
        // 开启默认样式
        sheetBuilder.useDefaultStyle(true)
                // 设置写入头信息开始的行数
                .relativeHeadRowIndex(relativeHeadRowIndex);
        // 执行Write
        sheetBuilder.doWrite(dataList);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String fileName = "test.xlsx";
        List<TestDataExcel> testDataExcelList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TestDataExcel testDataExcel = new TestDataExcel();
            int random = (int) (Math.random() * 10 + 1);
            testDataExcel.setCode(String.valueOf(random));
            testDataExcel.setParentCode("2");
            testDataExcelList.add(testDataExcel);
        }
        String sheetName = "test";
        TestDataImport dataImport = new TestDataImport();
        CustomMergeStrategy customMergeStrategy = new CustomMergeStrategy(testDataExcelList.stream().map(TestDataExcel::getCode).collect(Collectors.toList()), 0);
        dataImport.export(fileName, sheetName, testDataExcelList, TestDataExcel.class, 0, customMergeStrategy);
    }
}
