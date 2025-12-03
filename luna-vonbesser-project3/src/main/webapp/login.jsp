<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login or Register</title>
</head>
<body>
    <h2>Welcome to the E-Scooter System</h2>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="login" />
        <label>Email:</label><input type="text" name="email" /><br/>
        <label>Password:</label><input type="password" name="password" /><br/>
        <button type="submit">Login</button>
    </form>
    <hr/>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="register" />
        <button type="submit">Register</button>
    </form>
</body>
</html>