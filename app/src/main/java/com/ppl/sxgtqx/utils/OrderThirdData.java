package com.ppl.sxgtqx.utils;

import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class OrderThirdData {
	public static List<ConnType>orderThirdData(List<ConnType>inData){
		for(int i=0;i<inData.size()-1;i++){
			for(int j=i+1;j<inData.size();j++){
				boolean isChange = isChangeData(inData.get(i),inData.get(j));
				if(isChange){
					ConnType tmp = inData.get(i);
					inData.add(i, inData.get(j));
					inData.remove(i+1);
					inData.add(j, tmp);
					inData.remove(j+1);
				}
			}
		}
		return inData;
	}

	private static boolean isChangeData(ConnType levelThird,
			ConnType levelThird2) {
		// TODO Auto-generated method stub
		char pre[] = levelThird.getConn().toCharArray(); 
		char after[] = levelThird2.getConn().toCharArray();  
		int preType = 0;
		int afterType = 0;

		if(Character.isUpperCase(pre[0])){  
			preType = 2;
			System.out.println("第"+"个字符是大写字母");  
		}else if(Character.isLowerCase(pre[0])){  
			preType = 1;
			System.out.println("第"+"个字符是小写字母");  
		}else if(Character.isDigit(pre[0])){  
			preType = 0;
			System.out.println("第"+"个字符是数字");  
		}else{  
			preType = 3;
			System.out.println("第"+"个字符是中文");  
		} 
		
		if(Character.isUpperCase(after[0])){  
			afterType = 2;
			System.out.println("第"+"个字符是大写字母");  
		}else if(Character.isLowerCase(after[0])){  
			afterType = 1;
			System.out.println("第"+"个字符是小写字母");  
		}else if(Character.isDigit(after[0])){  
			afterType = 0;
			System.out.println("第"+"个字符是数字");  
		}else {  
			afterType = 3;
			System.out.println("第"+"个字符是中文");  
		} 
		if(preType > afterType){
			return false;
		}else if(preType < afterType){
			return true;
		}else{
			if(preType == 3){
				//拼音
				if(sort(levelThird.getConn(),levelThird2.getConn()) > 0){
					return true;
				}
			}else if(preType == 0){
				//数字
				if(perSta(levelThird.getConn(),levelThird2.getConn()) > 0){
					return true;
				}
			}else{
				//字母 不区分 大小写
				if(perSta(levelThird.getConn().toLowerCase(),levelThird2.getConn().toLowerCase()) > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	private static int perSta(String pre,String after){
		int perSta = 0;
		for(int i=0;i<(pre.length() < after.length() ? (pre.length()):(after.length()));i++){
			if(pre.toCharArray()[i] != after.toCharArray()[i]){
				if(pre.toCharArray()[i] > after.toCharArray()[i]){
					return 1;
				}else{
					return -1;
				}
			}
		}
		return perSta;
	}

	private static int sort(String lhs, String rhs) {
		return getPingYin(lhs).compareTo(getPingYin(rhs));
	}
	
	private static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for (char curchar : input) {
                if (Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else
                    output += Character.toString(curchar);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }


}
