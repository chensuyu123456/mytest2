package com.shellshellfish.aaas.datacollection.server.service.impl;


import com.shellshellfish.aaas.common.utils.DataCollectorUtil;
import com.shellshellfish.aaas.common.utils.MathUtil;
import com.shellshellfish.aaas.common.utils.SSFDateUtils;
import com.shellshellfish.aaas.common.utils.TradeUtil;
import com.shellshellfish.aaas.datacollect.DailyFunds.Builder;
import com.shellshellfish.aaas.datacollect.*;
import com.shellshellfish.aaas.datacollect.DataCollectionServiceGrpc.DataCollectionServiceImplBase;
import com.shellshellfish.aaas.datacollection.server.model.*;
import com.shellshellfish.aaas.datacollection.server.model.DailyFunds;
import com.shellshellfish.aaas.datacollection.server.repositories.CoinFundYieldrateRepository;
import com.shellshellfish.aaas.datacollection.server.repositories.DailyFundsRepository;
import com.shellshellfish.aaas.datacollection.server.repositories.MongoFundYeildRateRepository;
import com.shellshellfish.aaas.datacollection.server.service.DataCollectionService;
import com.shellshellfish.aaas.datacollection.server.util.DateUtil;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;


public class DataCollectionServiceImpl extends DataCollectionServiceImplBase implements
		DataCollectionService {


	Logger logger = LoggerFactory.getLogger(DataCollectionServiceImpl.class);


	private Random random = new Random();

	@Autowired
	DailyFundsRepository dailyFundsRepository;

	@Autowired
	MongoTemplate mongoTemplate;


	@Autowired
	CoinFundYieldrateRepository coinFundYieldrateRepository;

	@Autowired
	MongoFundYeildRateRepository mongoFundYeildRateRepository;

	@Override
	public List<FundYeildRate> getLastFundYeildRates(List<String> fundCodes) {
		Long yestDay = SSFDateUtils.getYestdayDateInLong();
		return mongoFundYeildRateRepository.findByQuerydateAndCodeIsIn(yestDay / 1000L, fundCodes);
	}

	@Override
	public List<FundYeildRate> getLastFundYeildRates4Test(List<String> fundCodes) {
		return mongoFundYeildRateRepository.findByCodeIsIn(fundCodes);
	}


	@Override
	public void getGrowthRateOfMonetaryFundsList(MonetaryFundsQueryItem monetaryFundsQueryItem,
												 StreamObserver<GrowthRateOfMonetaryFundCollection> responseObserver) {


		Long startTime = monetaryFundsQueryItem.getStartDate();
		Long endTime = monetaryFundsQueryItem.getEndDate();

		if (startTime != null && startTime > 10000000000L)
			startTime /= 1000L;

		if (endTime != null && endTime > 10000000000L)
			endTime /= 1000L;

		List<CoinFundYieldRate> originResult = coinFundYieldrateRepository
				.findCoinFundYieldRate(monetaryFundsQueryItem.getCode(), startTime, endTime);

		if (originResult == null || originResult.isEmpty())
			originResult = new ArrayList<>(0);

		GrowthRateOfMonetaryFundCollection.Builder builder = GrowthRateOfMonetaryFundCollection.newBuilder();
		copyProperites(originResult, builder);

		responseObserver.onNext(builder.build());
		responseObserver.onCompleted();

	}

	@Override
	public List<FundYeildRate> getLatestFundYeildRates(List<String> fundCodes, Long queryDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").in(fundCodes).and("querydate").gte(queryDate));
		List<FundYeildRate> fundYeildRates = mongoTemplate.find(query, FundYeildRate.class,
				"fund_yieldrate");
		return fundYeildRates;
	}


	@Override
	public void getFundsPrice(com.shellshellfish.aaas.datacollect.FundCodes request,
							  io.grpc.stub.StreamObserver<com.shellshellfish.aaas.datacollect.FundInfos> responseObserver) {
		List<String> codes = request.getFundCodeList();
		FundInfos fundPrices = getPriceOfCodes(codes);
		responseObserver.onNext(fundPrices);
		responseObserver.onCompleted();

	}

	@Override
	public void getFundDataOfDay(DailyFundsQuery request, StreamObserver<DailyFundsCollection>
			responseObserver) {
		String navLatestDateStart = request.getNavLatestDateStart();
		String navLatestDateEnd = request.getNavLatestDateEnd();
		List<String> codes = request.getCodesList();
		List<DailyFunds> dailyFundsList = new ArrayList<>();
		List<DailyFunds> partialFundsList = null;
		List<String> baseIndexs = new ArrayList<>();
		for (String code : codes) {
			try {

				Query query = new Query();
				query.addCriteria(Criteria.where("code").is(code).andOperator(
						Criteria.where("navlatestdate").gt(DateUtil.getDateLongVal
								(navLatestDateStart) / 1000),
						Criteria.where("navlatestdate").lte(DateUtil.getDateLongVal
								(navLatestDateEnd) / 1000)));
				partialFundsList = mongoTemplate.find(query, DailyFunds.class, "fund_yieldrate");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (!CollectionUtils.isEmpty(partialFundsList)) {
				//it is normal funds
				dailyFundsList.addAll(partialFundsList);
			} else {
				//it is baseIdx code
				baseIndexs.add(code);
			}
		}

		List<com.shellshellfish.aaas.datacollect.DailyFunds> dailyFundsListProto = new ArrayList<>();
		Builder builderDailyFunds = com.shellshellfish.aaas.datacollect.DailyFunds.newBuilder();
		for (DailyFunds dailyFunds : dailyFundsList) {
			BeanUtils.copyProperties(dailyFunds, builderDailyFunds, DataCollectorUtil
					.getNullPropertyNames(dailyFunds));

			Query query = new Query();
			query.addCriteria(Criteria.where("code").is(dailyFunds.getCode()));
			List<FundResources> fundResources = mongoTemplate.find(query, FundResources.class,
					"fundresources");
			if (CollectionUtils.isEmpty(fundResources)) {
				logger.error("cannot find fundTypeOne for code:" + dailyFunds.getCode());
			} else {
				builderDailyFunds.setFname(fundResources.get(0).getFname());
				builderDailyFunds.setFirstInvestType(fundResources.get(0).getFirstinvesttype());
				builderDailyFunds.setSecondInvestType(fundResources.get(0).getSecondinvesttype());
			}
			List<DayIndicator> dayIndicators = mongoTemplate.find(query, DayIndicator.class,
					"dayindicator");
			if (CollectionUtils.isEmpty(dayIndicators)) {
				logger.error("cannot find close for code:" + dailyFunds.getCode());
			} else {
				builderDailyFunds.setClose(dayIndicators.get(0).getClose());
			}
			dailyFundsListProto.add(builderDailyFunds.build());
			builderDailyFunds.clear();
		}
		//check if the codes is of base codes
		List<FundBaseClose> fundbasecloses = null;
		List<FundBaseClose> fundbaseclosesAll = new ArrayList<>();
		if (!CollectionUtils.isEmpty(baseIndexs)) {
			for (String code : baseIndexs) {
				try {
					Query query = new Query();
//      String dateStart = DateUtil.getDateStrFromLong(Long.parseLong(navLatestDateStart));
//      String dateEnd = DateUtil.getDateStrFromLong(Long.parseLong(navLatestDateEnd));
					query.addCriteria(Criteria.where("code").is(code).andOperator(Criteria.where
							("querydate").gt((DateUtil.getDateLongVal(navLatestDateStart) / 1000)), Criteria.where
							("querydate").lte(DateUtil.getDateLongVal(navLatestDateEnd) / 1000)));
					fundbasecloses = mongoTemplate.find(query, FundBaseClose.class, "fundbaseclose_origin");
					if (!CollectionUtils.isEmpty(fundbasecloses)) {
						fundbaseclosesAll.addAll(fundbasecloses);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("failed to convert date str to long date");
				}
			}


			if (CollectionUtils.isEmpty(fundbaseclosesAll)) {
				logger.error("cannot find baseIdx for dateStart:" + navLatestDateStart + " dateEnd:" + navLatestDateEnd);
			} else {
				//为了适应这个奇葩的表结构 fundbaseclose，只好写这个奇葩的hardcode赋值
				builderDailyFunds.clear();
				Set checkCodesSet = new HashSet();
				checkCodesSet.addAll(baseIndexs);
				Double nvadj = null;
				for (FundBaseClose fundBaseClose : fundbaseclosesAll) {
					builderDailyFunds.setCode(fundBaseClose.getCode());
					nvadj = fundBaseClose.getClose();
					builderDailyFunds.setNavadj(nvadj);
					builderDailyFunds.setNavLatestDate(fundBaseClose.getQuerydate());
					dailyFundsListProto.add(builderDailyFunds.build());
					builderDailyFunds.clear();
				}
			}
		}
		final DailyFundsCollection.Builder builder = DailyFundsCollection.newBuilder()
				.addAllDailyFunds(dailyFundsListProto);
		responseObserver.onNext(builder.build());
		responseObserver.onCompleted();
	}


	public FundInfos getPriceOfCodes(List<String> codes) {
//		List<FundYeildRate> fundYeildRateList = getLastFundYeildRates4Test(codes);
		Long nineDayBefore = TradeUtil.getUTCTimeNDayAfter(-9)/1000;
		logger.info("nineDayBefore:" + nineDayBefore);
		List<FundYeildRate> fundYeildRates = getLatestFundYeildRates(codes, nineDayBefore);
		List<FundYeildRate> filteredFunds = filter(fundYeildRates);
		FundInfos.Builder builder = FundInfos.newBuilder();
		FundInfo.Builder builderFI = FundInfo.newBuilder();
		for (FundYeildRate fundYeildRate : filteredFunds) {

			builderFI.setNavunit(
					Math.toIntExact(MathUtil.getLongPriceFromDoubleOrig(fundYeildRate.getNavunit())));
			builderFI.setFundCode(fundYeildRate.getCode());
			builder.addFundInfo(builderFI);
		}
		return builder.build();
	}


	private List<FundYeildRate> filter(List<FundYeildRate> fundYeildRateList) {
		Map<String, FundYeildRate> fundYeildRateHashMap = new HashMap<>();
		for (FundYeildRate fundYeildRate : fundYeildRateList) {
			if (!fundYeildRateHashMap.containsKey(fundYeildRate.getCode())) {
				if (fundYeildRate.getNavunit() != null && fundYeildRate.getNavunit() != Double.MIN_VALUE) {
					fundYeildRateHashMap.put(fundYeildRate.getCode(), fundYeildRate);
				}
			}else {
				if (fundYeildRateHashMap.get(fundYeildRate.getCode()).getQuerydate() < fundYeildRate
						.getQuerydate() && fundYeildRate.getNavunit() != Double.MIN_VALUE) {
					fundYeildRateHashMap.put(fundYeildRate.getCode(), fundYeildRate);
				}
			}
		}
		List<FundYeildRate> fundYeildRates = new ArrayList<>();
		for (Entry<String, FundYeildRate> entry : fundYeildRateHashMap.entrySet()) {
			fundYeildRates.add(entry.getValue());
		}
		return fundYeildRates;
	}

	@Override
	public List<String> CollectItemsSyn(List<String> collectDatas) throws Exception {
		return null;
	}

	@Override
	public List<String> CollectItemsAsyn(List<String> collectDatas) throws Exception {
		return null;
	}


	private void copyProperites(List<CoinFundYieldRate> originList, GrowthRateOfMonetaryFundCollection.Builder builder) {

		List<GrowthRateOfMonetaryFund> result = new ArrayList<>(originList.size());
		GrowthRateOfMonetaryFund.Builder growthRateOfMonetaryFundsBuilder = GrowthRateOfMonetaryFund.newBuilder();
		for (int i = 0; i < originList.size(); i++) {
			CoinFundYieldRate coinFundYieldRate = originList.get(i);
			growthRateOfMonetaryFundsBuilder.setCode(coinFundYieldRate.getCode());
			growthRateOfMonetaryFundsBuilder.setQueryDate(coinFundYieldRate.getQuerydate());
			growthRateOfMonetaryFundsBuilder.setQueryDateStr(coinFundYieldRate.getQueryDateStr());
			growthRateOfMonetaryFundsBuilder.setTenKiloUnitYield(coinFundYieldRate.getTenKiloUnitYield());
			growthRateOfMonetaryFundsBuilder.setYieldof7Days(coinFundYieldRate.getYieldoOf7Days());
			growthRateOfMonetaryFundsBuilder.setUpdate(coinFundYieldRate.getUpdate());
			builder.addGrowthRateOfMonetaryFunds(growthRateOfMonetaryFundsBuilder.build());
		}
	}
}

