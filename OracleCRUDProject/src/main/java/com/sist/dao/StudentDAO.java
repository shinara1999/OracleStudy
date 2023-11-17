package com.sist.dao;

import java.util.*;
import java.sql.*;

public class StudentDAO {
	private Connection conn;
	private PreparedStatement ps;
	private final String URl="jdbc:oracle:thin:@localhost:1521:XE";
	
	public StudentDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception ex) {}
	}
	
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URl, "hr", "happy");
		}catch(Exception ex) {}
	}
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
		}catch(Exception ex) {}
	}
	
	// 기능
	// 1. 목록 => SELECT => 페이징 (자바, 오라클=인라인뷰)
	// => 오라클만 연동 => 출력 => main, 브라우저로 전송
	// => 데이터를 모아서 전송 (목록) ArrayList
	public List<StudentVO> stdListData()
	{
		List<StudentVO> list=new ArrayList<StudentVO>();
		try
		{
			// 오라클 연결
			getConnection();
			// sql문장 만들기
			String sql="SELECT hakbun, name, kor, eng, math,"
					+"kor+eng+math, ROUND((kor+eng+math)/3.0, 2),"
					+"RANK() OVER(ORDER BY (kor+eng+math) DESC),"
					+"TO_CHAR(regdate, 'YYYY-MM-DD HH24:MI:SS') "
					+"FROM student";
			// RANK()는 자동정렬
			// => 웹은 모든 데이터가 공유 => 데이터는 반드시 오라클에 저장된 상태
			// VO => {} , List => [] => [{},{},{}...]
			// XML => JSON ==> Ajax
			// 오라클 전송
			ps=conn.prepareStatement(sql);
			// sql문장 실행
			ResultSet rs=ps.executeQuery();
			while(rs.next())
			{
				// 데이터를 모아서 전송 (MainClass)
				StudentVO vo=new StudentVO();
				
				vo.setHakbun(rs.getInt(1));
				vo.setName(rs.getString(2));
				vo.setKor(rs.getInt(3));
				vo.setEng(rs.getInt(4));
				vo.setMath(rs.getInt(5));
				vo.setTotal(rs.getInt(6));
				vo.setAvg(rs.getDouble(7));
				vo.setRank(rs.getInt(8));
				vo.setDbday(rs.getString(9));
				
				list.add(vo);
			}
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return list;
	}
	
	// 2. 추가 => INSERT
	public void stdInsert(StudentVO vo)
	{
		try
		{
			getConnection();
//			String sql="INSERT INYO student(hakbun, name, kor, eng, math) "
//					+"VALUES((SELECT MAX(hakbun)+1 FROM student),'"
//					+vo.getName()+"',"+vo.getKor()+","+vo.getEng()
//					+","+vo.getMath()+")";
//			System.out.println(sql);
			String sql="INSERT INTO student(hakbun, name, kor, eng, math) "
					+"VALUES((SELECT MAX(hakbun)+1 FROM student),?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setString(1, vo.getName());
			ps.setInt(2, vo.getKor());
			ps.setInt(3, vo.getEng());
			ps.setInt(4, vo.getMath());
			
			// 데이터형 처리
			// setString ==> ''
			// 실행
			ps.executeUpdate(); // 데이터베이스가 변동 =>
			// execute / executeUpdate
			// 			-------------- autoCommit이기 때문에 따로 commit을 날리지 않아도 된다.
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
	}
	
	// 3. 상세보기 => SELECT
	public StudentVO stdDetailData(int hakbun)
	{
		StudentVO vo=new StudentVO();
		try
		{
			getConnection();
			String sql="SELECT hakbun, name, kor, eng, math,"
					+"kor+eng+math, ROUND((kor+eng+math)/3.0, 2),"
					+"RANK() OVER(ORDER BY (kor+eng+math) DESC),"
					+"TO_CHAR(regdate, 'YYYY-MM-DD HH24:MI:SS') "
					+"FROM student "
					+"WHERE hakbun="+hakbun;
			
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			
			rs.next();
			
			vo.setHakbun(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setKor(rs.getInt(3));
			vo.setEng(rs.getInt(4));
			vo.setMath(rs.getInt(5));
			vo.setTotal(rs.getInt(6));
			vo.setAvg(rs.getDouble(7));
			vo.setRank(rs.getInt(8));
			vo.setDbday(rs.getString(9));
			
			rs.close();
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return vo;
	}
	
	// 4. 수정 => UPDATE
	public StudentVO stdUpdateData(int hakbun)
	{
		StudentVO vo=new StudentVO();
		try
		{
			getConnection();
			String sql="SELECT hakbun, name, kor, eng, math "
					+"FROM student "
					+"WHERE hakbun="+hakbun;
			// 대댓글 / 답변형
			ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			rs.next();
			vo.setHakbun(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setKor(rs.getInt(3));
			vo.setEng(rs.getInt(4));
			vo.setMath(rs.getInt(5));
			
			rs.close();
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return vo;
	}
	public void stdUpdate(StudentVO vo)
	{
		try
		{
			getConnection();
			String sql="UPDATE student SET "
					+"name=?, kor=?, eng=?, math=? "
					+"WHERE hakbun=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, vo.getName());
			ps.setInt(2, vo.getKor());
			ps.setInt(3, vo.getEng());
			ps.setInt(4, vo.getMath());
			ps.setInt(5, vo.getHakbun());
			
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
	// 5. 삭제 => DELETE
	public void stdDelete(int hakbun)
	{
		try
		{
			getConnection();
			String sql="DELETE FROM student "
					+"WHERE hakbun="+hakbun;
			ps=conn.prepareStatement(sql);
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
//	public static void main(String[] args) {
//		StudentDAO dao=new StudentDAO();
//		StudentVO vo=new StudentVO();
//		vo.setHakbun(8);
//		vo.setName("박병찬");
//		vo.setKor(78);
//		vo.setEng(67);
//		vo.setMath(89);
//		dao.stdInsertData(vo);
//	}
}




















