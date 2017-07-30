package com.worldchip.bbpawphonechat.entity;

import java.io.Serializable;
import java.util.List;

import android.util.Log;

import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity.ResponseEntity;

public class CommonProblemEntity {
	/**
	 * list_question : [{"content":
	 * "could u stop doing that stupid programming, u r such a liar"
	 * ,"create_time":1441728000000,"type":"硬件外观","user_image":[{"url":
	 * "http://push.bbpaw.com.cn/upload/chat_images/xx/xx"
	 * },{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"
	 * }],"response":
	 * {"content":"thank you for your response","create_time":1441728000000
	 * ,"feed_image"
	 * :[{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"}
	 * ]}},{"content"
	 * :"these whxxx r`nt loyal","create_time":1441729000000,"type"
	 * :"健康护眼","user_image"
	 * :[{"url":""}],"response":{"content":"","create_time":0
	 * ,"feed_image":[{"url":""}]}}] resultCode : 200
	 */

	private int resultCode;
	/**
	 * content : could u stop doing that stupid programming, u r such a liar
	 * create_time : 1441728000000 type : 硬件外观 user_image :
	 * [{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"
	 * },{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"}] response :
	 * {"content":"thank you for your response","create_time":1441728000000,
	 * "feed_image"
	 * :[{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"}]}
	 */

	private List<ListQuestionEntity> list_question;

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public void setList_question(List<ListQuestionEntity> list_question) {
		this.list_question = list_question;
	}

	public int getResultCode() {
		return resultCode;
	}

	public List<ListQuestionEntity> getList_question() {
		return list_question;
	}

	public static class ListQuestionEntity implements Serializable {
		private String content;
		private long create_time;
		private String type;
		/**
		 * content : thank you for your response create_time : 1441728000000
		 * feed_image :
		 * [{"url":"http://push.bbpaw.com.cn/upload/chat_images/xx/xx"}]
		 */

		private ResponseEntity response;
		/**
		 * url : http://push.bbpaw.com.cn/upload/chat_images/xx/xx
		 */

		private List<UserImageEntity> user_image;
		private List<String> my_url;
		private String commit_time;
		private boolean isReplied;
		private boolean isSolved;
		private String question_ID;

		public ListQuestionEntity(String content2, List<String> myUrl,
				String commit_time, boolean isReplied, boolean isSolved,
				String question_ID, ResponseEntity responseEntity) {
			// TODO Auto-generated constructor stub
			this.content = content2;
			this.response = responseEntity;
			this.my_url = myUrl;
			this.commit_time = commit_time;
			this.isReplied = isReplied;
			this.isSolved = isSolved;
			this.question_ID=question_ID;
			//Log.e("实体类imageUrl的长度", "------------------------" + myUrl.size());
		}
		

		public String getQuestion_ID() {
			return question_ID;
		}


		public void setQuestion_ID(String question_ID) {
			this.question_ID = question_ID;
		}


		public boolean getIsReplied() {
			return isReplied;
		}

	
		public boolean getIsSolved() {
			return isSolved;
		}

		

		public String getCommit_time() {
			return commit_time;
		}

		public void setCommit_time(String commit_time) {
			this.commit_time = commit_time;
		}

		public List<String> getMy_url() {
			return my_url;
		}

		public void setMy_url(List<String> my_url) {
			this.my_url = my_url;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public void setCreate_time(long create_time) {
			this.create_time = create_time;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setResponse(ResponseEntity response) {
			this.response = response;
		}

		public void setUser_image(List<UserImageEntity> user_image) {
			this.user_image = user_image;
		}

		public String getContent() {
			return content;
		}

		public long getCreate_time() {
			return create_time;
		}

		public String getType() {
			return type;
		}

		public ResponseEntity getResponse() {
			return response;
		}

		public List<UserImageEntity> getUser_image() {
			return user_image;
		}

		public static class ResponseEntity implements Serializable {
			private String content;
			private long create_time;
			/**
			 * url : http://push.bbpaw.com.cn/upload/chat_images/xx/xx
			 */

			private List<FeedImageEntity> feed_image;
			private List<String> BBpaurl;
			private String response_time;

			public ResponseEntity(String reposeContent, List<String> bBpawUrl,
					String response_time) {
				// TODO Auto-generated constructor stub
				this.content = reposeContent;
				this.BBpaurl = bBpawUrl;
				this.response_time = response_time;
			}

			public String getResponse_time() {
				return response_time;
			}

			public void setResponse_time(String response_time) {
				this.response_time = response_time;
			}

			public List<String> getBBpaurl() {
				return BBpaurl;
			}

			public void setBBpaurl(List<String> bBpaurl) {
				BBpaurl = bBpaurl;
			}

			public void setContent(String content) {
				this.content = content;
			}

			public void setCreate_time(long create_time) {
				this.create_time = create_time;
			}

			public void setFeed_image(List<FeedImageEntity> feed_image) {
				this.feed_image = feed_image;
			}

			public String getContent() {
				return content;
			}

			public long getCreate_time() {
				return create_time;
			}

			public List<FeedImageEntity> getFeed_image() {
				return feed_image;
			}

			public static class FeedImageEntity {
				private String url;

				public void setUrl(String url) {
					this.url = url;
				}

				public String getUrl() {
					return url;
				}
			}
		}

		public static class UserImageEntity {
			private String url;

			public void setUrl(String url) {
				this.url = url;
			}

			public String getUrl() {
				return url;
			}
		}
	}
}