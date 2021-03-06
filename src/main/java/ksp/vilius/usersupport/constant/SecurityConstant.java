package ksp.vilius.usersupport.constant;

public class SecurityConstant {
    public static final Long EXPIRATION_TIME = 432_000_000L; //5 Days in millisecs
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String GET_VILKSP_LLC = " vilksp,LLC";
    public static final String GET_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED = "You don't have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/api/user/login", "/api/user/register", "/api/user/resetpassword/**", "/api/user/image/**"};
}
