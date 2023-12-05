package com.sist.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.sist.dao.*;

public class BookDAO {
	private Connection conn;
	private PreparedStatement ps;
	private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
	
	public BookDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception ex) {ex.printStackTrace();}
	}
	// 오라클 연동
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL, "hr", "happy");
		}catch(Exception ex) {ex.printStackTrace();}
	}
	// 오라클 닫기
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
		}catch(Exception ex) {}
	}
	
	// isbn 부르기
	public ArrayList<BookMainVO> isertISBN()
	{
		ArrayList<BookMainVO> list=new ArrayList<BookMainVO>();
		try {

			getConnection();
			String sql="SELECT b1.ISBN "
					+"FROM bookmain b1 "
					+"LEFT OUTER JOIN BOOKSTORE b2 "
					+"ON b1.ISBN = b2.ISBN "
					+"WHERE b2.ISBN IS NULL";
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				BookMainVO vo=new BookMainVO();
				vo.setIsbn(rs.getString(1));
				list.add(vo);
			}
			rs.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return list;
	}
	
	// 테이블에 넣기
	public void bookInsert(BookVO vo)
	{
		try
		{
			/*
			 		SELECT b1.ISBN
					FROM bookmain b1
					LEFT OUTER JOIN BOOKSTORE b2
					ON b1.ISBN = b2.ISBN 
					WHERE b2.ISBN IS NULL;
			 */
			getConnection();
			String sql="INSERT INTO BOOKSTORE VALUES("
					+"BOOKSTORE_stno_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
					+"";
			ps=conn.prepareStatement(sql);
			ps.setString(1, vo.getISBN());
			ps.setString(2, vo.getFixedPrice());
			ps.setString(3, vo.getSalePrice());
			ps.setString(4, vo.getDeliveryDate());
			ps.setDouble(5, vo.getScore());
			ps.setString(6, vo.getBookInfo());
			ps.setString(7, vo.getContents());
			ps.setString(8, vo.getAuthorInfo());
			ps.setInt(9, 0);
			
			ps.executeUpdate();
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
	}
	
}
