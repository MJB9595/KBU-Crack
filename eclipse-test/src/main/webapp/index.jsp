<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%
    String student = "김재아";
    String subject = "UITEST";
    String serverTime = new java.util.Date().toString();

%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>main-jsp</title>
    <link rel="stylesheet" href="./CSS/style.css">
</head>
<body>
<h1>
    hello World<br>
    JSP 처리과정 실습
</h1>
<p> 이 이름은 JSP 에서 서버가 만든 값이에요<strong><%=student %></strong></p>
<p> 이 과목명도 JSP 에서 서버가 만든 값이에요<strong><%=subject %></strong></p>
<p> 서버시간 : <strong><%=serverTime %></strong></p>
<hr>
<img src='./images/i16471962888.jpg' alt="몰라!" width="200"><br>
<button onclick="showMessage()">JS 실행 버튼</button>
<script src="./js/app.js"></script>
</body>
</html>