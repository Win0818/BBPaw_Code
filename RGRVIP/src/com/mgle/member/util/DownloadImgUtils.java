package com.mgle.member.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mgle.member.util.ImageSizeUtil.ImageSize;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImgUtils
{

	/**
	 * 根据URL下载图片在指定的文件
	 * @param urlStr
	 * @param file
	 * @return
	 */
	public static boolean downloadImgByUrl(String urlStr, File file)
	{
		if (TextUtils.isEmpty(urlStr)) {
			return false;
		}
		FileOutputStream fos = null;
		InputStream is = null;
		try
		{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			is = conn.getInputStream();
			fos = new FileOutputStream(file);
			byte[] buf = new byte[512];
			int len = 0;
			while ((len = is.read(buf)) != -1)
			{
				fos.write(buf, 0, len);
			}
			fos.flush();
			return true;

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}

			try
			{
				if (fos != null)
					fos.close();
			} catch (IOException e)
			{
			}
		}

		return false;

	}
	
	//������Ϊ��̬���������ڵ���
	 public static byte[] readImage(String path) throws Exception{
		  URL url = new URL(path);
		// ��סʹ�õ���HttpURLConnection��
		  HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		  conn.setRequestMethod("GET");
		  //������г���5����Զ�ʧЧ ����android�涨
		  conn.setConnectTimeout(5*1000);
		  InputStream inStream = conn.getInputStream();  
		  //����readStream����
		  return readStream(inStream);
	 }

	 public static byte[] readStream(InputStream inStream) throws Exception{
		   //����ݶ�ȡ��ŵ��ڴ���ȥ
		  ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		  byte[] buffer = new byte[1024];
		  int len = -1;
		  while( (len=inStream.read(buffer)) != -1){
		      outSteam.write(buffer, 0, len);
		  }
		  outSteam.close();
		  inStream.close();
		  return outSteam.toByteArray();
	 }
	 
	public static String downloadImg(String urlStr, String fileName){
		try
		{
			byte[] data = readImage(urlStr);
			String path = Utils.getCachePath()+File.separator+fileName;
			Log.e("InputStream", "..path="+path+"; data.size="+data.length);
			FileOutputStream outStream = new FileOutputStream(new File(path));
			outStream.write(data);
			outStream.close();
		
			return path;
		}catch(Exception err){
			err.printStackTrace();
		}
		
		return "";
	}
	
	public static InputStream downloadImgByUrl(String urlStr)
	{
		FileOutputStream fos = null;
		InputStream is = null;
		try
		{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			Log.e("InputStream", "downloadImgByUrl..lenth="+conn.getContentLength());
			is = conn.getInputStream();
			//is.mark(is.available());
			
			//conn.disconnect();
			return is;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}

			try
			{
				if (fos != null)
					fos.close();
			} catch (IOException e)
			{
			}
		}

		return null;

	}

	/**
	 * 根据url下载图片在指定的文件
	 * 
	 * @param urlStr
	 * @param file
	 * @return
	 */
	public static Bitmap downloadImgByUrl(String urlStr, ImageView imageview)
	{
		FileOutputStream fos = null;
		InputStream is = null;
		try
		{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			is = new BufferedInputStream(conn.getInputStream());
			is.mark(is.available());
			
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
			
			ImageSize imageViewSize = ImageSizeUtil.getImageViewSize(imageview);
			opts.inSampleSize = ImageSizeUtil.caculateInSampleSize(opts,
					imageViewSize.width, imageViewSize.height);
			
			opts.inJustDecodeBounds = false;
			is.reset();
			bitmap = BitmapFactory.decodeStream(is, null, opts);

			conn.disconnect();
			return bitmap;

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}

			try
			{
				if (fos != null)
					fos.close();
			} catch (IOException e)
			{
			}
		}

		return null;

	}

}
