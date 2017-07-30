package com.beanpai.egr.shopping.utils;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.beanpai.egr.shopping.entity.Category;
import com.beanpai.egr.shopping.entity.CategoryInfo;
import com.beanpai.egr.shopping.entity.Commodity;
import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.CommodityInfo;
import com.beanpai.egr.shopping.entity.MsgResult;

import android.util.Log;


public class JsonParserUtil {

	/**
	 */
	public static final int MAX_TEPY_VIDEO_NUM = 5;
	/**
	 */
	public static final int MAX_VIDEOS_NUM = 10;
	private static final String TAG = "--JsonParserUtil--";
	//private static final boolean DEBUG = false;
	
	
	public static CategoryInfo parseCategoryInfo(String json){
		CategoryInfo categoryInfo = new CategoryInfo();
		List<Category> categoryList = new ArrayList<Category>();
		
		Category categroy = null;
		try{
			Log.e(TAG, "parseCategoryInfo..json="+json);
		    JSONObject jsonObject = new JSONObject(json);
		    if(jsonObject!=null){
		    	categoryInfo.id =jsonObject.getInt("id");
		    	categoryInfo.mallName =jsonObject.getString("mallName");
				JSONArray dataJsonList = jsonObject.getJSONArray("categorys"); 	              
				if(dataJsonList!=null){
					for(int i=0; i<dataJsonList.length(); i++){
						JSONObject obj = dataJsonList.getJSONObject(i);
						categroy = new Category();
						categroy.id = obj.getInt("id");
						categroy.type = obj.getInt("type");
						categroy.categoryName = obj.getString("categoryName");
						categroy.categoryFlag = obj.getInt("categoryFlag");
						categroy.logoUrl = obj.getString("logoUrl");
						categroy.programListUrl = obj.getString("programListUrl");
						
						categoryList.add(categroy);
					}
				}
		    }
		}catch(Exception err){
			err.printStackTrace();
		}
        
		categoryInfo.categorys = categoryList;
		return categoryInfo;
	}
	
	
	public static CommodityInfo parseCommodityInfo(String json){
		CommodityInfo commondityInfo = new CommodityInfo();
		List<Commodity> commodityList = new ArrayList<Commodity>();
		
		Commodity commodity = null;
		try{
			Log.e(TAG, "parseCommodityInfo..json="+json);
		    JSONObject jsonObject = new JSONObject(json);
		    if(jsonObject!=null){
		    	commondityInfo.pageSize =jsonObject.getInt("pageSize");
		    	commondityInfo.currentPage =jsonObject.getInt("currentPage");
		    	commondityInfo.totalPage =jsonObject.getInt("totalPage");
		    	commondityInfo.totalSize =jsonObject.getInt("totalSize");
				JSONArray dataJsonList = jsonObject.getJSONArray("pageList"); 	              
				if(dataJsonList!=null){
					for(int i=0; i<dataJsonList.length(); i++){
						JSONObject obj = dataJsonList.getJSONObject(i);
						commodity = new Commodity();
						commodity.salePrice = obj.getString("salePrice");
						commodity.nowSalePrice = obj.getString("nowSalePrice");
						commodity.integral = obj.getString("integral");
						commodity.nowIntegral = obj.getString("nowIntegral");
						commodity.id = obj.getInt("id");
						commodity.detailUrl = obj.getString("detailUrl");
						commodity.name = obj.getString("name");
						commodity.fileurl = obj.getString("fileurl");
						if(commodity.nowSalePrice==null || commodity.nowSalePrice.equals("") 
								 ||commodity.fileurl==null || commodity.fileurl.equals("")){
							continue;
						}
						commodityList.add(commodity);
					}
				}
		    }
		}catch(Exception err){
			err.printStackTrace();
		}
        
		commondityInfo.commoditys = commodityList;
		return commondityInfo;
	}
	
	public static CommodityDetailInfo parseDetailCommodityInfo(String json){
		CommodityDetailInfo commondityDetailInfo = new CommodityDetailInfo();
		List<String> detailUrls = new ArrayList<String>();
		try{
			Log.e(TAG, "parseDetailCommodityInfo..json="+json);
		    JSONObject jsonObject = new JSONObject(json);
		    if(jsonObject!=null){
		    	commondityDetailInfo.id =jsonObject.getInt("id");
		    	commondityDetailInfo.name =jsonObject.getString("name");
		    	commondityDetailInfo.salePrice =jsonObject.getString("salePrice");
		    	commondityDetailInfo.nowSalePrice =jsonObject.getString("nowSalePrice");
		    	commondityDetailInfo.integral =jsonObject.getString("integral");
		    	commondityDetailInfo.nowIntegral =jsonObject.getString("nowIntegral");
		    	commondityDetailInfo.description =jsonObject.getString("description");
		    	commondityDetailInfo.freight =jsonObject.getInt("freight");
		    	commondityDetailInfo.city =jsonObject.getString("city");
		    	commondityDetailInfo.sales =jsonObject.getInt("sales");
		    	commondityDetailInfo.inventory =jsonObject.getInt("inventory");
		    	
		    	JSONArray dataJsonList = jsonObject.getJSONArray("fileurls"); 	              
				if(dataJsonList!=null){
					for(int i=0; i<dataJsonList.length(); i++){
						Log.e(TAG, "parseDetailCommodityInfo...url="+dataJsonList.getString(i));
						detailUrls.add(dataJsonList.getString(i));
					}
				}
		    }
		}catch(Exception err){
			err.printStackTrace();
		}
        
		commondityDetailInfo.detailUrlList = detailUrls;
		return commondityDetailInfo;
	}


	public static MsgResult parseMsgResultInfo(String msgResult) {
		MsgResult msg = new MsgResult();
		try {
			Log.e(TAG, "parseMsgResultInfo..msgResult="+msgResult);
		}catch(Exception err){
			err.printStackTrace();
		}
		
		return msg;
	}

}
