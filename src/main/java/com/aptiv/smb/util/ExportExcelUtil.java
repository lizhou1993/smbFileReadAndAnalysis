package com.aptiv.smb.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.aptiv.smb.vo.BomAndBillExportVO;
import com.aptiv.smb.vo.BomModuleExportVO;
import com.aptiv.smb.vo.BomUsedTimesExportVO;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExportExcelUtil {
    /**
     * 导出为Excel
     * 详细文档https://github.com/alibaba/easyexcel
     */
    public static <T extends BaseRowModel> void exportExcel(HttpServletResponse response, String fileName, List<BomUsedTimesExportVO> data1, Class<BomUsedTimesExportVO> clazz1, List<BomModuleExportVO> data2, Class<BomModuleExportVO> clazz2, List<BomAndBillExportVO> data3, Class<BomAndBillExportVO> clazz3) throws IOException {
        try (ServletOutputStream out = response.getOutputStream()) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            String fileNameEncode = new String((fileName + ".xlsx").getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            Sheet sheet1 = new Sheet(1, 0, clazz1);
            sheet1.setSheetName("BOM记录汇总");
            writer.write(data1, sheet1);

            Sheet sheet2 = new Sheet(2, 0, clazz2);
            sheet2.setSheetName("BOM模块号配置");
            writer.write(data2, sheet2);

            Sheet sheet3 = new Sheet(3, 0, clazz3);
            sheet3.setSheetName("BOM对应订单信息");
            writer.write(data3, sheet3);

            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameEncode);
            response.setHeader("Filename", URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            response.setHeader("Access-Control-Expose-Headers", "Filename");
            writer.finish();
            out.flush();
        }
    }

    public static <T extends BaseRowModel> void saveExcel(String fileName, List<BomUsedTimesExportVO> data1, Class<BomUsedTimesExportVO> clazz1, List<BomModuleExportVO> data2, Class<BomModuleExportVO> clazz2, List<BomAndBillExportVO> data3, Class<BomAndBillExportVO> clazz3) throws Exception {
        try {
            // 任何自己有权限操作得路径
            String path = "C:\\Users\\z7h3qv\\AppData\\Local\\Temp\\" + fileName + ".xlsx";
            OutputStream out = new FileOutputStream(path);
            ExcelWriter writer = EasyExcelFactory.getWriter(out);

            Sheet sheet1 = new Sheet(1, 0, clazz1);
            sheet1.setSheetName("BOM记录汇总");
            writer.write(data1, sheet1);

            Sheet sheet2 = new Sheet(2, 0, clazz2);
            sheet2.setSheetName("BOM模块号配置");
            writer.write(data2, sheet2);

            Sheet sheet3 = new Sheet(3, 0, clazz3);
            sheet3.setSheetName("BOM对应订单信息");
            writer.write(data3, sheet3);

            writer.finish();
            // 关闭流
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
