<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sponsor Home</title>
</head>
<body>
    <h2>Sponsor Home</h2>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="billing" />
        <button type="submit">View Credits/Billing</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="addScooter" />
        <button type="submit">Add Scooter</button>
    </form>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="viewChargingStations" />
        <button type="submit">View Charging Stations</button>
    </form>
</body>
</html>