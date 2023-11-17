<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.sist.dao.*"%>
<%
	String hakbun=request.getParameter("hakbun");
	StudentDAO dao=new StudentDAO();
	dao.stdDelete(Integer.parseInt(hakbun));
	response.sendRedirect("list.jsp");
%>