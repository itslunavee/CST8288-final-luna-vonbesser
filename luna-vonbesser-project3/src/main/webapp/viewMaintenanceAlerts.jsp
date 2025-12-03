<%@ page import="java.util.List,java.util.Map" %>
<html>
<head>
    <title>View Maintenance Alerts</title>
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
        <h1>Maintenance Alerts</h1>
    </header>
    <main>
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
                        <form action="${pageContext.request.contextPath}/controller" method="post">
                            <input type="hidden" name="command" value="scheduleMaintenance" />
                            <input type="hidden" name="alertId" value="${alert.id}" />
                            <button type="submit">Schedule</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>