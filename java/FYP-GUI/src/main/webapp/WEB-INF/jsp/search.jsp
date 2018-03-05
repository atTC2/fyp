<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>Paper Search</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="search" />
	</jsp:include>

	<div class="container">
		<div class="page-header">
			<h1>Search for papers</h1>
		</div>
	</div>

	<form:form method="POST" action="/search" modelAttribute="search"
		class="form-group">
		<div class="container">
			<form:label class="control-label" path="text">Enter terms to search for and focus on task, process or material related items:</form:label>
			<div class="form-group input-group">
				<form:input type="text" class="form-control" path="text"
					placeholder="Search terms or phrases" maxlength="120" />
				<span class="input-group-btn"> <input class="btn btn-primary"
					type="submit" value="Submit" />
				</span>
			</div>
			<div>
				<label class="control-label">Focus on:</label>
				<form:checkbox path="focusOnTask" />
				Task
				<form:checkbox path="focusOnProcess" />
				Process
				<form:checkbox path="focusOnMaterial" />
				Material
			</div>
			<c:if test="${hasErrored}">
				<div class="alert alert-dismissable alert-danger">
					<button type="button" class="close" data-dismiss="alert">×</button>
					<strong><form:errors path="text" /></strong>
				</div>
			</c:if>
		</div>
	</form:form>

	<c:if test="${noResults}">
		<div class="container">
			<div class="alert alert-dismissable alert-danger">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<p>
					<strong>No papers found!</strong>
				</p>
			</div>
		</div>
	</c:if>

	<c:if test="${not empty results}">
		<div class="container">
			<p>${resultsInfo}</p>
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th>ID</th>
						<th>Paper Title</th>
						<th>KPs / Rels</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${results}" var="result">
						<tr class="clickable-row" style="cursor: pointer;"
							data-href="view?paper=${result.id}">
							<td>${result.id}</td>
							<td><strong>${result.paper}</strong><a class="pull-right"
								href="/view/download?paper=${result.id}"><span
									class="glyphicon glyphicon-download"></span></a> <br />${result.snippet}</td>
							<td>${result.kps}&nbsp;/&nbsp;${result.rels}<a
								class="pull-right" href="/view/extractions?paper=${result.id}"><span
									class="glyphicon glyphicon-download"></span></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<script>
			jQuery(document).ready(function($) {
				$(".clickable-row").click(function() {
					window.location = $(this).data("href");
				});
			});
		</script>
	</c:if>

</body>

</html>