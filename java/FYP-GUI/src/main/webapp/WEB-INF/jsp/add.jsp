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
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="add" />
	</jsp:include>

	<div class="container">
		<div class="page-header">
			<h1>Add a paper to the database</h1>
		</div>

		<c:choose>
			<c:when test="${hasErrored}">
				<c:set var="cssForForm" scope="session" value="has-error" />
			</c:when>
			<c:when test="${hasSucceeded}">
				<c:set var="cssForForm" scope="session" value="has-success" />
			</c:when>
			<c:otherwise>
				<c:set var="cssForForm" scope="session" value="" />
			</c:otherwise>
		</c:choose>

		<form:form method="POST" action="/add" modelAttribute="location"
			class="form-group ${cssForForm}">
			<form:label class="control-label" path="location">Please enter the paper location:</form:label>
			<div class="form-group input-group">
				<form:input type="text" class="form-control" path="location" />
				<span class="input-group-btn"> <input class="btn btn-primary"
					type="submit" value="Submit" />
				</span>
			</div>
			<c:if test="${hasErrored}">
				<div class="alert alert-dismissable alert-danger">
					<button type="button" class="close" data-dismiss="alert">×</button>
					<strong>Oh dear!</strong>
					<form:errors path="location" />
				</div>
			</c:if>
		</form:form>
	</div>

	<c:if test="${not empty message}">
		<div class="container">
			<div class="alert alert-dismissable alert-success">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<p>
					<strong>${message}</strong>
				</p>
				<a href="/view?paper=${paperId}">View the paper here</a>
			</div>
		</div>
	</c:if>

</body>

</html>