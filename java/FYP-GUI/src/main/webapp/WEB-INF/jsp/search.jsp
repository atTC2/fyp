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


	<c:choose>
		<c:when test="${hasErrored}">
			<c:set var="cssForForm" scope="session" value="has-error" />
		</c:when>
		<c:otherwise>
			<c:set var="cssForForm" scope="session" value="" />
		</c:otherwise>
	</c:choose>

	<form:form method="POST" action="/search" modelAttribute="search"
		class="form-group ${cssForForm}">

		<div class="container">
			<div class="row">
				<div class="col-lg-4 text-center">
					<div class="card">
						<div class="card-block form-group">
							<h3 class="card-title">
								<form:label class="control-label" path="task">Task:</form:label>
							</h3>
							<div class="card-subtitle mb-2 text-muted">
								<form:input type="text" class="form-control" path="task" />
								<p>
									<form:errors path="task" />
								</p>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4 text-center">
					<div class="card">
						<div class="card-block form-group">
							<h3 class="card-title">
								<form:label class="control-label" path="process">Process:</form:label>
							</h3>
							<div class="card-subtitle mb-2 text-muted">
								<form:input type="text" class="form-control" path="process" />
								<p>
									<form:errors path="process" />
								</p>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4 text-center">
					<div class="card">
						<div class="card-block form-group">
							<h3 class="card-title">
								<form:label class="control-label" path="material">Material:</form:label>
							</h3>
							<div class="card-subtitle mb-2 text-muted">
								<form:input type="text" class="form-control" path="material" />
								<p>
									<form:errors path="material" />
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row text-center">
				<input class="btn btn-primary btn-lg" type="submit" value="Submit" />
			</div>
		</div>

	</form:form>

	<c:if test="${not empty results}">
		<div class="container">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th>ID</th>
						<th>Paper Title</th>
						<th>KPs</th>
						<th>Rels</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${results}" var="result">
						<tr class="clickable-row" style="cursor: pointer;"
							data-href="view?paper=${result.id}">
							<td>${result.id}</td>
							<td><strong>${result.paper}</strong></td>
							<td>${result.kps}</td>
							<td>${result.rels}</td>
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