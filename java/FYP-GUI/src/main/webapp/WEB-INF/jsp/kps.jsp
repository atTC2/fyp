<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<jsp:include page="header.jsp"></jsp:include>

<script type="text/javascript" src="/js/jqcloud.min.js"></script>
<link rel="stylesheet" href="/css/jqcloud.min.css" />

<title>Key Phrases</title>

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="kps" />
	</jsp:include>

	<div class="container">
		<div class="page-header">
			<h1>All found key phrases, grouped by classification.</h1>
			<h2>To generate the below graphs, key phrases are broken up into
				tokens, and the size of a token is relative to it's TF-IDF value.</h2>
		</div>
	</div>
	<div class="container form-group">
		<div class="row text-center">
			<div id='task' class="jqcloud"></div>
			<a type="button" class="btn btn-info" href="/kps/task">View all <strong>Task</strong>
				key phrases
			</a>
		</div>
	</div>
	<div class="container form-group">
		<div class="row text-center">
			<div id='process' class="jqcloud"></div>
			<a type="button" class="btn btn-warning" href="/kps/process">View
				all <strong>Process</strong> key phrases
			</a>
		</div>
	</div>
	<div class="container form-group">
		<div class="row text-center">
			<div id='material' class="jqcloud"></div>
			<a type="button" class="btn btn-success" href="/kps/material">View
				all <strong>Material</strong> key phrases
			</a>
		</div>
	</div>

	<script>
		// Cloud drawing configurations
		var cloudConfig = {
			"task" : {
				"id" : "#task",
				"title" : "Task",
				"colours" : [ "#5bc0de", "#5bc0de", "#5bc0de", "#5bc0de",
						"#5bc0de", "#5bc0de", "#5bc0de", "#5bc0de", "#5bc0de" ]
			},
			"process" : {
				"id" : "#process",
				"title" : "Process",
				"colours" : [ "#f89406", "#f89406", "#f89406", "#f89406",
						"#f89406", "#f89406", "#f89406", "#f89406", "#f89406" ]
			},
			"material" : {
				"id" : "#material",
				"title" : "Material",
				"colours" : [ "#62c462", "#62c462", "#62c462", "#62c462",
						"#62c462", "#62c462", "#62c462", "#62c462", "#62c462" ]
			}
		}

		// Draw a cloud, or an appropriate error message if it could not be drawn
		function drawCloud(cloudData, config) {
			console.log(cloudData);
			if (cloudData == null || cloudData == []) {
				writeError(config);
				return;
			}

			$(config.id).jQCloud(cloudData, {
				width : $('.page-header').width(),
				height : $(window).height() / 1.5,
				colors : config.colours
			});
		}

		// Handles AJAX errors
		function ajaxFailure(err, config) {
			console.log(err);
			writeError(config);
		}

		// Writes errors to the screen
		function writeError(config) {
			$(config.id)
					.append(
							"<div class=\"alert alert-danger\"><h5>"
									+ config.title
									+ " key phrase cloud could not be loaded.</h5></div>");
		}

		// Load all 3 clouds
		$.ajax({
			url : '/kps',
			data : {
				type : 'Task'
			},
			type : 'POST',
			dataType : 'json',
			success : function(cloudData) {
				drawCloud(cloudData, cloudConfig.task);
			},
			error : function(err) {
				ajaxFailure(err, cloudConfig.task);
			}
		});

		$.ajax({
			url : '/kps',
			data : {
				type : 'Process'
			},
			type : 'POST',
			dataType : 'json',
			success : function(cloudData) {
				drawCloud(cloudData, cloudConfig.process);
			},
			error : function(err) {
				ajaxFailure(err, cloudConfig.process);
			}
		});

		$.ajax({
			url : '/kps',
			data : {
				type : 'Material'
			},
			type : 'POST',
			dataType : 'json',
			success : function(cloudData) {
				drawCloud(cloudData, cloudConfig.material);
			},
			error : function(err) {
				ajaxFailure(err, cloudConfig.material);
			}
		});
	</script>
</body>

</html>
