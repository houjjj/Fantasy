package com.houjun.jwtdemo.config;

import com.houjun.jwtdemo.filter.JwtFilter;
import com.houjun.jwtdemo.filter.JwtLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
       return new BCryptPasswordEncoder();
    }

    //配置用户来源
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("houjun").password("$2a$10$diQUBpaqtw2TKMrcfl/baeXeh2iuFzzM3f.T2WwYpr20cTw31nFu.").roles("admin")
                .and()
                .withUser("zhangsan").password("$2a$10$W9O7E9R8ATIRTRZ1K552j.NceKCBhA/b8pD1qawMaxpiwUUcAzVeC").roles("user")
                .and();

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/*")
                .hasRole("admin")//admin/*请求需要有admin角色
                .antMatchers("/user/*")
                .hasRole("user")
//                .antMatchers("/doLogin").permitAll()//允许doLogin
                .anyRequest().authenticated()//任何请求都需要授权后访问
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
//                        resp.setContentType("text/application;charset=utf8");
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        UserDetails principal = (UserDetails) authentication.getPrincipal();
//                        String username = principal.getUsername();
//                        Map<String, String> map = new HashMap<>();
//                        map.put("msg", "登录成功！");
//                        map.put("username", username);
//                        map.put("auth", Arrays.deepToString(principal.getAuthorities().toArray()));
//                        map.put("code", "200");
//                        String s = objectMapper.writeValueAsString(map);
//                        PrintWriter writer = resp.getWriter();
//                        writer.write(s);
//                        writer.flush();
//                        writer.close();
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
//                        Map<String, String> map = new HashMap<>();
//                        map.put("msg", "登录失败！");
//                        resp.setContentType("text/application;charset=utf8");
//                        PrintWriter writer = resp.getWriter();
//                        writer.write(new ObjectMapper().writeValueAsString(map));
//                        writer.flush();
//                        writer.close();
//                    }
//                })
                .and()
                //登录校验的逻辑交给JwtLoginFilter
                .addFilterBefore(new JwtLoginFilter("/doLogin",authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(),UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }
}
