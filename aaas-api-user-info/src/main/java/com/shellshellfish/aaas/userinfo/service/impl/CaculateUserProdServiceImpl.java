package com.shellshellfish.aaas.userinfo.service.impl;

import com.shellshellfish.aaas.common.enums.MonetaryFundEnum;
import com.shellshellfish.aaas.common.enums.PendingRecordStatusEnum;
import com.shellshellfish.aaas.common.enums.TrdOrderOpTypeEnum;
import com.shellshellfish.aaas.common.enums.TrdOrderStatusEnum;
import com.shellshellfish.aaas.common.utils.TradeUtil;
import com.shellshellfish.aaas.userinfo.model.MonetaryFund;
import com.shellshellfish.aaas.userinfo.model.dao.MongoCaculateBase;
import com.shellshellfish.aaas.userinfo.model.dao.MongoCaculateResult;
import com.shellshellfish.aaas.userinfo.model.dao.MongoPendingRecords;
import com.shellshellfish.aaas.userinfo.model.dao.UiProductDetail;
import com.shellshellfish.aaas.userinfo.repositories.mysql.UiProductDetailRepo;
import com.shellshellfish.aaas.userinfo.repositories.mysql.UiProductRepo;
import com.shellshellfish.aaas.userinfo.service.CaculateUserProdService;
import com.shellshellfish.aaas.userinfo.utils.MyBeanUtils;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Created by chenwei on 2018- 六月 - 08
 */
@Service
public class CaculateUserProdServiceImpl implements CaculateUserProdService {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  UiProductRepo uiProductRepo;

  @Autowired
  UiProductDetailRepo uiProductDetailRepo;

  /**
   * 查询PendingRecords表里面所有的已经处理完的记录， 交易状态为购买确认和赎回确认的列入
   * 到表MongoCaculateBase表里面
   * @param userProdId
   * @param fundCode
   * @return
   */
  @Override
  public boolean updateCaculateBase(Long userProdId, String fundCode) {
    Query query = new Query();

    query.addCriteria(Criteria.where("user_prod_id").is(userProdId).and("fund_code").is(fundCode)
        .and("process_status").is(PendingRecordStatusEnum.HANDLED.getStatus()).orOperator
            (Criteria.where("trade_status").is(TrdOrderStatusEnum.CONFIRMED.getStatus()),
                Criteria.where("trade_status").is(TrdOrderStatusEnum.SELLCONFIRMED.getStatus())));
    List<MongoPendingRecords> mongoPendingRecords =
    mongoTemplate.find(query, MongoPendingRecords.class,"ui_pending_records");
    for(MongoPendingRecords item: mongoPendingRecords){
        Query querySub = new Query();
        querySub.addCriteria(Criteria.where("user_prod_id").is(userProdId).and("fund_code").is
            (fundCode).and("outside_order_id").is(item.getOutsideOrderId()));
        List<MongoCaculateBase> mongoCaculateBases =
            mongoTemplate.find(querySub, MongoCaculateBase.class,"ui_calc_base");
        if(CollectionUtils.isEmpty(mongoCaculateBases)){
          MongoCaculateBase mongoCaculateBase = new MongoCaculateBase();
          MyBeanUtils.mapEntityIntoDTO(item, mongoCaculateBase);
          mongoCaculateBase.setOutsideOrderId(item.getOutsideOrderId());
          mongoCaculateBase.setTradeConfirmShare(item.getTradeConfirmShare());
          mongoCaculateBase.setTradeConfirmSum(item.getTradeConfirmSum());
          caculateAbstractShare(mongoCaculateBase);
          mongoTemplate.save(mongoCaculateBase);
        }else{
          logger.error("user_prod_id:{} fund_code:{} outside_order_id:{} have more than 1 records"
                  + " in ui_calc_base",userProdId, fundCode, item.getOutsideOrderId());
          if(mongoCaculateBases.size() > 1){
            mongoTemplate.remove(querySub, "ui_calc_base");
          }
          MyBeanUtils.mapEntityIntoDTO(item, mongoCaculateBases.get(0));
          mongoCaculateBases.get(0).setTradeConfirmShare(item.getTradeConfirmShare());
          mongoCaculateBases.get(0).setTradeConfirmSum(item.getTradeConfirmSum());
          //注意凡是pendingProcessStatus状态为Handled的话必须Navadj已经取到
          mongoCaculateBases.get(0).setApplyDateNavadj(item.getApplyDateNavadj());
          caculateAbstractShare(mongoCaculateBases.get(0));
          mongoTemplate.save(mongoCaculateBases.get(0));
        }
    }
    return false;
  }


  private void caculateAbstractShare(MongoCaculateBase mongoCaculateBase){
    if(MonetaryFundEnum.containsCode(mongoCaculateBase.getFundCode())){
      BigDecimal abstractShares = TradeUtil
          .getBigDecimalNumWithDivOfTwoLongAndRundDown(mongoCaculateBase.getTradeConfirmShare()
              *1000000L, mongoCaculateBase.getApplyDateNavadj());
      mongoCaculateBase.setCalculatedShare(abstractShares.longValue());
    }else{
      mongoCaculateBase.setCalculatedShare(mongoCaculateBase.getTradeConfirmShare());
    }
  }

  @Override
  public boolean caculateQuantityByUserProdIdAndFundCode(Long userProdId, String fundCode) {
    //查询所有ui_calc_base里面的对应的记录，求sum,取md5 和当前算出的md5 验证一致性，不一致就更新数据
    Query query = new Query();
    query.addCriteria(Criteria.where("user_prod_id").is(userProdId).and("fund_code").is(fundCode));
    List<MongoCaculateBase> mongoCaculateBases =
    mongoTemplate.find(query, MongoCaculateBase.class, "ui_calc_base");
    if(CollectionUtils.isEmpty(mongoCaculateBases)){
      logger.error("There is no mongoCaculateBases in ui_calc_base for userProdId:{} "
          + "fundCode:{}", userProdId, fundCode);
      return false;
    }
    Long currentQuantity = 0L;

    for(MongoCaculateBase mongoCaculateBase: mongoCaculateBases){
      if(mongoCaculateBase.getTradeType() == TrdOrderOpTypeEnum.BUY.getOperation()){
        currentQuantity +=  mongoCaculateBase.getCalculatedShare();
      }else if(mongoCaculateBase.getTradeType() == TrdOrderOpTypeEnum.REDEEM.getOperation()){
        currentQuantity -= mongoCaculateBase.getCalculatedShare();
      }else{
        logger.error("we havent handle this kind of trade type:{}", mongoCaculateBase.getTradeType());
      }
    }

    StringBuilder stringBuilder = new StringBuilder();
    Collections.sort(mongoCaculateBases, new Comparator<MongoCaculateBase>() {
      @Override
      public int compare(MongoCaculateBase o1, MongoCaculateBase o2) {
        return getOrderKeyInfos(o1).compareToIgnoreCase(getOrderKeyInfos(o2));
      }
    });
    for(MongoCaculateBase mongoCaculateBase: mongoCaculateBases){
      stringBuilder.append(getOrderKeyInfos(mongoCaculateBase)).append("|");
    }
    boolean useHash = true;
    String currHash = null;
    try{
      currHash = TradeUtil.getMD5(stringBuilder.toString());
    }catch (Exception ex){
      useHash = false;
    }

    Query queryResult = new Query();
    queryResult.addCriteria(Criteria.where("user_prod_id").is(userProdId).and("fund_code").is(fundCode));
    List<MongoCaculateResult> mongoCaculateResults =
        mongoTemplate.find(queryResult, MongoCaculateResult.class, "ui_calc_result");
    MongoCaculateResult mongoCaculateResult = null;
    if(CollectionUtils.isEmpty(mongoCaculateResults)){
      logger.info("init mongoCaculateResult ");
      mongoCaculateResult = makeNewMongoCaculateResult(userProdId, currentQuantity, fundCode,
          currHash);
      mongoTemplate.save(mongoCaculateResult, "ui_calc_result");
      updateProductDetailWithCaculateResult(mongoCaculateResult);
    }else{
      if(mongoCaculateResults.size() > 1){
        logger.error("There is duplicate records there, need to clean it up");
        mongoTemplate.remove(queryResult,"ui_calc_result");
        mongoCaculateResult = makeNewMongoCaculateResult(userProdId, currentQuantity, fundCode,
            currHash);
        mongoTemplate.save(mongoCaculateResult, "ui_calc_result");
        updateProductDetailWithCaculateResult(mongoCaculateResult);
      }else{
        mongoCaculateResult = mongoCaculateResults.get(0);
        if(useHash && !mongoCaculateResult.getCurrHash().equals(currHash)){
          logger.info("Hash value is different, we need to update quantity, previous quantity:{} "
              + "currentQuantity:{}", mongoCaculateResult.getCurrQuantity(), currentQuantity);
          mongoCaculateResult.setCurrQuantity(currentQuantity);
          mongoCaculateResult.setCurrHash(currHash);
          mongoTemplate.save(mongoCaculateResult, "ui_calc_result");
          updateProductDetailWithCaculateResult(mongoCaculateResult);
        }else if(!useHash && (mongoCaculateResult.getCurrQuantity() == currentQuantity)) {
          logger.info("Hash value is different, we need to update quantity, previous quantity:{} "
              + "currentQuantity:{}", mongoCaculateResult.getCurrQuantity(), currentQuantity);
          mongoCaculateResult.setCurrQuantity(currentQuantity);
//          mongoCaculateResult.setCurrHash(currHash);
          mongoTemplate.save(mongoCaculateResult, "ui_calc_result");
          updateProductDetailWithCaculateResult(mongoCaculateResult);
        }
      }
    }
    return true;
  }
  MongoCaculateResult makeNewMongoCaculateResult(Long userProdId, Long currentQuantity, String
      fundCode, String
      currHash ){
    MongoCaculateResult mongoCaculateResult = new MongoCaculateResult();
    mongoCaculateResult.setCurrQuantity(currentQuantity);
    mongoCaculateResult.setFundCode(fundCode);
    mongoCaculateResult.setCurrHash(currHash);
    mongoCaculateResult.setUserProdId(userProdId);
    return mongoCaculateResult;
  }

  public String getOrderKeyInfos(MongoCaculateBase mongoCaculateBase){
    StringBuilder sb = new StringBuilder();
    return sb.append(mongoCaculateBase.getApplyDateStr()).append(mongoCaculateBase.getOutsideOrderId()).append
        (mongoCaculateBase.getTradeType()).append(mongoCaculateBase.getCalculatedShare()).toString();
  }
  @Override
  public void updateCaculateBase() {
    List<Long> prodIds = uiProductRepo.getAllId();
    for(Long userProdId: prodIds){
      List<UiProductDetail> uiProductDetails = uiProductDetailRepo.findAllByUserProdId(userProdId);
      for(UiProductDetail uiProductDetail: uiProductDetails){
        try{
          updateCaculateBase(userProdId, uiProductDetail.getFundCode());
          caculateQuantityByUserProdIdAndFundCode(userProdId, uiProductDetail.getFundCode());
        }catch (Exception ex){
          logger.error("error:", ex);
        }
      }
    }
  }

  private void updateProductDetailWithCaculateResult(MongoCaculateResult mongoCaculateResult){
    UiProductDetail uiProductDetail = uiProductDetailRepo.findByUserProdIdAndFundCode
        (mongoCaculateResult.getUserProdId(), mongoCaculateResult.getFundCode());
    if(uiProductDetail != null){
      if(uiProductDetail.getLastestSerial() == mongoCaculateResult.getCurrHash()){
        if(uiProductDetail.getFundQuantity() == Math.toIntExact(mongoCaculateResult
            .getCurrQuantity())){
          logger.info("There is no change for both hash and quantity");
        }else{
          logger.error("the quantity is different: uiProdDetail:{}, caculResult:{} with the same "
              + "hash:{}", uiProductDetail.getFundQuantity(), mongoCaculateResult.getCurrQuantity
              (), mongoCaculateResult.getCurrHash());
          uiProductDetail.setFundQuantity(Math.toIntExact(mongoCaculateResult.getCurrQuantity()));

        }
      }else{
        uiProductDetail.setFundQuantity(Math.toIntExact(mongoCaculateResult.getCurrQuantity()));
        uiProductDetail.setLastestSerial(mongoCaculateResult.getCurrHash());
      }
      uiProductDetailRepo.save(uiProductDetail);
    }
  }
}
