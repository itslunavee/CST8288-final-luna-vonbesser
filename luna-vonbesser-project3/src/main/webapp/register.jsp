<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
    <h2>Register</h2>
    <form action="controller" method="post">
        <input type="hidden" name="command" value="register" />
        <label>Email:</label><input type="text" name="email" /><br/>
        <label>Password:</label><input type="password" name="password" /><br/>
        <label>Confirm Password:</label><input type="password" name="confirmPassword" /><br/>
        <button type="submit">Register</button>
    </form>
</body>
</html>