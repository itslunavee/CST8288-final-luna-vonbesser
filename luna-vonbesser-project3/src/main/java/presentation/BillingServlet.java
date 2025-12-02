package presentation;

import business.BillingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {

    private BillingService billingService;

    @Override
    public void init() throws ServletException {
        billingService = new BillingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        // get billing summary from business service
        Map<String, Object> summary = billingService.getBillingSummary(userId);

        // pass data to JSP
        request.setAttribute("billingSummary", summary);
        request.setAttribute("userName", session.getAttribute("userName"));
        request.setAttribute("userType", session.getAttribute("userType"));

        // forward to billing page
        request.getRequestDispatcher("billing.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("pay".equals(action)) {
            // process payment
            try {
                double amount = Double.parseDouble(request.getParameter("amount"));
                String paymentMethod = request.getParameter("paymentMethod");

                boolean success = billingService.processPayment(userId, amount, paymentMethod);

                if (success) {
                    session.setAttribute("message", "payment of $" + amount + " processed successfully!");
                } else {
                    session.setAttribute("error", "payment failed. please try again.");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("error", "invalid amount entered.");
            }
        } else if ("estimate".equals(action)) {
            // estimate trip cost
            try {
                double distance = Double.parseDouble(request.getParameter("distance"));
                int minutes = Integer.parseInt(request.getParameter("minutes"));

                double estimatedCost = billingService.estimateTripCost(distance, minutes);
                session.setAttribute("estimate", estimatedCost);

            } catch (NumberFormatException e) {
                session.setAttribute("error", "invalid distance or time entered.");
            }
        }

        // redirect back to billing page
        response.sendRedirect("billing");
    }
}
