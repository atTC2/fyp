<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="navbar navbar-default navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<a href="/" class="navbar-brand">ExtractorIE</a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div
			class="collapse navbar-collapse navbar-responsive-collapse navbar-right">
			<ul class="nav navbar-nav">
				<li <c:if test="${param.active eq 'home'}">class="active"</c:if>><a
					href="/">Home</a></li>
				<li <c:if test="${param.active eq 'search'}">class="active"</c:if>><a
					class="nav-link" href="/search">Search for a paper</a></li>
				<li
					class="dropdown <c:if test="${param.active eq 'kps'}">active</c:if>"><a
					class="dropdown-toggle" data-toggle="dropdown" href="#">Key
						Phrases<span class="caret"></span>
				</a>
					<ul class="dropdown-menu">
						<li><a href="/kps">View all</a></li>
						<li class="divider"></li>
						<li><a href="/kps/task">Tasks</a></li>
						<li><a href="/kps/process">Processes</a></li>
						<li><a href="/kps/material">Materials</a></li>
					</ul></li>
				<li <c:if test="${param.active eq 'add'}">class="active"</c:if>><a
					class="nav-link" href="/add">Add a paper</a></li>
			</ul>
		</div>
	</div>
</div>
