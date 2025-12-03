<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Maintainer Home</title>
</head>
<body>
    <h2>Maintainer Home</h2>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="billing" />
        <button type="submit">View Credits/Billing</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="viewMaintenanceAlerts" />
        <button type="submit">View Maintenance Alerts</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="scheduleMaintenance" />
        <button type="submit">Schedule Maintenance Task</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="viewChargingStations" />
        <button type="submit">View Charging Stations</button>
    </form>
</body>
</html>