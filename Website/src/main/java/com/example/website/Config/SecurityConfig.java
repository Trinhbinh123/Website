package com.example.website.Config;
import com.example.website.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
        http.csrf().disable()
                .authorizeRequests()
                // Chỉ admin mới truy cập được /admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Cho phép mọi người truy cập website, nhưng yêu cầu đăng nhập cho hành động mua hàng
                .requestMatchers("/cart").authenticated()
                .requestMatchers("/website","/detail/**").permitAll()
                // Cho phép truy cập không cần đăng nhập cho các trang login và register
                .requestMatchers("/login", "/register").permitAll()
                // Mọi yêu cầu khác đều cần phải đăng nhập
                .anyRequest().authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email") // Khớp với tên trường trong form
                        .passwordParameter("matKhau")
                        .defaultSuccessUrl("/website", true)
                        .failureUrl("/login?error") // Đường dẫn khi đăng nhập thất bại
                        .permitAll()
                );
        return http.build();
    }

}