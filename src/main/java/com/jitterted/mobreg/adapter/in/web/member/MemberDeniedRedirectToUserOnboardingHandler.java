package com.jitterted.mobreg.adapter.in.web.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * If the user authenticates and tries to access /member, but fails (they're not a member),
 * then redirect them to the /user page for on-boarding.
 */
public class MemberDeniedRedirectToUserOnboardingHandler extends AccessDeniedHandlerImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberDeniedRedirectToUserOnboardingHandler.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null &&
            auth.isAuthenticated()) {
            if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
                LOGGER.info("Authenticated, but not authorized: {}", oAuth2User.getAuthorities().toString());
                if (request.getRequestURI().contains("/member")) {
                    response.sendRedirect("/user");
                }
            }
        }
        super.handle(request, response, accessDeniedException);
    }
}
