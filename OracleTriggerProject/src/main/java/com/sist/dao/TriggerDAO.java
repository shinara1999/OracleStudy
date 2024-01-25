package com.sist.dao;

import java.util.*;
import java.security.DomainCombiner;
import java.sql.*;

public class TriggerDAO {
	private Connection conn;
	private PreparedStatement ps;
	private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
	
	public TriggerDAO()
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
	// 기능 설정
	public void inputInsert(int no, int account, int price)
	{
		try
		{
			getConnection();
			String sql="INSER INTO 입고 VALUES(?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, no);
			ps.setInt(2, account);
			ps.setInt(3, price);
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
	public void outputInsert(int no, int account, int price)
	{
		try
		{
			getConnection();
			String sql="INSER INTO 출고 VALUES(?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, no);
			ps.setInt(2, account);
			ps.setInt(3, price);
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
	public void remainData()
	{
		try
		{
			getConnection();
			String sql="SELECT * FROM 재고";
			ps=conn.prepareStatement(sql);
			// 결과값 받기
			ResultSet rs=ps.executeQuery();
			System.out.println("품번 수량 누적금액");
			while(rs.next())
			{
				System.out.println(rs.getInt(1)+" "
									+rs.getInt(2)+" "
									+rs.getInt(3));
			}
			rs.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}	
	}
	public static void main(String[] args) {
		TriggerDAO dao=new TriggerDAO();
		dao.inputInsert(100, 3, 1500);
		dao.remainData();
	}
}















