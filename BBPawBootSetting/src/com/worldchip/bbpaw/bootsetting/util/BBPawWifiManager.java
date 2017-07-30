package com.worldchip.bbpaw.bootsetting.util;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class BBPawWifiManager {
	private static final String TAG = BBPawWifiManager.class.getSimpleName();
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mScanResultList;
	private static BBPawWifiManager mInstance = null;
	
	public static BBPawWifiManager getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (BBPawWifiManager.class)
			{
				if (mInstance == null)
				{
					mInstance = new BBPawWifiManager(context);
				}
			}
		}
		return mInstance;
	}
	
	public BBPawWifiManager(Context context) {
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	public int getWifiState() {
		return mWifiManager.getWifiState();
	}


	/**
	 * 扫描wifi
	 * 
	 * @return
	 */
	public void searchWifi() {
		mWifiManager.startScan();
		mWifiManager.getScanResults();
		//mWificonfigurationList = mWifiManager.getConfiguredNetworks();
		mScanResultList = mWifiManager.getScanResults();
		WifiUtils.sortResultListForWifiSignal(mScanResultList);
	}

	public List<WifiConfiguration> getConfiguredNetworks() {
		return mWifiManager.getConfiguredNetworks();
	}

	public List<ScanResult> getScanrResults() {
		return mScanResultList;
	}

	/**
	 * 指定配置好的网络进行连接 
	 * 
	 * @param index
	 */
	public void connectConfiguration(WifiConfiguration wcg) {

		mWifiManager.enableNetwork(wcg.networkId, true);

	}

	/**
	 *	添加一个网络并连接  
	 */
	public int addNetwork(WifiConfiguration wcg) {
		//Boolean isConnect = mWifiManager.enableNetwork(wcgId, true);
		//LogUtil.e(TAG, "addNetwork isConnect == "+isConnect);
		return mWifiManager.addNetwork(wcg);
	}
	
	/**
	 *	添加一个网络并连接  
	 */
	public boolean enableNetwork(int netId, boolean disableOthers) {
		return mWifiManager.enableNetwork(netId, disableOthers);
	}

	
	/**
	 *  断开指定ID的网络 
	 */
	public void disConnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	public String getMacAddress() {
		refreshWifiInfo();
		return (mWifiInfo == null) ? "null" : mWifiInfo.getMacAddress();
	}

	public int getIpAddress() {
		refreshWifiInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public String getBSSID() {
		refreshWifiInfo();
		return (mWifiInfo == null) ? "null" : mWifiInfo.getBSSID();
	}

	public int getNetworkId() {
		refreshWifiInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String getSSID() {
		refreshWifiInfo();
		return (mWifiInfo == null) ? "null" : mWifiInfo.getSSID();
	}

	public void refreshWifiInfo() {
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public WifiConfiguration createWifiInfo(String SSID, String pwd, int type) {

		WifiConfiguration wc = new WifiConfiguration();

		wc.allowedAuthAlgorithms.clear();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.clear();
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		wc.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.isExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (type == WifiUtils.SECURITY_NONE) // WIFICIPHER_NOPASS
		{
			wc.wepKeys[0] = "";
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
		}
		if (type == WifiUtils.SECURITY_WEP) // WIFICIPHER_WEP
		{
			wc.hiddenSSID = true;
			wc.wepKeys[0] = "\"" + pwd + "\"";
			wc.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
		}
		if (type == WifiUtils.SECURITY_PSK) // WIFICIPHER_WPA
		{
			wc.preSharedKey = "\"" + pwd + "\"";
			wc.hiddenSSID = true;
			wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wc.status = WifiConfiguration.Status.ENABLED;
		}
		return wc;
	}

	/**
	 * �Ƿ���ڴ�wifi
	 * 
	 * @param SSID
	 * @return
	 */
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : configuredNetworks) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
	
}
