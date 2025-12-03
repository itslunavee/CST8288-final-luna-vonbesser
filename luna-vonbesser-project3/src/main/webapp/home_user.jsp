<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Home</title>
</head>
<body>
    <h2>User Home</h2>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="billing" />
        <button type="submit">View Billing Report</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="viewChargingStations" />
        <button type="submit">View Charging Stations</button>
    </form>
</body>
</html>