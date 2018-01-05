package com.shellshellfish.aaas.common.enums;

public enum TrdPayFlowStatusEnum {
  CONFIRMFAILED(0,"确认失败"), CONFIRMSUCCESS(1, "确认成功"), PARTCONFIRMED(2, "部分确认"),
  REALTIMECONFIRMSUCESS(3, "实时确认成功"), NOTHANDLED(9, "未处理");

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  private int status;
  private String comment;
  TrdPayFlowStatusEnum(int status, String comment){
    this.status = status;
    this.comment = comment;
  }

  public static String getComment(int status){

    for (TrdPayFlowStatusEnum enumItem : TrdPayFlowStatusEnum.values()) {
      if (enumItem.getStatus() == status) {
        return enumItem.getComment();
      }
    }
    return null;
  }


  public static TrdPayFlowStatusEnum getByStatus(int status){
    for (TrdPayFlowStatusEnum enumItem : TrdPayFlowStatusEnum.values()) {
      if (enumItem.getStatus() == status) {
        return enumItem;
      }
    }
    return null;
  }

}
