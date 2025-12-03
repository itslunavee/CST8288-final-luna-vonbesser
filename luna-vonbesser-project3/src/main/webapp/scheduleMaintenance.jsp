<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Schedule Maintenance</title>
</head>
<body>
    <h2>Schedule Maintenance</h2>
    <c:choose>
        <c:when test="${success}">
            <p>Maintenance task scheduled successfully!</p>
        </c:when>
        <c:otherwise>
            <p>Failed to schedule maintenance task.</p>
        </c:otherwise>
    </c:choose>
    <a href="controller?command=viewMaintenanceAlerts">Back to Alerts</a>
</body>
</html>