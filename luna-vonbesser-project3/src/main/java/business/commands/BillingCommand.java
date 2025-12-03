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
        Map<String, Object> summary = billingService.getBillingSummary(userId);

        request.setAttribute("billingSummary", summary);
        request.setAttribute("userName", session.getAttribute("userName"));
        request.setAttribute("userType", session.getAttribute("userType"));

        return "billing.jsp";
    }

    @Override
    public String getCommandName() {
        return "billing";
    }
}