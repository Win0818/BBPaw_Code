package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;

public class XmlReader {
	private static final String TAG = "--XmlReader--";
	private static final boolean DEBUG = true;

	
	
	public static List<VaccineInfo> pullVeccineDefault(Context context) 
	{
		List<VaccineInfo> dataList = null;
		 
		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			pullParserFactory.setNamespaceAware(true);  
	        XmlPullParser xmlPullParser = pullParserFactory.newPullParser(); 
	        InputStream inputStream = context.getResources().getAssets().open("default_vaccine.xml");
	        if(inputStream == null) return dataList;

	        VaccineInfo itemInfo = null;
	    	
	        xmlPullParser.setInput(inputStream,"UTF-8"); 
 	        //��ʼ
 			int eventType = xmlPullParser.getEventType();
 			while(eventType != XmlPullParser.END_DOCUMENT)
 			{
 				String nodeName = xmlPullParser.getName();
 				switch (eventType) 
 				{
 					case XmlPullParser.START_DOCUMENT:
 						{
 							dataList = new ArrayList<VaccineInfo>();
 						}
 						break;
 					case XmlPullParser.START_TAG:
 						{
 							
 							if(nodeName.equals("menu"))
 							{
 								itemInfo = new VaccineInfo(); 
 							}else if (nodeName.equals("item")){
 								String key = xmlPullParser.getAttributeValue("", "key");
 								String value = xmlPullParser.getAttributeValue("", "value");
 								if (key.equals(Vaccine.AGE)) {
 									itemInfo.setAge(value);
 								} else if (key.equals(Vaccine.AGE_TITLE)) {
 									itemInfo.setAgeTitle(value);
 								} else if (key.equals(Vaccine.VACCINE_TYPE_NAME)) {
 									itemInfo.setVaccineTypeName(value);
 								} else if (key.equals(Vaccine.VACCINE_TYPE)) {
 									itemInfo.setVaccineType(value);
 								} else if (key.equals(Vaccine.VACCINE_EXPLAIN)) {
 									itemInfo.setVaccineExplain(value);
 								} else if (key.equals(Vaccine.DATE)) {
 									itemInfo.setDate(value);
 								}
 							}
 						}
 						break;
 					case XmlPullParser.END_TAG:  
 	                    {  
 	                    	if(DEBUG)Log.e(TAG, "END_TAG: nodeName---"+nodeName);
 							if(nodeName.equals("menu")){
 								dataList.add(itemInfo);
 							}
 	                    }  
                         break;
 					default:
 						break;
 				}
 				eventType = xmlPullParser.next(); 
 			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException err! "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException err! "+e.getMessage());
			e.printStackTrace();
		}
		if(DEBUG)Log.e("lee", " pullVeccineDefault size---" + dataList.size());
		return dataList;
	}
	
	
	/** 
	 * ��ȡSD����ָ���ļ����е�XML�ļ�
	 * @param fileName 
	 * @return ����XML�ļ���inputStream 
	 */       
	public static InputStream getInputStream(String filePath)
	{  
		InputStream inputStream = null;
	    try {  
	    	  
	        File xmlFlie = new File(filePath); 
	        if(xmlFlie.exists()){
	        	inputStream = new FileInputStream(xmlFlie);  
			}
	    } catch (IOException e) {  
	    	if(DEBUG)Log.e(TAG, "getInputStream...IOException : " + e.getMessage());  
	        e.printStackTrace();  
	    }  
	    return inputStream;  
	}
	
	 /**
     * �ж��ļ��ı����ʽ
     * 
     * @param fileName
     *            :file
     * @return �ļ������ʽ
     * @throws Exception
     */
    public static String codeString(String fileName) {
    	
    	String code = "UTF-8";
    	
        try {
			BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
			        fileName));
			int p = (bin.read() << 8) + bin.read();
 
			switch (p) {
			case 0xefbb:
			    code = "UTF-8";
			    break;
			case 0xfffe:
			    code = "Unicode";
			    break;
			case 0xfeff:
			    code = "UTF-16BE";
			    break;
			default:
			    code = "GBK";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        return code;
    }
}