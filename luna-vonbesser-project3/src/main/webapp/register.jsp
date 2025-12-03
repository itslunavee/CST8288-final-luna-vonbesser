<html>
<head>
    <title>Register</title>
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
        <h1>Register Page</h1>
    </header>
    <main>
        <h2>Register</h2>
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="register" />
            <label>Email:</label><input type="text" name="email" /><br/>
            <label>Password:</label><input type="password" name="password" /><br/>
            <label>Confirm Password:</label><input type="password" name="confirmPassword" /><br/>
            <label>User Type:</label>
            <select name="userType">
                <option value="USER">User</option>
                <option value="MAINTAINER">Maintainer</option>
                <option value="SPONSOR">Sponsor</option>
            </select><br/>
            <button type="submit">Register</button>
        </form>
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>