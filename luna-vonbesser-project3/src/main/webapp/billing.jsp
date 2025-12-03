<%@ page import="java.util.Map" %>
<%@ page import="java.text.NumberFormat" %>
<%
    Map<String, Object> summary = (Map<String, Object>) request.getAttribute("billingSummary");
    String userName = (String) request.getAttribute("userName");
    String userType = (String) request.getAttribute("userType");
    
    Double balance = summary != null ? (Double) summary.get("balance") : 0.0;
    Double unpaid = summary != null ? (Double) summary.get("unpaid") : 0.0;
    Double monthlyUsage = summary != null ? (Double) summary.get("monthlyUsage") : 0.0;
    String dueDate = summary != null ? (String) summary.get("dueDate") : "";
    Boolean isOverdue = summary != null ? (Boolean) summary.get("isOverdue") : false;
    
    NumberFormat currency = NumberFormat.getCurrencyInstance();
    
    // get messages from session
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
    Double estimate = (Double) session.getAttribute("estimate");
    
    // clear messages after displaying
    session.removeAttribute("message");
    session.removeAttribute("error");
    session.removeAttribute("estimate");
%>
<!DOCTYPE html>
<html>
<head>
    <title>CESC - Billing & Payments</title>
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
            max-width: 800px;
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
        .card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }
        .card.overdue {
            border-left-color: #dc3545;
        }
        .card.success {
            border-left-color: #28a745;
        }
        .debit {
            color: #dc3545;
            font-weight: bold;
        }
        .credit {
            color: #28a745;
            font-weight: bold;
        }
        .due-soon {
            color: #856404;
        }
        form {
            margin: 15px 0;
        }
        input, select, button {
            padding: 8px;
            margin: 5px 0;
        }
        button {
            background: #007bff;
            color: white;
            border: none;
            cursor: pointer;
        }
        button:hover {
            background: #0056b3;
        }
        .info-box {
            background: #e7f3ff;
            padding: 10px;
            border-radius: 3px;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <header>
        <h1>Billing Page</h1>
    </header>
    <main>
        <h2>Billing & Payments</h2>
        <p>welcome, <%= userName %>! (<%= userType %>)</p>
        
        <% if (message != null) { %>
            <div class="card success">
                <p>✅ <%= message %></p>
            </div>
        <% } %>
        
        <% if (error != null) { %>
            <div class="card overdue">
                <p>❌ <%= error %></p>
            </div>
        <% } %>
        
        <div class="card <%= isOverdue ? "overdue" : "" %>">
            <h3>account summary</h3>
            
            <p><strong>current balance:</strong> 
                <% if (balance < 0) { %>
                    <span class="debit"><%= currency.format(balance) %></span>
                <% } else { %>
                    <span class="credit"><%= currency.format(balance) %></span>
                <% } %>
            </p>
            
            <p><strong>unpaid charges:</strong> 
                <% if (unpaid > 0) { %>
                    <span class="debit"><%= currency.format(unpaid) %></span>
                <% } else { %>
                    <span class="credit"><%= currency.format(0.0) %></span>
                <% } %>
            </p>
            
            <p><strong>this month's usage:</strong> <%= currency.format(monthlyUsage) %></p>
            
            <% if (!dueDate.isEmpty()) { %>
                <p><strong>payment due by:</strong> <%= dueDate %></p>
            <% } %>
            
            <% if (isOverdue) { %>
                <div class="info-box">
                    <p>⚠️ <strong>your account is overdue!</strong></p>
                    <p>please make a payment immediately to avoid account suspension.</p>
                </div>
            <% } else if (unpaid > 0) { %>
                <div class="info-box">
                    <p>📅 <strong>payment reminder</strong></p>
                    <p>please pay your balance of <%= currency.format(unpaid) %> by <%= dueDate %>.</p>
                </div>
            <% } %>
        </div>
        
        <% if (unpaid > 0) { %>
        <div class="card">
            <h3>make a payment</h3>
            
            <form action="billing" method="post">
                <input type="hidden" name="action" value="pay">
                
                <label>amount to pay:</label><br>
                <input type="number" name="amount" step="0.01" min="0.01" 
                       max="<%= unpaid %>" value="<%= unpaid %>" required><br>
                
                <label>payment method:</label><br>
                <select name="paymentMethod" required>
                    <option value="">select method...</option>
                    <option value="credit_card">credit card</option>
                    <option value="debit_card">debit card</option>
                    <option value="paypal">paypal</option>
                    <option value="bank_transfer">bank transfer</option>
                </select><br><br>
                
                <button type="submit">submit payment</button>
            </form>
            
            <p style="font-size: 0.9em; color: #666;">
                <strong>note:</strong> payments are applied to the oldest charges first.<br>
                processing may take 1-2 business days.
            </p>
        </div>
        <% } %>
        
        <div class="card">
            <h3>trip cost estimator</h3>
            
            <% if (estimate != null) { %>
                <div class="info-box">
                    <p>estimated cost: <strong><%= currency.format(estimate) %></strong></p>
                </div>
            <% } %>
            
            <form action="billing" method="post">
                <input type="hidden" name="action" value="estimate">
                
                <label>estimated distance (km):</label><br>
                <input type="number" name="distance" step="0.1" min="0.1" value="2.5" required><br>
                
                <label>estimated time (minutes):</label><br>
                <input type="number" name="minutes" min="1" value="15" required><br><br>
                
                <button type="submit">estimate cost</button>
            </form>
            
            <p style="font-size: 0.9em; color: #666; margin-top: 15px;">
                <strong>how costs are calculated:</strong><br>
                • $0.25 per kilometer<br>
                • $0.10 per minute away from station<br>
                • 10% discount on trips over $5.00
            </p>
        </div>
        
        <div style="margin-top: 30px;">
            <h4>need help?</h4>
            <p>if you have questions about your bill or need payment assistance, please contact:</p>
            <ul>
                <li>email: billing@cesc.algonquin.com</li>
                <li>phone: (613) 555-BILL</li>
                <li>office: C building, room 123</li>
            </ul>
        </div>
        
    </main>
    <footer>
        <p>&copy; 2025 Luna Vonbesser Project</p>
    </footer>
</body>
</html>
