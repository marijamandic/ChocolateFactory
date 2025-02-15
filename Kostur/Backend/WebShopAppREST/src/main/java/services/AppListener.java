package services;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import dao.FactoryDAO;
import dao.ChocolateDAO;

@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {

        String contextPath = event.getServletContext().getRealPath("");

        // Inicijalizujemo FactoryDAO
        FactoryDAO factoryDAO = new FactoryDAO(contextPath);
        event.getServletContext().setAttribute("factoryDAO", factoryDAO);

        // Inicijalizujemo ChocolateDAO
        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
        event.getServletContext().setAttribute("chocolateDAO", chocolateDAO);

    }

}
