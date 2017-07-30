package com.egreat.adlauncher.db;

public class ApkInfo {
	int id = -1;
	String id_text = null;

	public boolean isLocal = false;
	public int resId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getId_text() {
		return id_text;
	}

	public void setId_text(String id_text) {
		this.id_text = id_text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getDownloadlink() {
		return downloadlink;
	}

	public void setDownloadlink(String downloadlink) {
		this.downloadlink = downloadlink;
	}

	public String getApkconfig() {
		return apkconfig;
	}

	public void setApkconfig(String apkconfig) {
		this.apkconfig = apkconfig;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public void setIntro(String introl){
		this.introl = introl;
	}
	
	public String getIntrol(){
		return this.introl;
	}
	
	String introl = null;
	String name = null;
	String packagename = null;
	String edition = null;
	String downloadlink = null;
	String apkconfig = null;
	String poster = null;
}
