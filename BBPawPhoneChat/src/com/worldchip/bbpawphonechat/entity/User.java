/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldchip.bbpawphonechat.entity;

import android.graphics.drawable.Drawable;
import com.easemob.chat.EMContact;

public class User extends EMContact {
	private int unreadMsgCount;
	private String header;
	private String avatar;
    private Drawable contactsHead;
    private String  headurl;
    private String  remark_name;

	public User(){}
	
	public User(String username, Drawable  drawable){
	    this.username = username;
	    this.contactsHead = drawable;
	}

	public User(String username){
	    this.username = username;
	}
	
	public String getHeader() {
		return header;
	}
	
	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
	
	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    
	
	public Drawable getContactsHead() {
		return contactsHead;
	}

	public void setContactsHead(Drawable contactsHead) {
		this.contactsHead = contactsHead;
	}

    public String getRemark_name() {
		return remark_name;
	}

	public void setRemark_name(String remark_name) {
		this.remark_name = remark_name;
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
}
