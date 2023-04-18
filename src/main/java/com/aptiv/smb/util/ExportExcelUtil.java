package com.aptiv.smb.util;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExportExcelUtil {
    /**
     * 导出为Excel
     * 详细文档https://github.com/alibaba/easyexcel
     *
     * @param fileName 文件名称，不包含扩展名
     * @param data     表数据行
     * @param clazz    数据对应的model class
     * @param <T>      数据对应的model
     */
    public static <T extends BaseRowModel> void exportExcel(HttpServletResponse response, String fileName, List<T> data, Class<T> clazz) throws IOException {
        try (ServletOutputStream out = response.getOutputStream()) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            String fileNameEncode = new String((fileName + ".xlsx").getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            Sheet sheet = new Sheet(1, 0, clazz);
            sheet.setSheetName(fileName);
            writer.write(data, sheet);
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameEncode);
            response.setHeader("Filename", URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            response.setHeader("Access-Control-Expose-Headers", "Filename");
            writer.finish();
            out.flush();
        }
    }
}
