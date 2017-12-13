package com.shellshellfish.aaas.finance.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shellshellfish.aaas.finance.model.ChartResource;
import com.shellshellfish.aaas.finance.returnType.FundReturn;
import com.shellshellfish.aaas.finance.returnType.PerformanceVolatilityReturn;
import com.shellshellfish.aaas.finance.returnType.ReturnType;
import com.shellshellfish.aaas.finance.service.IndexService;
import com.shellshellfish.aaas.finance.util.FishLinks;

@Service("indexService")
@Transactional
public class IndexServiceImpl implements IndexService {

	@Autowired
	AssetAllocationServiceImpl assetAllocationService;

	@Override
	public Map<String, Object> homepage() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> linksMap = new HashMap<String, Object>();
		List<Map> relateList = new ArrayList();
		Map<String, Object> linkMap = new HashMap<String, Object>();
		linkMap.put("href", "/api/ssf-finance/retests");
		linkMap.put("name", "retest");
		relateList.add(linkMap);

		Map<String, Object> riskMap = new HashMap<String, Object>();
		riskMap.put("href", "/api/ssf-finance/risktypes/{risktype}");
		riskMap.put("name", "prerisktypes");
		relateList.add(riskMap);

		Map<String, Object> chartMap = new HashMap<String, Object>();
		chartMap.put("href", "/api/ssf-finance/product-groups/charts/1");
		chartMap.put("name", "charts");
		relateList.add(chartMap);
		linksMap.put("related", relateList);

		Map<String, Object> selfMap = new HashMap<String, Object>();
		selfMap.put("href", "/api/ssf-finance/product-groups/homepage");
		selfMap.put("describedBy", "/api/ssf-finance/product-groups/homepage.json");
		linksMap.put("self", selfMap);

		double historicalYearPerformance = 0;
		double historicalvolatility = 0;
		PerformanceVolatilityReturn performanceVolatilityReturn = assetAllocationService.getPerformanceVolatility("1",
				"C1", "1");
		if (performanceVolatilityReturn != null) {
			// .getPerformanceVolatility(cust_risk, investment_horizon);
			List<Map<String, Object>> list = performanceVolatilityReturn.get_items();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					Integer id = (Integer) map.get("id");
					if (id == 1) {
						historicalYearPerformance = (double) map.get("value");
					} else if (id == 2) {
						historicalvolatility = (double) map.get("value");
					}
				}
			}
		}
		result.put("historicalYearPerformance", historicalYearPerformance);
		result.put("historicalvolatility", historicalvolatility);
		String groupId = performanceVolatilityReturn.getProductGroupId();
		String subGroupId = performanceVolatilityReturn.getProductSubGroupId();
		result.put("groupId", groupId);
		result.put("subGroupId", subGroupId);

		FundReturn fundReturn = assetAllocationService.selectById(groupId, subGroupId);
		List<Map<String, Double>> assetsRatiosList = fundReturn.getAssetsRatios();
		result.put("assetsRatios", assetsRatiosList);

		result.put("name", "理财产品 首页");
		result.put("_links", linksMap);

		return result;
	}

	@Override
	public ChartResource getChart() {
		ChartResource chartResource = new ChartResource();
		chartResource.setName("历史收益图");
		chartResource.setType("历史收益图");
		ReturnType returnType = null;
		// ReturnType returnType =
		// assetAllocationService.getFundGroupIncomeMounth(id,subGroupId,6);
		List<Map<String, Object>> list = returnType.get_items();
		List<List<List<Object>>> listAfter = new ArrayList();
		if (list != null) {
			return chartResource;
		}
		List<List<Object>> listAfter2 = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			List<Object> listConvert = new ArrayList();
			Map<String, Object> value = list.get(i);
			for (String key : value.keySet()) {
				listConvert.add(key);
				listConvert.add(value.get(key));
			}
			listAfter2.add(listConvert);
		}
		listAfter.add(listAfter2);
		chartResource.setLineValues(listAfter);
		FishLinks links = new FishLinks();
		links.setSelf("/api/ssf-finance/product-groups/homepage/charts/1");
		links.setDescribedBy("/schema/api/ssf-finance/product-groups/homepage/charts/item.json");
		chartResource.setLinks(links);
		return chartResource;
	}

	@Override
	public Map<String, Object> getRiskInfo(String risktype) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> linksMap = new HashMap<String, Object>();
		PerformanceVolatilityReturn performanceVolatilityReturn = assetAllocationService.getPerformanceVolatility("1",
				risktype, null);
		String groupId = performanceVolatilityReturn.getProductGroupId();
		String subGroupId = performanceVolatilityReturn.getProductSubGroupId();
		FundReturn fundReturn = assetAllocationService.selectById(groupId, subGroupId);
		if (fundReturn == null) {
			result.put("error", "404 DATA NOT FOUND.");
			return result;
		}
		List<Map<String, Double>> assetsRatiosList = fundReturn.getAssetsRatios();
		result.put("assetsRatios", assetsRatiosList);

		Map<String, Object> selfMap = new HashMap<String, Object>();
		selfMap.put("href", "/api/ssf-finance/product-groups/risktypes/" + risktype);
		selfMap.put("describedBy", "/api/ssf-finance/product-groups/risktypes.json");
		linksMap.put("self", selfMap);

		result.put("_links", linksMap);
		return result;
	}

}
