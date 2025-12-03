<html>
<head>
    <title>Home - Maintainer</title>
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
        <h1>Maintainer Home</h1>
    </header>
    <main>
        <h2>Maintainer Home</h2>
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="billing" />
            <button type="submit">View Credits/Billing</button>
        </form>
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="viewMaintenanceAlerts" />
            <button type="submit">View Maintenance Alerts</button>
        </form>
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="scheduleMaintenance" />
            <button type="submit">Schedule Maintenance Task</button>
        </form>
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="viewChargingStations" />
            <button type="submit">View Charging Stations</button>
        </form>
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>