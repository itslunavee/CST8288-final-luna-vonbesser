<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>View Scooters</title>
</head>
<body>
<h1>Available Scooters</h1>

<%-- Display scooters if available --%>
<c:if test="${not empty scooters}">
    <ul>
        <c:forEach var="scooter" items="${scooters}">
            <li>${scooter.id} - ${scooter.model} - ${scooter.status}</li>
        </c:forEach>
    </ul>
</c:if>

<%-- Display message if no scooters are available --%>
<c:if test="${empty scooters}">
    <p>No scooters available at the moment.</p>
</c:if>

</body>
</html>