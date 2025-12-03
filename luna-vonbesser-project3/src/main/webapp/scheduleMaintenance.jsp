<html>
<head>
    <title>Schedule Maintenance</title>
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
        <h1>Schedule Maintenance</h1>
    </header>
    <main>
        <h2>Schedule Maintenance</h2>
        <c:choose>
            <c:when test="${success}">
                <p>Maintenance task scheduled successfully!</p>
            </c:when>
            <c:otherwise>
                <p>Failed to schedule maintenance task.</p>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/controller?command=viewMaintenanceAlerts">Back to Alerts</a>
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>