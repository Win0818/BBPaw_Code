package com.worldchip.bbpaw.bootsetting.util;

import java.util.ArrayList;
import java.util.List;

public class ShowAllLanguage {
	List<String> list;

	@SuppressWarnings("unused")
	private void showLanguage(String[] str) {
		list = new ArrayList<String>();
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals("en_US")) {
				list.add("America");

			} else if (str[i].equals("zh_CN")) {
				list.add("Chinese");

			}
		}

	}
}
