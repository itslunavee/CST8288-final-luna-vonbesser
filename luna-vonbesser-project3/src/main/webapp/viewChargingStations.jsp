<%@ page import="java.util.List,java.util.Map" %>
<html>
<head>
    <title>View Charging Stations</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
        }
        header {
            background-color: #333;
            color: white;
            padding: 1rem;
            text-align: center;
        }
        main {
            padding: 2rem;
        }
        footer {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 1rem;
            position: fixed;
            bottom: 0;
            width: 100%;
        }
    </style>
</head>
<body>
    <header>
        <h1>Charging Stations</h1>
    </header>
    <main>
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
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>