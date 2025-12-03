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
            max-width: 400px;
            margin: 0 auto;
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
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            cursor: pointer;
            width: 100%;
        }
        .error {
            color: red;
            background-color: #ffe6e6;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .message {
            color: green;
            background-color: #e6ffe6;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <header>
        <h1>Register Page</h1>
    </header>
    <main>
        <h2>Create Account</h2>
        
        <%-- Display error messages --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <%-- Display success messages from session --%>
        <% if (session.getAttribute("message") != null) { %>
            <div class="message">
                <%= session.getAttribute("message") %>
                <% session.removeAttribute("message"); %>
            </div>
        <% } %>
        
        <form action="${pageContext.request.contextPath}/controller" method="post">
            <input type="hidden" name="command" value="register" />
            
            <div class="form-group">
                <label>Full Name:</label>
                <input type="text" name="name" required />
            </div>
            
            <div class="form-group">
                <label>Email:</label>
                <input type="email" name="email" required />
            </div>
            
            <div class="form-group">
                <label>Password:</label>
                <input type="password" name="password" required minlength="6" />
            </div>
            
            <div class="form-group">
                <label>Confirm Password:</label>
                <input type="password" name="confirmPassword" required />
            </div>
            
            <div class="form-group">
                <label>User Type:</label>
                <select name="userType">
                    <option value="USER">Regular User</option>
                    <option value="MAINTAINER">Maintainer</option>
                    <option value="SPONSOR">Sponsor</option>
                </select>
            </div>
            
            <button type="submit">Register</button>
        </form>
        
        <p style="text-align: center; margin-top: 15px;">
            Already have an account? <a href="login.jsp">Login here</a>
        </p>
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>