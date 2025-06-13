package com.ferdican.restaurantsystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("The username " + username + " is logged in.");
        boolean hasCustomerRole = authentication.getAuthorities().stream().
                anyMatch(r -> r.getAuthority().equals("customer"));
        boolean hasAdminRole = authentication.getAuthorities().stream().
                anyMatch(r -> r.getAuthority().equals("admin"));
        boolean hasWaiterRole = authentication.getAuthorities().stream().
                anyMatch(r -> r.getAuthority().equals("waiter"));

        if (hasAdminRole || hasCustomerRole || hasWaiterRole) {
            response.sendRedirect("/dashboard/");
        }
    }


}
