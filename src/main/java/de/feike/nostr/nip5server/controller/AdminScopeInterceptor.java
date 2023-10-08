package de.feike.nostr.nip5server.controller;

import de.feike.nostr.nip5server.config.Nip5ServerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminScopeInterceptor implements HandlerInterceptor {
    private static final String N5S_SECRET_HEADER = "NIP5S_SECRET";

    private final Nip5ServerConfig nip5ServerConfig;
    private boolean isSecured = false;

    public AdminScopeInterceptor(Nip5ServerConfig nip5ServerConfig) {
        this.nip5ServerConfig = nip5ServerConfig;
        if (StringUtils.hasLength(nip5ServerConfig.getAdminSecret())) {
            isSecured = true;
        }

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isSecured) {
            return true;
        }
        String providedSecret = request.getHeader(N5S_SECRET_HEADER);
        if (!nip5ServerConfig.getAdminSecret().equals(providedSecret)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or wrong header " + N5S_SECRET_HEADER);
            return false;
        }
        return true;
    }
}
