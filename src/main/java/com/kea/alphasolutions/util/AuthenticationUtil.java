package com.kea.alphasolutions.util;

import com.kea.alphasolutions.model.Resource;
import jakarta.servlet.http.HttpSession;

public class AuthenticationUtil {
    
    public static boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }
    
    public static boolean isAdmin(HttpSession session) {
        Resource user = (Resource) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getSystemRole());
    }
    
    public static Resource getCurrentUser(HttpSession session) {
        return (Resource) session.getAttribute("loggedInUser");
    }
}