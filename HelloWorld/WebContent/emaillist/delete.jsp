<%@page import="com.bit.utils.emaillist.dao.EmaillistDaoOrcl"%>
<%@page import="com.bit.utils.emaillist.dao.EmaillistDao"%>
<%@page import="com.bit.utils.emaillist.vo.EmaillistVo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- no=6 -->
<!-- delete.jsp에서 dao 불러와서 delete 메서드로 현재 행의 이메일 주소 레코드 삭제 -->

<%
//요청 정보로부터 파라미터 추출
Long no = Long.valueOf(request.getParameter("no"));

//DTO 객체 생성
EmaillistVo vo = new EmaillistVo();
//DAO 객체 생성
EmaillistDao dao = new EmaillistDaoOrcl();

dao.delete(no);

response.sendRedirect(request.getContextPath() + "/emaillist/");	//	이동할 페이지를 명시
%>