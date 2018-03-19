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
					<c:forEach items="${results}" var="result" varStatus="loop">
						<c:choose>
							<c:when test="${loop.index < MAX_RESULTS}">
								<!-- Show results -->
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
							</c:when>
							<c:otherwise>
								<c:if test="${loop.index == MAX_RESULTS}">
									<!-- Show 'Show more' button -->
									<tr class="clickable-row" style="cursor: pointer;"
										id="showMoreRow">
										<td colspan="3" onclick="showMore()"
											style="text-align: center;"><strong>Show
												${resultsCount - MAX_RESULTS} more</strong></td>
									</tr>
								</c:if>
								<!-- Make thing, but hidden -->
								<tr class="clickable-row" style="cursor: pointer;"
									data-href="view?paper=${result.id}" hidden="true">
									<td>${result.id}</td>
									<td><strong>${result.paper}</strong><a class="pull-right"
										href="/view/download?paper=${result.id}"><span
											class="glyphicon glyphicon-download"></span></a> <br />${result.snippet}</td>
									<td>${result.kps}&nbsp;/&nbsp;${result.rels}<a
										class="pull-right" href="/view/extractions?paper=${result.id}"><span
											class="glyphicon glyphicon-download"></span></a></td>
								</tr>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<script>
			// Make the row items clickable so the user can go to the full papers
			jQuery(document).ready(function($) {
				$(".clickable-row").click(function() {
					window.location = $(this).data("href");
				});
			});

			// Make it so if the 'show more' button is pressed, more is shown
			function showMore() {
				$('#showMoreRow').remove();
				$('tr').show();
			}
		</script>
	</c:if>

</body>

</html>