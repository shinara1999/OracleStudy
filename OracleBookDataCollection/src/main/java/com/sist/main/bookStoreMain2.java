package com.sist.main;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sist.dao.BookVO;
import com.sist.dao.*;

public class bookStoreMain2 {

   public static void main(String[] args) {
    
	   int tMill = 600000;
      
	try
      {  
    	 BookDAO dao=new BookDAO();
         ArrayList<BookMainVO> vo=dao.isertISBN();
         
         for(BookMainVO dd:vo)
         {
        	 
        	 Document doc=Jsoup.connect("https://www.yes24.com/product/search?query="+dd.getIsbn()).timeout(tMill).get();
        	 Elements Link=doc.select("div.info_name");
            
               for(int i=0;i<Link.size();i++)
               {
            	  String url=""; 
            	  try {
                  Element span=Link.get(i).selectFirst("span.gd_res");
                  if((!span.text().equals("[도서]"))&&(!span.text().equals("[외서]")))
                     continue;
                  String link=Link.get(i).selectFirst("a.gd_name").attr("href");     
                  url=link.substring(link.indexOf("Goods/")+6);
            	  }catch(Exception e) {e.printStackTrace(); continue;}
            	  
           
                  String fixedPrice_withoutTags="";
                  String salePrice_withoutTags="";
                  String deliveryTx="";
                  String bookInfo_decodedText="";
                  String con_decodedText="";
                  String score_withoutTags="";
                  String authorInfo_decodedText="";
                  double score_d=0;
                  
                  Document doc2=Jsoup.connect("https://www.yes24.com/Product/Goods/"+url).get();
                  
                  // 정가
                  try {
                  Element fixedPrice=doc2.selectFirst("div.gd_infoTb em.yes_m");
                  String fixedPriceHtml=fixedPrice.html();
                  fixedPrice_withoutTags=fixedPriceHtml.replaceAll("<em class=\"yes_m\">|</em>", "");
                  System.out.println(fixedPrice_withoutTags);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 판매가
                  try {
                  Element salePrice=doc2.selectFirst("div.gd_infoTb span.nor_price");
                  String salePriceHtml=salePrice.html();
                  salePrice_withoutTags=salePriceHtml.replaceAll("<span class=\"nor_price\">|</em>|</span>|<em class=\"yes_m\">", "");
                  System.out.println(salePrice_withoutTags);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 배송비:무료
                  try {
                  Element delivery=doc2.selectFirst("li.deli_des");
                  // li 태그 내의 텍스트 가져오기
                  deliveryTx = delivery.text();
                  // "배송비 안내" 부분 삭제
                  deliveryTx=deliveryTx.replace("배송비 안내", "").trim();   
                  System.out.println(deliveryTx);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 별점
                  
                  try {
                  Element score=doc2.selectFirst("span.gd_rating em.yes_b");
                  String scoreHtml=score.html();
                  score_withoutTags=scoreHtml.replaceAll("", "");
                  score_d = Double.parseDouble(score_withoutTags);
                  System.out.println(score_d);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 책소개
                  try {
                  Element bookInfo=doc2.selectFirst("div#infoset_introduce div.infoWrap_txtInner textarea.txtContentText");
                  String bookInfoHtml = bookInfo.html();
                  String bookInfo_withoutTags = bookInfoHtml.replaceAll("(?i)<br\\s*/?>|<b>|</b>", "\n");   
                  bookInfo_decodedText = Jsoup.parse(bookInfo_withoutTags).text();
                  bookInfo_decodedText = bookInfo_decodedText.replaceAll("(?i)<br\\s*/?>|<b>|</b>", " ");   
                  System.out.println(bookInfo_decodedText);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 목차
                  try {
                  Element contents=doc2.selectFirst("div#infoset_toc div.infoWrap_txt textarea.txtContentText");
                  // Element에서 HTML 내용 추출
                  String contentsHtml = contents.html();
                  // <br>, </br>, </b> 등의 태그를 줄 바꿈 문자(\n)로 대체
                  String con_withoutTags = contentsHtml.replaceAll("(?i)<br\\s*/?>|<b>|</b>", "\n");
                  // HTML 이스케이프된 문자열을 다시 복원
                  con_decodedText = Jsoup.parse(con_withoutTags).text();
                  con_decodedText = con_decodedText.replaceAll("(?i)<br\\s*/?>|<b>|</b>", " ");   
                  System.out.println(con_decodedText);
                  }catch(Exception ex) {ex.printStackTrace();}
                  
                  // 저자소개
                  try {
                  Element authorInfo2=doc2.selectFirst("div#infoset_authorGrp span.info_origin");
                  String authorInfoHtml = authorInfo2.html();
                  String authorInfo_withoutTags = authorInfoHtml.replaceAll("(?i)<br\\s*/?>|<b>|</b>", "\n");   
                  authorInfo_decodedText = Jsoup.parse(authorInfo_withoutTags).text();
                  authorInfo_decodedText = authorInfo_decodedText.replaceAll("(?i)<br\\s*/?>|<b>|</b>", " ");   
                  System.out.println(authorInfo_decodedText);
                  }catch(Exception ex) {ex.printStackTrace();}
                     
                  		
                    	BookVO vo2=new BookVO();
                    	vo2.setISBN(dd.getIsbn());
                    	vo2.setFixedPrice(fixedPrice_withoutTags);
                    	vo2.setSalePrice(salePrice_withoutTags);
                    	vo2.setDeliveryDate(deliveryTx);
                    	vo2.setScore(score_d);
                    	vo2.setBookInfo(bookInfo_decodedText);
                    	vo2.setContents(con_decodedText);
                    	vo2.setAuthorInfo(authorInfo_decodedText);
                    	
                    	dao.bookInsert(vo2);
                    	System.out.println("save end");
               	
               }     
            }
      }catch(Exception ex) {ex.printStackTrace();}
   }
}