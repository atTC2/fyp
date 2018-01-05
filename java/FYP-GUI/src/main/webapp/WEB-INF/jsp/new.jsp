<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>Add New Paper</title>

</head>
<body>

	<div class="container">
		<h1>Add a Paper to the database</h1>
		<form:form method="POST" action="/new" modelAttribute="location">
			<form:label path="location">Please enter the paper location:</form:label>
			<form:input path="location" />
			<input type="submit" value="Submit" />
			<form:errors path="location" />
		</form:form>
	</div>

	<c:if test="${not empty message}">
		<div class="container">
			<p>${message}</p>
			<a href="/view?paper=${paperId}">View the paper here</a>
		</div>
	</c:if>

</body>

</html>