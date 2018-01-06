<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="navbar navbar-default navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<a href="/" class="navbar-brand">Tom Clarke's FYP</a>
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
				<li <c:if test="${param.active eq 'add'}">class="active"</c:if>><a
					class="nav-link" href="/add">Add a paper</a></li>
				<li><a class="nav-link" href="/#about">About</a></li>
			</ul>
		</div>
	</div>
</div>
