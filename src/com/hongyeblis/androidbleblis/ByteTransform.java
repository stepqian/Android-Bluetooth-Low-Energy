package com.hongyeblis.androidbleblis;

import android.annotation.SuppressLint;

import java.util.Arrays;

@SuppressLint("NewApi")
public class ByteTransform {
	
	/**
	 * 16进制字符串转byte
	 * @param strIn
	 * @return
	 * @throws Exception
	 */
	public static byte[] hexStr2ByteArr(String strIn) {
		try {
			byte[] arrB = strIn.getBytes();
			int iLen = arrB.length;
			byte[] arrOut = new byte[iLen/2];
			for (int i = 0; i < iLen; i = i + 2) {
				String strTmp = new String(arrB, i, 2);
				arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			}
			return arrOut;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * byte装成int
	 * @param value
	 * @return
	 */
	public static int bytesToint(byte[] value) {
		int ret = 0;
		for (int i = 0; i < value.length; i++) {
			ret += (value[value.length - i - 1] & 0xFF) << (i * 8);
		}
		return ret;
	}
 
	public static long bytesToLong(byte[] value) {
		long ret = 0;
		for (int i = 0; i < value.length; i++) {
			ret += (long) (value[value.length - i - 1] & 0xFF) << (long) (i * 8);
		}
		return ret;
	}
	/**
	 * 16进制的形式输出
	 * @param b
	 * @return
	 */
	public static String byte2HexStr(byte[] b){    
	    String stmp="";    
	    StringBuilder sb = new StringBuilder("");    
	    
	    try {
			for (int n=0;n<b.length;n++)    
			{    
			    stmp = Integer.toHexString(b[n] & 0xFF);    
			    sb.append((stmp.length()==1)? "0"+stmp : stmp);    
			    //sb.append(" ");    
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	    
	    return sb.toString().toUpperCase().trim();    
	}
    /**
     * 转成字节长度
     * @param iSource 原数据
     * @param iArrayLen 输出数据
     * 只能转4个字节
     * @return
     */
    public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		byte temp;
		int len = bLocalArr.length;
		for (int i = 0; i < len / 2; i++) {
			temp = bLocalArr[i];
			bLocalArr[i] = bLocalArr[len - 1 - i];
			bLocalArr[len - 1 - i] = temp;
		}
		return bLocalArr;
	}
    
    public static byte[] longTobytes(long val) {
		int length = 8;
//		for (int i = 0; i < 8; i++)
//			if (val >= 1 << (i * 8) || val < 0)
//				length++;
//			else
//				break;
//		if (length == 0)
//			length = 1;
		byte[] value = new byte[length];
		for (int i = 0; i < length; i++)
			value[length - i - 1] = (byte) (val >> i * 8);

		return value;
	}
	/**
	 * java 合并两个byte数组 (内容长度数组加内容长度) 
	 * @param byte_1
	 * @param byte_2
	 * @return
	 */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_sum = new byte[byte_1.length+byte_2.length];//总长度
        System.arraycopy(byte_1, 0, byte_sum, 0, byte_1.length); //复制第二个数组到第三个数组
        System.arraycopy(byte_2, 0, byte_sum, byte_1.length, byte_2.length);  //整合数组
        return byte_sum;  
    }
    /**
     * 合并四个数组
     * @param byte_id
     * @param byte_1
     * @param byte_2
     * @param byte_CRC 
     * @return
     */
    public static byte[] byteMerger1(byte[] byte_id,byte[] byte_1, byte[] byte_2, byte[] byte_CRC){
        byte[] byte_sum = new byte[byte_id.length+byte_1.length+byte_2.length+byte_CRC.length];//总长度
        
        System.arraycopy(byte_id, 0, byte_sum, 0, byte_id.length);//复制第一个数组到第三个
        System.arraycopy(byte_1, 0, byte_sum, byte_id.length, byte_1.length); //复制第二个数组到第三个数组
        System.arraycopy(byte_2, 0, byte_sum, byte_id.length+byte_1.length, byte_2.length);  //整合数组
        System.arraycopy(byte_CRC, 0, byte_sum, byte_id.length+byte_1.length+byte_2.length, byte_CRC.length);  //整合数组
          return byte_sum; 
      }
    /**
     * 数组转整形
     * @param bRefArr
     * @return
     */
	public static int toInt(byte[] bRefArr) {
		byte temp;
		int len = bRefArr.length;
		for (int i = 0; i < len / 2; i++) {
			temp = bRefArr[i];
			bRefArr[i] = bRefArr[len - 1 - i];
			bRefArr[len - 1 - i] = temp;
		}
	    int iOutcome = 0;
	    byte bLoop;
	    for (int i = 0; i < bRefArr.length; i++) {
	        bLoop = bRefArr[i];
	        iOutcome += (bLoop & 0xFF) << (8 * i);
	    }
	    return iOutcome;
	}
	/**
	 * 裁剪多余的字节
	 * @return
	 */
	public static byte[] tailoringbyte(byte[] complete_byte,int leng){
			byte[] new_byte = Arrays.copyOfRange(complete_byte, 1, leng);
			System.out.println("最终数据校验结果=="+crc_testing(new_byte)+"原始数据="+byte2HexStr(new_byte));
		return new_byte;
	}
	
	/**
	 * 异或内容
	 * @param context
	 * @return
	 */
	public static byte XORContext(byte[] context){
		try {
			byte xor_result = context[0];
			for (int i = 1; i < context.length; i++) {
				xor_result^= context[i];
			}
			return xor_result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("XORContext eer:"+e.getMessage());
		}
		return -1;
	}


	/**
	 * CRC校验结果
	 * @param need_byte
	 * @return
	 */
	public static boolean crc_testing(byte[] need_byte) {
		int data_len = ByteTransform.toInt(Arrays.copyOfRange(need_byte,(need_byte.length-2), need_byte.length));//crc
		byte[] content_bs = Arrays.copyOfRange(need_byte,2, need_byte.length-2);//内容
		int crc_16 = cd_crc_16(content_bs, content_bs.length);
		if(data_len==crc_16){
			return true;
		}else{
			return false;
			}
	}
	/**
	 * CRC校验
	 * @param pdata
	 * @param data_len
	 * @return
	 */
	public static int cd_crc_16(byte[] pdata,int data_len) 
	{ 
	    short wCRC = (short) 0xcd01; //0xFFFF; 
	    int i = 0; 
	    byte chChar = 0; 
	    for (i = 0; i < data_len; i++) 
	    { 
	        chChar = pdata[i]; 
	        wCRC = (short) (crc16_table[(chChar ^ wCRC) & 15] ^ (wCRC >> 4)); 
	        wCRC = (short) (crc16_table[((chChar >> 4) ^ wCRC) & 15] ^ (wCRC >> 4)); 
	    }
	    return wCRC & 0xFFFF;
	}
	static short crc16_table[] =   		//wCRCTalbeAbs
	{
	    0x0000, (short) 0xCC01, (short) 0xD801, 0x1400, (short) 0xF001, 0x3C00, 0x2800, (short) 0xE401, 
	    (short) 0xA001, 0x6C00, 0x7800, (short) 0xB401, 0x5000, (short) 0x9C01, (short) 0x8801, 0x4400, 
	};
	
	public static void main(String[] args) {
	}
}
