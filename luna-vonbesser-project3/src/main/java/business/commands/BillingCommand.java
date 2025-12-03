package business.commands;

import java.util.Map;

import business.BillingService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BillingCommand implements CommandInterface {

    private final BillingService billingService;

    public BillingCommand() {
        this.billingService = new BillingService();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return "login.jsp";
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        // Handle different billing actions
        if ("pay".equals(action)) {
            return handlePayment(request, response, userId, session);
        } else if ("estimate".equals(action)) {
            return handleEstimate(request, response, userId, session);
        } else {
            // Default: show billing summary
            return showBillingSummary(request, userId, session);
        }
    }

    private String showBillingSummary(HttpServletRequest request, int userId, HttpSession session) {
        Map<String, Object> summary = billingService.getBillingSummary(userId);

        request.setAttribute("billingSummary", summary);
        request.setAttribute("userName", session.getAttribute("userName"));
        request.setAttribute("userType", session.getAttribute("userType"));

        return "billing.jsp";
    }

    private String handlePayment(HttpServletRequest request, HttpServletResponse response, 
                                 int userId, HttpSession session) {
        try {
            double amount = Double.parseDouble(request.getParameter("amount"));
            String paymentMethod = request.getParameter("paymentMethod");

            boolean success = billingService.processPayment(userId, amount, paymentMethod);

            if (success) {
                session.setAttribute("message", "Payment of $" + amount + " processed successfully!");
            } else {
                session.setAttribute("error", "Payment failed. Please try again.");
            }

            // Redirect back to billing page to show updated summary
            response.sendRedirect(request.getContextPath() + "/controller?command=billing");
            return null;

        } catch (Exception e) {
            session.setAttribute("error", "Invalid payment details: " + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/controller?command=billing");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private String handleEstimate(HttpServletRequest request, HttpServletResponse response, 
                                  int userId, HttpSession session) {
        try {
            double distance = Double.parseDouble(request.getParameter("distance"));
            int minutes = Integer.parseInt(request.getParameter("minutes"));

            double estimate = billingService.estimateTripCost(distance, minutes);
            session.setAttribute("estimate", estimate);

            // Redirect back to billing page to show estimate
            response.sendRedirect(request.getContextPath() + "/controller?command=billing");
            return null;

        } catch (Exception e) {
            session.setAttribute("error", "Invalid estimate parameters: " + e.getMessage());
            try {
                response.sendRedirect(request.getContextPath() + "/controller?command=billing");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public String getCommandName() {
        return "billing";
    }
}