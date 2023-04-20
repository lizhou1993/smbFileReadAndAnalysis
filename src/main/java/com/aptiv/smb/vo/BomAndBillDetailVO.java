package com.aptiv.smb.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BomAndBillDetailVO {
    private String model;
    private String order;
    private String type;
    private String bomId;
    private String createTime;
    private List<BomDetailVO> bomDetailVOS;

}
