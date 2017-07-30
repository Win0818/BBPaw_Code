package com.egreat.devicemanger;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**  
 * 使用3DES加密与解密,可对byte[],String类型进行加密与解密 密文可使用String,byte[]存储.  
 *  
 */ 

public class EncryptUtil {
private String Algorithm = ""; //定义 加密算法,可用
	
	
	private EncryptUtil(String algorithm) {
		this.Algorithm = algorithm;
	}
	
	public static EncryptUtil getDesInstance() {
		return new EncryptUtil("DESede");
	
	}
	
	public static EncryptUtil getAesInstance() {
		return new EncryptUtil("AES");
	}
	
//    private Key key;        //密钥   
  
    /**  
     * 根据参数生成KEY  
     *  
     * @param strKey 密钥字符串  
     */  
    private SecretKeySpec generateKey(String password) {   
        try {   

        	if(Algorithm.equalsIgnoreCase("AES"))
        		return generateAesKey(password);
        	else if("DESede".equalsIgnoreCase(Algorithm))
        		return generateDesKey(password);
        	else
        		return null;
        } catch (Exception e) {   
            e.printStackTrace(); 
            return null;
        }   
    }   

	
	private SecretKeySpec generateAesKey(String password) {   
        try {   

            KeyGenerator kgen = KeyGenerator.getInstance(Algorithm);
            kgen.init(128, new SecureRandom(password.getBytes()));  
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, Algorithm); 
            return key;

        } catch (Exception e) {   
            e.printStackTrace(); 
            return null;
        }   
    }   
    
    public SecretKeySpec generateDesKey(String strKey) {   
        try {   

        	StringBuffer sb = new StringBuffer(strKey);
        	while(sb.length()<24) { // 补齐24位。右边补齐0{
        		sb.append("0");
        	}
        	
        	SecretKeySpec deskey = new SecretKeySpec(sb.toString().getBytes(), Algorithm);
            
        	return deskey;

        } catch (Exception e) {   
            e.printStackTrace();   
            return null;
        }   
    }  
  
    /**  
     * 加密String明文输入,String密文输出  
     *  
     * @param strMing String明文  
     * @return String密文 16进制格式  
     */  
    public String encryptToHexString(String strMing, String password) {   
        byte[] byteMi = null;   
        byte[] byteMing = null;   
        String strMi = "";   
        try {
        	byteMing = strMing.getBytes();
            byteMi = this.getEncCode(byteMing, password);   
            strMi = byteArr2HexStr(byteMi);   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            byteMing = null;   
            byteMi = null;   
        }   
        return strMi;   
    }   
  
    
 
  
    /**  
     * 加密以byte[]明文输入,byte[]密文输出  
     *  
     * @param byteS byte[]明文  
     * @return byte[]密文  
     */  
    private byte[] getEncCode(byte[] byteS, String password) {   
        byte[] byteFina = null;   
        Cipher cipher;   
        try {
        	Key key = generateKey(password);
            cipher = Cipher.getInstance(Algorithm);   
            cipher.init(Cipher.ENCRYPT_MODE, key);   
            byteFina = cipher.doFinal(byteS);   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            cipher = null;   
        }   
        return byteFina;   
    }   
      
       
    //数组转16进制字符串   
    public static String byteArr2HexStr(byte[] arrB) throws Exception {    
        int iLen = arrB.length;   
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍   
        StringBuffer sb = new StringBuffer(iLen * 2);   
        for (int i = 0; i < iLen; i++) {   
            int intTmp = arrB[i];   
            // 把负数转换为正数   
            while (intTmp < 0) {   
                intTmp = intTmp + 256;   
            }   
            // 小于0F的数需要在前面补0   
            if (intTmp < 16) {   
                sb.append("0");   
            }   
            sb.append(Integer.toString(intTmp, 16));   
        }   
        // 最大128位   
        String result = sb.toString();   
//      if(result.length()>128){   
//          result = result.substring(0,result.length()-1);   
//      }   
        return result;   
    }   

}
