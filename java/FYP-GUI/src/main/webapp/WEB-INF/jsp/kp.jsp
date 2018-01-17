<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<title>${title}s</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="kps" />
	</jsp:include>

	<div class="container">
		<div class="page-header">
			<c:choose>
				<c:when test="${title eq 'Task'}">
					<h1>
						<span class="label label-info">Task Key Phrases</span>
					</h1>
				</c:when>
				<c:when test="${title eq 'Process'}">
					<h1>
						<span class="label label-warning">Process Key Phrases</span>
					</h1>
				</c:when>
				<c:when test="${title eq 'Material'}">
					<h1>
						<span class="label label-success">Material Key Phrases</span>
					</h1>
				</c:when>
				<c:otherwise>
					<h1 class="text-danger">${title}</h1>
				</c:otherwise>
			</c:choose>
		</div>

		<c:if test="${not empty kps}">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th>Key Phrase</th>
						<th>Paper</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${kps}" var="kp">
						<tr class="clickable-row" style="cursor: pointer;"
							data-href="/view?paper=${kp.paperId}">
							<td>${kp.keyPhrase}</td>
							<td>${kp.paper}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<script>
				jQuery(document).ready(function($) {
					$(".clickable-row").click(function() {
						window.location = $(this).data("href");
					});
				});
			</script>
		</c:if>
	</div>
</body>

</html>
