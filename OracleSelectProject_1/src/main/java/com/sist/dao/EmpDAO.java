package com.sist.dao;
/*
		 연동하는 부분 : DAO
		 
		 브라우저	===== 자바 ===== 오라클
		 		 요청	 SQL 전송  | SQL문장 실행
		 		 전송 ===== 결과값 받기
		 	| 결과값을 받아서 화면 출력 (HTML)
		 ** 오라클 SQL문장과 자바에서 전송하는 SQL문장이 다른것이 있다.
		 							95%는 통일 => 5%가 다르다.
		 							=> LIKE가 약간 틀리다.
		 							=> 오라클 / MySQL
		 1) 연결 (송수신) => 드라이버 설정
		 				  ------- 클래스로 만들어져 있다.
		    Class.forName("oracle.jdbc.driver.OracleDriver")
		    			   -------------------------------
		    			   		ojdbc.jar
		 2) 오라클 연결
		 	Connection conn=DriverManager.getConnection(URL, username, password)
		 														hr		happy
		 	URL : jdbc:업체명:드라이버타입:@IP:PORT:데이터베이스명
		 		  jdbc:oracle:thin:@localhost:1521:XE
		 		  => SQLPlus 동일
		 3) SQL문장을 전송
		 	PresparedStatement ps=conn.prepareStatement(SQL문장);
		 	SQL문장 => SELECT...
		 4) 오라클에서 실행된 데이터를 받는다.
		 	ResultSet rs=ps.executeQuery()
		 	=>		  -- 실행된 결과를 메모리에 저장
		 	SELECT ename, job
		 	ResultSet
		 	
		 	ENAME                JOB
	-------------------- ------------------
	KING                 PRESIDENT	| 커서이동 => next()
	BLAKE                MANAGER
	CLARK                MANAGER
	JONES                MANAGER
	MARTIN               SALESMAN
	ALLEN                SALESMAN
	TURNER               SALESMAN
	JAMES                CLERK
	WARD                 SALESMAN
	FORD                 ANALYST
	SMITH                CLERK
	SCOTT                ANALYST
	ADAMS                CLERK
	MILLER               CLERK	| 커서이동 => previous()
	|커서가 여기에 존재 (SQL창에서) => 값을 가져오지 못한다. => 맨 위나 아래로 커서를 옮겨줘야 한다.
	
	=> ORDER BY를 이용해서 데이터를 읽어온다. => next()
	while(rs.next()) // 위에서부터 한줄씩 읽어오기
	{
		=> VO에 값을 채운다.
	}
	=> 읽을 데이터가 없는 상태
	rs.close()
	ps.close()
	conn.close()  ==> 종료
	------------ 코딩하는 패턴이 1개
	------------ SQL문장을 정상수행하게 제작
				 ------ 오라클
	------------ DML , DQL => CRUD
	
	=> 반복 구간 => 연결 / 닫기 => 메소드
	
	SELECT : 데이터 읽기 (검색)
		=> 형식
		   SELECT * | column1, column2...
		   FROM table_name명
		   [
		   		WHERE 조건문 (연산자)
		   		GROUP BY 컬럼|함수 => 그룹
		   		HAVING 그룹에 대한 조건 ==> 반드시 GROUP BY가 있는 경우에만 사용
		   		ORDER BY 컬럼(정렬대상), 번호 => ASC|DESC
		   									 --- 생략 가능
		   ]
 */
import java.util.*; // Date
import java.sql.*; // Connection , PreparedStatement , ResultSet
public class EmpDAO {
		// 네트워크 => 자바응용프로그램 (클라이언트) <=====> 오라클 (서버)
		// 요청 (SQL) <=====> 응답 (실제 출력 결과값을 받는다.) 
		// 연결 객체 생성	=> Connection
		private Connection conn;
		
		// SQL문장 송수신
		private PreparedStatement ps; // read/write
		
		// 오라클 연결 => 오라클 주소 필요
		private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
		
		// 드라이버 등록 => 한번만 수행 => 보통 (생성자)
		public EmpDAO()
		{
			try
			{
				// 대소문자 구분
				Class.forName("oracle.jdbc.driver.OracleDriver");
				// 메모리 할당 => 클래스명으로 메모리 할당이 가능 => 리플렉션
			}catch(Exception ex) {}
		}
		
		// 연결 => SQL 플러스를 연결
		public void getConnection()
		{
			try
			{
				conn=DriverManager.getConnection(URL, "hr", "happy");
				// conn hr/happy
			}catch(Exception ex) {}
		}
		
		// 해제
		public void disConnection()
		{
			try
			{
				if(ps!=null) ps.close();
				if(conn!=null) conn.close();
				
				// exit
			}catch(Exception ex) {}
		}
		
		// 기능 수행 => 메소드 => 테이블 1개당 VO 1개 , DAO 1개
		// SQL문장 전송
		// emp에서 데이터를 => 사번 , 이름 , 입사일 , 직위 , 급여
		public void empListData()
		{
			try
			{
				// 1. 오라클 연결
				getConnection();
				// 2. SQL문장 제작
				String sql="SELECT empno, ename, job, hiredate, sal " // 공백 필수
						 + "FROM emp";
				// 3. SQL문장을 오라클 전송
				ps=conn.prepareStatement(sql);
				// 4. 결과값 받기
				ResultSet rs=ps.executeQuery();
				// 5. 결과값 출력
				/*
				 		no	 name	 sex	regdate
				 		----------------------------
				 		1	신아라	여자 	23/11/13
				 		=> while문 한번에 이 한줄을 읽어와야 한다. (record 단위)
				 		-	-----	---		--------
				 		rs.getInt(1)
						   rs.getString(2)
						   		rs.getString(3)
						   				  rs.getDate(4)
				 */
				while(rs.next())
				{
					System.out.println(rs.getInt(1)+" "
									  +rs.getString(2)+" "
									  +rs.getString(3)+" "
									  +rs.getDate(4)+" "
									  +rs.getInt(5));
				}
				rs.close(); // 메모리 종료
			}catch(Exception ex)
			{
				// 오류위치확인
				ex.printStackTrace();
			}
			finally
			{
				// 닫기
				disConnection();
			}
		}
		
		// 사원 이름, 직위, 급여, 입사일, 성과급 => 성과급이 없는 사원의 목록을 출력
		public void empNotCommListData()
		{
			try
			{
				getConnection();
				String sql="SELECT ename, job, hiredate, sal, comm "
						+ "FROM EMP WHERE comm IS NULL";
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
									  +rs.getString(2)+" "
									  +rs.getDate(3)+" "
									  +rs.getInt(4)+" "
									  +rs.getInt(5));
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
		
		// 사원 이름, 직위, 입사일, 급여, 성과급 (성과급이 있는 사원, 0은 제외)
		public void empCommListData()
		{
			try
			{
				getConnection();
				String sql="SELECT ename, job, hiredate, sal, comm "
						+ "FROM EMP WHERE comm IS NOT NULL AND comm<>0";
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
									  +rs.getString(2)+" "
									  +rs.getDate(3)+" "
									  +rs.getInt(4)+" "
									  +rs.getInt(5));
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
		
		// 사용자로부터 검색을 받아서 검색
		public void empFindData(String ename)
		{
			try
			{
				getConnection();
				String sql="SELECT ename, job, hiredate, sal "
						+"FROM emp "
						+"WHERE ename LIKE '%'||?||'%'"; // ?에 입력된 값이 들어온다.
				ps=conn.prepareStatement(sql);
				// => ?에 값을 채운 후 실행 요청
				ps.setString(1, ename);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
									+rs.getString(2)+" "
									+rs.getDate(3)+" "
									+rs.getInt(4));
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
		
		// RPAD
		public void empRpadData()
		{
			try
			{
				getConnection();
				String sql="SELECT ename, RPAD(substr(ename, 1, 2), LENGTH (ename), '*') "
						+ "FROM emp";
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "+rs.getString(2));
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
		
		public void empSalInfoData()
		{
			try
			{
				getConnection();
				String sql="SELECT ename, ROUND(MONTHS_BETWEEN(SYSDATE, hiredate)), TO_CHAR(sal, '$999,999'), TO_CHAR(sal*12, '$999,999'), TO_CHAR(sal+NVL(comm, 0), '$999,999'),"
						+ "TO_CHAR(hiredate, 'YYYY-MM-DD HH24:MI:SS') "
						+ "FROM emp";
				// , 다음에는 공백 사용할 필요 X
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
							 +rs.getInt(2)+" "
							 +rs.getShort(3)+" "
							 +rs.getShort(4)+" "
							 +rs.getString(5)+" "
							 +rs.getString(6));
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
		
		public void empGroupByData()
		{
			try
			{
				getConnection();
				String sql="SELECT TO_CHAR(hiredate, 'YYYY'), COUNT(*), SUM(sal), AVG(sal), MAX(sal), MIN(sal) "
						+ "FROM EMP "
						+ "GROUP BY TO_CHAR(hiredate, 'YYYY') "
						+ "ORDER BY 1 ASC";
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
							+rs.getInt(2)+" "
							+rs.getInt(3)+" "
							+rs.getDouble(4)+" "
							+rs.getInt(5)+" "
							+rs.getInt(6));
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
		
		// 서브쿼리를 사용하지 않았을 때
		public void subQueryNotData()
		{
			try
			{
				getConnection();
				String sql="SELECT ROUND(AVG(sal)) "
						+"FROM emp";
				ps=conn.prepareStatement(sql);
				// SQL문장은 1개 전송 가능
				ResultSet rs=ps.executeQuery();
				rs.next();
				int avg=rs.getInt(1);
				rs.close();
				
				sql="SELECT ename, job, hiredate, sal "
						+"FROM emp "
						+"WHERE sal<?";
				ps=conn.prepareStatement(sql);
				ps.setInt(1, avg);
				rs=ps.executeQuery();
				
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
							+rs.getString(2)+" "
							+rs.getDate(3)+" "
							+rs.getInt(4));
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				disConnection();
			}
		}
		
		// 서브쿼리를 사용했을 때
		public void subQueryData()
		{
			try
			{
				getConnection();
				String sql="SELECT ename, sal, job, hiredate "
						+ "FROM EMP "
						+ "WHERE sal<(SELECT ROUND(AVG(sal)) FROM emp)";
				ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				ps=conn.prepareStatement(sql);
				rs=ps.executeQuery();
				while(rs.next())
				{
					System.out.println(rs.getString(1)+" "
							+rs.getString(2)+" "
							+rs.getDate(3)+" "
							+rs.getInt(4));
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
}














