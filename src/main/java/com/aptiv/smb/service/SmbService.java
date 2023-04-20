package com.aptiv.smb.service;

import com.aptiv.smb.util.DomParsingUtil;
import com.aptiv.smb.util.SmbReaderUtil;
import com.aptiv.smb.util.UnZipUtil;
import com.aptiv.smb.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmbService {
    private final SmbReaderUtil smbReaderUtil;
    public DataVO doIt() {
        // 读取全部文件
        List<String> filePaths = smbReaderUtil.smbReader();
        log.info("压缩包数量共计:" + filePaths.size() + "个");

        Map<String, List<BomModuleExportVO>> bomModuleMap = new HashMap<>(); // bom模块号配置
        Map<String, Integer> bomUsedTimesMap = new HashMap<>(); // bom使用次数map
        List<BomAndBillExportVO> bomAndBillExportVOList = new ArrayList<>(); // bom订单信息

        int count = 0;
        for(String filePath : filePaths) {
            log.info("正在执行第:" + (++count) + "个");
            List<String> xmlStrs = UnZipUtil.unzipFile(filePath); // 解压zip，获取压缩包下的所有xml文件字符串， 一个压缩包文件返回多个xml文件
            for (String xmlStr : xmlStrs) { // dom解析xml，返回需要数据集
                List<BomAndBillDetailVO> bomAndBillDetailVOS = DomParsingUtil.parse(xmlStr); // 一个xml解析多个工单bom
                for (BomAndBillDetailVO vo : bomAndBillDetailVOS) {
                    BomAndBillExportVO bomAndBillExportVO = new BomAndBillExportVO();
                    bomAndBillExportVO.setModel(vo.getModel());
                    bomAndBillExportVO.setOrder(vo.getOrder());
                    bomAndBillExportVO.setCreateTime(vo.getCreateTime());
                    bomAndBillExportVO.setType(vo.getType());
                    bomAndBillExportVO.setBomId(vo.getBomId());
                    bomAndBillExportVOList.add(bomAndBillExportVO);

                    // 如果bomUsedTimesMap bom使用次数 中不含当前bom则加入map
                    if (!bomUsedTimesMap.containsKey(vo.getBomId())) {
                        bomUsedTimesMap.put(vo.getBomId(), 1);
                    } else { // 如果存在map中则使用次数加1
                        bomUsedTimesMap.put(vo.getBomId(), bomUsedTimesMap.get(vo.getBomId()) + 1);
                    }

                    // 如果bomModuleMap bom配置 中不含当前bom则加入map
                    if (!bomModuleMap.containsKey(vo.getBomId())) {
                        List<BomModuleExportVO> bomModuleExportVOS = new ArrayList<>();

                        for (BomDetailVO detailVO : vo.getBomDetailVOS()) {
                            BomModuleExportVO bomModuleExportVO = new BomModuleExportVO();
                            bomModuleExportVO.setPartNo(detailVO.getPartNo());
                            bomModuleExportVO.setBomId(detailVO.getBomId());
                            bomModuleExportVO.setType(detailVO.getType());
                            bomModuleExportVO.setCreateTime(detailVO.getCreateTime());
                            bomModuleExportVOS.add(bomModuleExportVO);
                        }
                        bomModuleMap.put(vo.getBomId(), bomModuleExportVOS);
                    }
                }
            }
        }

        DataVO dataVO = new DataVO();
        //
        List<BomUsedTimesExportVO> bomUsedTimesExportVOList = new ArrayList<>(); // bom使用次数
        for (String key : bomUsedTimesMap.keySet()) {
            BomUsedTimesExportVO exportVO = new BomUsedTimesExportVO();
            exportVO.setBomId(key);
            exportVO.setUsedTimes(bomUsedTimesMap.get(key));
            exportVO.setCreateTime(bomModuleMap.get(key).size() == 0 ? "" : bomModuleMap.get(key).get(0).getCreateTime());
            bomUsedTimesExportVOList.add(exportVO);
        }
        dataVO.setBomUsedTimesExportVOS(bomUsedTimesExportVOList);
        //
        List<BomModuleExportVO> bomModuleExportVOS = new ArrayList();
        for (String key : bomModuleMap.keySet()) {
            bomModuleExportVOS.addAll(bomModuleMap.get(key));
        }
        dataVO.setBomModuleExportVOS(bomModuleExportVOS);
        //
        dataVO.setBomAndBillExportVOS(bomAndBillExportVOList);

        log.info("统计完成");
        return dataVO;
    }
}
