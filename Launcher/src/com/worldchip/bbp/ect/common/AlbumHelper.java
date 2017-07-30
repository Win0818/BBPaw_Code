package com.worldchip.bbp.ect.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.worldchip.bbp.ect.entity.ImageBucket;
import com.worldchip.bbp.ect.entity.ImageItem;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

/**
 * ר��������
 * @author WGQ
 */
public class AlbumHelper {
	
	Context context;
	ContentResolver cr;

	// ����ͼ�б�
	HashMap<String, String> thumbnailList = new HashMap<String, String>();
	// ר���б�
	List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
	HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

	private static AlbumHelper instance;

	private AlbumHelper() {}

	public static AlbumHelper getHelper() 
	{
		if (instance == null) 
		{
			instance = new AlbumHelper();
		}
		return instance;
	}

	/**
	 * ��ʼ��
	 * 
	 * @param context
	 */
	public void init(Context context)
	{
		if (this.context == null)
		{
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	/**
	 * �õ�����ͼ
	 */
	private void getThumbnail() 
	{
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,Thumbnails.DATA };
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,null, null, null);
		getThumbnailColumnData(cursor);
	}

	/**
	 * �����ݿ��еõ�����ͼ
	 * @param cur
	 */
	private void getThumbnailColumnData(Cursor cur) 
	{
		if (cur.moveToFirst()) 
		{
			int image_id;
			String image_path;
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	/**
	 * �õ�ԭͼ
	 */
	void getAlbum() 
	{
		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,null, null);
		getAlbumColumnData(cursor);
	}

	/**
	 * �ӱ������ݿ��еõ�ԭͼ
	 * 
	 * @param cur
	 */
	private void getAlbumColumnData(Cursor cur)
	{
		if (cur.moveToFirst()) 
		{
			int _id;
			String album;
			String albumArt;
			String albumKey;
			String artist;
			int numOfSongs;

			int _idColumn = cur.getColumnIndex(Albums._ID);
			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

			do {
				_id = cur.getInt(_idColumn);
				album = cur.getString(albumColumn);
				albumArt = cur.getString(albumArtColumn);
				albumKey = cur.getString(albumKeyColumn);
				artist = cur.getString(artistColumn);
				numOfSongs = cur.getInt(numOfSongsColumn);
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cur.moveToNext());

		}
	}

	/**
	 * �Ƿ񴴽���ͼƬ��
	 */
	boolean hasBuildImagesBucketList = false;

	/**
	 * �õ�ͼƬ��
	 */
	void buildImagesBucketList() 
	{
		if (hasBuildImagesBucketList) {
			if (thumbnailList!= null && !thumbnailList.isEmpty()) {
				thumbnailList.clear();
			}
			
			if (bucketList != null && !bucketList.isEmpty()) {
				bucketList.clear();
			}
		}
		// ��������ͼ����
		getThumbnail();
		// �����������
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID,Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,Media.SIZE, Media.BUCKET_DISPLAY_NAME };
		// �õ�һ���α�
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,null);
		if (cur.moveToFirst()) 
		{
			// ��ȡָ���е�����
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			// ��ȡͼƬ����

			do {
				String _id = cur.getString(photoIDIndex);
				String path = cur.getString(photoPathIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);

				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null)
				{
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.setImageList( new ArrayList<ImageItem>());
					bucket.setBucketName(bucketName);
				}
				
				int cout = bucket.getCount() + 1;
				bucket.setCount(cout);
				ImageItem imageItem = new ImageItem();
				imageItem.setImageId(_id);
				imageItem.setImagePath(path);
				imageItem.setThumbnailPath(thumbnailList.get(_id));
				List<ImageItem> list = bucket.getImageList();
				list.add(imageItem);
				bucket.setImageList(list);

			} while (cur.moveToNext());
		}
		hasBuildImagesBucketList = true;
	}

	/**
	 * �õ�ͼƬ��
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) 
	{
		if (refresh || (!refresh && !hasBuildImagesBucketList))
		{
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

	/**
	 * �õ�ԭʼͼ��·��
	 * 
	 * @param image_id
	 * @return
	 */
	String getOriginalImagePath(String image_id) 
	{
		String path = null;
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,Media._ID + "=" + image_id, null, null);
		if (cursor != null) 
		{
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(Media.DATA));
		}
		return path;
	}
}