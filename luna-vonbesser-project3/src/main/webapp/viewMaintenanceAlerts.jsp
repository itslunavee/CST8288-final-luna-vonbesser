<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,java.util.Map" %>
<html>
<head>
    <title>Maintenance Alerts</title>
</head>
<body>
    <h2>Maintenance Alerts</h2>
    <table border="1">
        <tr><th>ID</th><th>Scooter</th><th>Type</th><th>Description</th><th>Priority</th><th>Status</th><th>Created</th><th>Resolved</th><th>Action</th></tr>
        <c:forEach var="alert" items="${alerts}">
            <tr>
                <td>${alert.id}</td>
                <td>${alert.vehicleNumber}</td>
                <td>${alert.alertType}</td>
                <td>${alert.description}</td>
                <td>${alert.priority}</td>
                <td>${alert.status}</td>
                <td>${alert.createdDate}</td>
                <td>${alert.resolvedDate}</td>
                <td>
                    <form action="controller" method="post">
                        <input type="hidden" name="command" value="scheduleMaintenance" />
                        <input type="hidden" name="alertId" value="${alert.id}" />
                        <button type="submit">Schedule</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>