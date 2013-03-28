package org.fido;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Vcf {

	
	
	/*
     * 解码
     */
    public static  String qpDecoding(String str)
     {
      if (str == null)
      {
       return "";
      }
      try
      {
       str = str.replaceAll("=\n", "");
       byte[] bytes = str.getBytes("US-ASCII");
       for (int i = 0; i < bytes.length; i++)
       {
        byte b = bytes[i];
        if (b != 95)
        {
         bytes[i] = b;
        }
        else
        {
         bytes[i] = 32;
        }
       }
       if (bytes == null)
       {
        return "";
       }
       ByteArrayOutputStream buffer = new ByteArrayOutputStream();
       for (int i = 0; i < bytes.length; i++)
       {
        int b = bytes[i];
        if (b == '=')
        {
         try
         {
          int u = Character.digit((char) bytes[++i], 16);
          int l = Character.digit((char) bytes[++i], 16);
          if (u == -1 || l == -1)
          {
           continue;
          }
          buffer.write((char) ((u << 4) + l));
         }
         catch (ArrayIndexOutOfBoundsException e)
         {
          e.printStackTrace();
         }
        }
        else
        {
         buffer.write(b);
        }
       }
       return new String(buffer.toByteArray(), "UTF-8");
      }
      catch (Exception e)
      {
       e.printStackTrace();
       return "";
      }
     }
    

    /*
     * 编码
     */
    
     public static String qpEncodeing(String str)
        {
            char[] encode = str.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encode.length; i++)
            {
                if ((encode[i] >= '!') && (encode[i] <= '~') && (encode[i] != '=')
                        && (encode[i] != '\n'))
                {
                    sb.append(encode[i]);
                }
                else if (encode[i] == '=')
                {
                    sb.append("=3D");
                }
                else if (encode[i] == '\n')
                {
                    sb.append("\n");
                }
                else
                {
                    StringBuffer sbother = new StringBuffer();
                    sbother.append(encode[i]);
                    String ss = sbother.toString();
                    byte[] buf = null;
                    try
                    {
                        buf = ss.getBytes("utf-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                    if (buf.length == 3)
                    {
                        for (int j = 0; j < 3; j++)
                        {
                            String s16 = String.valueOf(Integer.toHexString(buf[j]));
                            // 抽取中文字符16进制字节的后两位,也就是=E8等号后面的两位,
                            // 三个代表一个中文字符
                            char c16_6;
                            char c16_7;
                            if (s16.charAt(6) >= 97 && s16.charAt(6) <= 122)
                            {
                                c16_6 = (char) (s16.charAt(6) - 32);
                            }
                            else
                            {
                                c16_6 = s16.charAt(6);
                            }
                            if (s16.charAt(7) >= 97 && s16.charAt(7) <= 122)
                            {
                                c16_7 = (char) (s16.charAt(7) - 32);
                            }
                            else
                            {
                                c16_7 = s16.charAt(7);
                            }
                            sb.append("=" + c16_6 + c16_7);
                        }
                    }
                }
            }
            return sb.toString();
        }

     public static List<Card> parseVcfToCardList(String file){
    	 List<Card> result = new ArrayList<Card>();
    	 BufferedReader bf = null;
    	 try {
			bf = new BufferedReader(new FileReader(file));
		
	    	 Card card = null;
	    	 while(bf.ready()){
	    		 String line = bf.readLine();
	    		 if(line !=null && line.startsWith("BEGIN:VCARD")){
	    			 card = new Card();
	    		 }
	    		 if(line != null && line.startsWith("FN")){
	    			 String name = line.substring(line.lastIndexOf(":")+1);
	    			 card.setName(qpDecoding(name));
	    		 }
	    		 if(line !=null && line.startsWith("TEL")){
	    			 String tel = line.substring(line.lastIndexOf(":")+1);
	    			 if(tel.startsWith("+86")){
	    				 tel = tel.substring(3);
	    			 }
	    			 if(tel.startsWith("106")){
	    				 tel = tel.substring(3);
	    			 }
	    			 card.setTel(tel);
	    			 result.add(card);
	    			 card = null;
	    		 }
	    	 }
    	 } catch (FileNotFoundException e) {
			e.printStackTrace();
    	 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
    		 if(bf != null){
    			 try {
					bf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
    		 }
    	 }
    	 return result;
     }
     
     public static void saveToFile(String out,List<Card> cardList){
    	 BufferedWriter writer = null;
    	 try {
    		 writer = new BufferedWriter(new FileWriter(out));
    		 for(Card card : cardList){
    			 writer.write(card.getName() + ",'" + card.getTel()+"\n");
        	 }
    		 writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			 if(writer != null){
    			 try {
    				 writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
    		 }
		}
     }
     
     
     public static void main(String[] arg){
    	 //String str = "=59=E4=BA=94=E5=A7=A8=E6=89=8B=E6=9C=BA";
    	 //System.out.print(Vcf.qpDecoding(str));
    	 String file = "E:/fidoDoc/friends/tel/00001.vcf";
    	 String file2 = "E:/fidoDoc/friends/tel/00002.vcf";
    	 String fileOut = "E:/fidoDoc/friends/tel/out1.csv";
    	 String fileOut2 = "E:/fidoDoc/friends/tel/out2.csv";
    	 //String a ="15324123597";
    	 List<Card> cardList = parseVcfToCardList(file);
    	 saveToFile(fileOut,cardList);
    	 System.out.println("finished");
    	 
    	 List<Card> cardList2 = parseVcfToCardList(file2);
    	 saveToFile(fileOut2,cardList2);
    	 System.out.println("finished");
     }
}
