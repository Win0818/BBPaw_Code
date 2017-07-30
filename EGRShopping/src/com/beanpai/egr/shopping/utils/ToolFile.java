package com.beanpai.egr.shopping.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * �ļ�������
 * 
 * @author WGQ
 * @version 1.0
 */
public class ToolFile {

	private static final String TAG = ToolFile.class.getSimpleName();

	/**
	 * ����Ƿ��ѹ���SD�������Ƿ����SD����
	 * 
	 * @return
	 */
	public static boolean isMountedSDCard() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else {
			Log.w(TAG, "SDCARD is not MOUNTED !");
			return false;
		}
	}

	/**
	 * ��ȡSD��ʣ����������λByte��
	 * 
	 * @return
	 */
	public static long gainSDFreeSize() {
		if (isMountedSDCard()) {
			// ȡ��SD���ļ�·��
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// ��ȡ�������ݿ�Ĵ�С(Byte)
			long blockSize = sf.getBlockSize();
			// ���е����ݿ������
			long freeBlocks = sf.getAvailableBlocks();

			// ����SD�����д�С
			return freeBlocks * blockSize; // ��λByte
		} else {
			return 0;
		}
	}

	/**
	 * ��ȡSD������������λByte��
	 * 
	 * @return
	 */
	public static long gainSDAllSize() {
		if (isMountedSDCard()) {
			// ȡ��SD���ļ�·��
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// ��ȡ�������ݿ�Ĵ�С(Byte)
			long blockSize = sf.getBlockSize();
			// ��ȡ�������ݿ���
			long allBlocks = sf.getBlockCount();
			// ����SD����С��Byte��
			return allBlocks * blockSize;
		} else {
			return 0;
		}
	}

	/**
	 * ��ȡ���õ�SD��·������SD����û�й����򷵻�""��
	 * 
	 * @return
	 */
	public static String gainSDCardPath() {
		if (isMountedSDCard()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			if (!sdcardDir.canWrite()) {
				Log.w(TAG, "SDCARD can not write !");
			}
			return sdcardDir.getPath();
		}
		return "";
	}

	/**
	 * ����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У������ڶ������еĸ�ʽ���ļ�
	 * @param filePath �ļ�·��
	 */
	public static String readFileByLines(String filePath) throws IOException
	{
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),System.getProperty("file.encoding")));
			String tempString = null;
			while ((tempString = reader.readLine()) != null)
			{
				sb.append(tempString);
				sb.append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (reader != null){reader.close();}
		}

		return sb.toString();

	}
	
	/**
	 * ����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У������ڶ������еĸ�ʽ���ļ�
	 * @param filePath �ļ�·��
	 * @param encoding д�ļ�����
	 */
	public static String readFileByLines(String filePath,String encoding) throws IOException
	{
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),encoding));
			String tempString = null;
			while ((tempString = reader.readLine()) != null)
			{
				sb.append(tempString);
				sb.append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (reader != null){reader.close();}
		}

		return sb.toString();
	}
	
	
	/**
	 * ��������
	 * @param filePath �ļ�·��
	 * @param content ���������
	 * @throws IOException
	 */
	public static void saveToFile(String filePath,String content) throws IOException
	{
		saveToFile(filePath,content,System.getProperty("file.encoding"));
	}

	/**
	 * ָ�����뱣������
	 * @param filePath �ļ�·��
	 * @param content ���������
	 * @param encoding д�ļ�����
	 * @throws IOException
	 */
	public static void saveToFile(String filePath,String content,String encoding) throws IOException
	{
		BufferedWriter writer = null;
		File file = new File(filePath);
		try
		{
			if(!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), encoding));
			writer.write(content);

		} finally
		{
			if (writer != null){writer.close();}
		}
	}
	
	/**
	 * ׷���ı�
	 * @param content ��Ҫ׷�ӵ�����
	 * @param file ��׷���ļ�Դ
	 * @throws IOException
	 */
	public static void appendToFile(String content, File file) throws IOException
	{
		appendToFile(content, file, System.getProperty("file.encoding"));
	}

	/**
	 * ׷���ı�
	 * @param content ��Ҫ׷�ӵ�����
	 * @param file ��׷���ļ�Դ
	 * @param encoding �ļ�����
	 * @throws IOException
	 */
	public static void appendToFile(String content, File file, String encoding) throws IOException
	{
		BufferedWriter writer = null;
		try
		{
			if(!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
			writer.write(content);
		} finally
		{
			if (writer != null){writer.close();}
		}
	}
	
	/**
	 * �ж��ļ��Ƿ����
	 * @param filePath �ļ�·��
	 * @return �Ƿ����
	 * @throws Exception
	 */
	public static Boolean isExsit(String filePath)
	{
		Boolean flag = false ;
		try
		{
			File file = new File(filePath);
			if(file.exists())
			{
				flag = true;
			}
		}catch(Exception e){
			System.out.println("�ж��ļ�ʧ��-->"+e.getMessage()); 
		} 
		
		return flag;
	}
	
	/**
	 * ���ٶ�ȡ����Ӧ�ð��µ��ļ�����
	 * 
	 * @param context
	 *            ������
	 * @param filename
	 *            �ļ�����
	 * @return �ļ�����
	 * @throws IOException
	 */
	public static String read(Context context, String filename) throws IOException {
		FileInputStream inStream = context.openFileInput(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/**
	 * ��ȡָ��Ŀ¼�ļ����ļ�����
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @return �ļ�����
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static String read(String fileName) throws IOException {
		FileInputStream inStream = new FileInputStream(fileName);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/***
	 * ����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У������ڶ������еĸ�ʽ���ļ�
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @param encoding
	 *            �ļ�����
	 * @return �ַ�������
	 * @throws IOException
	 */
	public static String read(String fileName, String encoding)
			throws IOException {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return sb.toString();
	}

	/**
	 * ��ȡrawĿ¼���ļ�����
	 * 
	 * @param context
	 *            ����������
	 * @param rawFileId
	 *            raw�ļ���id
	 * @return
	 */
	public static String readRawValue(Context context, int rawFileId) {
		String result = "";
		try {
			InputStream is = context.getResources().openRawResource(rawFileId);
			int len = is.available();
			byte[] buffer = new byte[len];
			is.read(buffer);
			result = new String(buffer, "UTF-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ��ȡassetsĿ¼���ļ�����
	 * 
	 * @param context
	 *            ����������
	 * @param fileName
	 *            �ļ����ƣ�������չ��
	 * @return
	 */
	public static String readAssetsValue(Context context, String fileName) {
		String result = "";
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
			int len = is.available();
			byte[] buffer = new byte[len];
			is.read(buffer);
			result = new String(buffer, "UTF-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * ��ȡassetsĿ¼���ļ�����
	 * 
	 * @param context
	 *            ����������
	 * @param fileName
	 *            �ļ����ƣ�������չ��
	 * @return
	 */
	public static List<String> readAssetsListValue(Context context, String fileName) {
		List<String> list = new ArrayList<String>();
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * ��ȡSharedPreferences�ļ�����
	 * 
	 * @param context
	 *            ������
	 * @param fileNameNoExt
	 *            �ļ����ƣ����ô���׺����
	 * @return
	 */
	public static Map<String, ?> readShrePerface(Context context,String fileNameNoExt) {
		SharedPreferences preferences = context.getSharedPreferences(
				fileNameNoExt, Context.MODE_PRIVATE);
		return preferences.getAll();
	}

	/**
	 * д��SharedPreferences�ļ�����
	 * 
	 * @param context
	 *            ������
	 * @param fileNameNoExt
	 *            �ļ����ƣ����ô���׺����
	 * @param values
	 *            ��Ҫд�������Map(String,Boolean,Float,Long,Integer)
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void writeShrePerface(Context context, String fileNameNoExt,Map<String, ?> values) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) 
			{
				Map.Entry<String, ?> entry = (Map.Entry<String, ?>) iterator.next();
				if (entry.getValue() instanceof String) {
					editor.putString(entry.getKey(), (String) entry.getValue());
				} else if (entry.getValue() instanceof Boolean) {
					editor.putBoolean(entry.getKey(),(Boolean) entry.getValue());
				} else if (entry.getValue() instanceof Float) {
					editor.putFloat(entry.getKey(), (Float) entry.getValue());
				} else if (entry.getValue() instanceof Long) {
					editor.putLong(entry.getKey(), (Long) entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					editor.putInt(entry.getKey(),(Integer) entry.getValue());
				}
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * д��Ӧ�ó����filesĿ¼���ļ�
	 * 
	 * @param context
	 *            ������
	 * @param fileName
	 *            �ļ�����
	 * @param content
	 *            �ļ�����
	 */
	public static void write(Context context, String fileName, String content) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			outStream.write(content.getBytes());
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * д��Ӧ�ó����filesĿ¼���ļ�
	 * 
	 * @param context
	 *            ������
	 * @param fileName
	 *            �ļ�����
	 * @param content
	 *            �ļ�����
	 */
	public static void write(Context context, String fileName, byte[] content) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			outStream.write(content);
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * д��Ӧ�ó����filesĿ¼���ļ�
	 * 
	 * @param context
	 *            ������
	 * @param fileName
	 *            �ļ�����
	 * @param modeType
	 *            �ļ�д��ģʽ��Context.MODE_PRIVATE��Context.MODE_APPEND��Context.
	 *            MODE_WORLD_READABLE��Context.MODE_WORLD_WRITEABLE��
	 * @param content
	 *            �ļ�����
	 */
	public static void write(Context context, String fileName, byte[] content,
			int modeType) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName,
					modeType);
			outStream.write(content);
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ָ�����뽫����д��Ŀ���ļ�
	 * 
	 * @param target
	 *            Ŀ���ļ�
	 * @param content
	 *            �ļ�����
	 * @param encoding
	 *            д���ļ�����
	 * @throws Exception
	 */
	public static void write(File target, String content, String encoding)
			throws IOException {
		BufferedWriter writer = null;
		try {
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(target, false), encoding));
			writer.write(content);

		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * ָ��Ŀ¼д���ļ�����
	 * @param filePath �ļ�·��+�ļ���
	 * @param content �ļ�����
	 * @throws IOException
	 */
	public static void write(String filePath, byte[] content)
			throws IOException {
		FileOutputStream fos = null;

		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			fos.write(content);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * д���ļ�
	 * 
	 * @param inputStream�����ļ����ֽ�������
	 * @param filePath�ļ��Ĵ��·��(���ļ�����)
	 * @throws IOException 
	 */
	public static File write(InputStream inputStream, String filePath) throws IOException {
		OutputStream outputStream = null;
		// ��ָ��Ŀ¼����һ�����ļ�����ȡ�ļ�����
		File mFile = new File(filePath);
		if (!mFile.getParentFile().exists())
			mFile.getParentFile().mkdirs();
		try {
			outputStream = new FileOutputStream(mFile);
			byte buffer[] = new byte[4 * 1024];
			int lenght = 0 ;
			while ((lenght = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer,0,lenght);
			}
			outputStream.flush();
			return mFile;
		} catch (IOException e) {
			Log.e(TAG, "д���ļ�ʧ�ܣ�ԭ��"+e.getMessage());
			throw e;
		}finally{
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * ָ��Ŀ¼д���ļ�����
	 * @param filePath �ļ�·��+�ļ���
	 * @param content �ļ�����
	 * @throws IOException
	 */
	public static void saveAsJPEG(Bitmap bitmap,String filePath)
			throws IOException {
		FileOutputStream fos = null;

		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * ָ��Ŀ¼д���ļ�����
	 * @param filePath �ļ�·��+�ļ���
	 * @param content �ļ�����
	 * @throws IOException
	 */
	public static void saveAsPNG(Bitmap bitmap,String filePath)
			throws IOException {
		FileOutputStream fos = null;

		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * ���л�����
	 * @param rsls ��Ҫ���л��Ķ���
	 * @param filename �ļ���
	 */
	public static synchronized <T> void serializeObject(T rsls, String filename) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(rsls);
			byte[] data = baos.toByteArray();
			OutputStream os = new FileOutputStream(new File(filename));
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����л�����
	 * @param filename �ļ���
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> T deserializeObject(String filename) {
		T obj = null;
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (fis.available() > 0) {
				obj = (T) ois.readObject();
			}
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}
