<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,java.util.Map" %>
<html>
<head>
    <title>Charging Stations</title>
</head>
<body>
    <h2>Charging Stations</h2>
    <c:forEach var="station" items="${stations}">
        <h3>${station.locationName} (Capacity: ${station.maxCapacity})</h3>
        <p>Number of scooters: ${scooterCount[station.id]}</p>
        <table border="1">
            <tr><th>ID</th><th>Model</th><th>Status</th><th>Battery Level</th></tr>
            <c:forEach var="scooter" items="${scootersByStation[station.id]}">
                <tr>
                    <td>${scooter.vehicleNumber}</td>
                    <td>${scooter.model}</td>
                    <td>${scooter.status}</td>
                    <td>${scooter.currentCharge}%</td>
                </tr>
            </c:forEach>
        </table>
    </c:forEach>
</body>
</html>