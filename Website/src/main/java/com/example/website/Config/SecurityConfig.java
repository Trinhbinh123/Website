package com.example.website.Config;
import com.example.website.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);  // Đăng ký dịch vụ lấy thông tin người dùng
        authenticationProvider.setPasswordEncoder(new NoOpPasswordEncoder());  // Không mã hóa mật khẩu (plaintext)
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/login", "/register").permitAll();
                    registry.requestMatchers("/website",
                            "/quenMk/**",
                            "/api/**",
                            "/detail/**",
                            "/static/src/website/css/**",
                            "/static/src/website/js/**",
                            "/static/src/website/css/**",
                            "/static/src/website/**",
                            "/static/src/website/lib/**").permitAll();
                    registry.requestMatchers("/cart").hasRole("USER");
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(authenticationSuccessHandler)
                            .permitAll();
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/logout") // URL để đăng xuất
                            .logoutSuccessUrl("/login") // URL sau khi đăng xuất thành công
                            .invalidateHttpSession(true) // Hủy phiên làm việc
                            .deleteCookies("JSESSIONID") // Xóa cookie phiên
                            .permitAll(); // Cho phép tất cả người dùng truy cập vào /logout
                })
                .build();
//        http.csrf().disable()
//                .authorizeRequests()
//                // Chỉ admin mới truy cập được /admin
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                // Cho phép mọi người truy cập website, nhưng yêu cầu đăng nhập cho hành động mua hàng
//                .requestMatchers("/cart").authenticated()
//                .requestMatchers("/website","/detail/**","/src/website/css/**", "/src/website/**","/src/website/**","/src/website/lib/**").permitAll()
//                // Cho phép truy cập không cần đăng nhập cho các trang login và register
//                .requestMatchers("/login", "/register").permitAll()
//                // Mọi yêu cầu khác đều cần phải đăng nhập
//                .anyRequest().authenticated()
//                .and()
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(authenticationSuccessHandler)
//                        .permitAll()
//                );
//        return http.build();
    }

}