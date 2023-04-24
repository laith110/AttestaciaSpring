package com.example.attestationspring.config;



import com.example.attestationspring.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig{

    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // конфигурируем работу Spring Security
       //csrf().disable()//отключаем защиту от межсайтовой подделки запросов

       http.authorizeHttpRequests()//указываем что все страницы должны быть защищены аутентификацией
               .requestMatchers("/admin").hasRole("ADMIN")//Указываем на то что страница /admin доступна пользователю с ролью ADMIN
               //указываем что не аутентифицированые пользователи могут зайти на страницу аутентификации и на объект ошибки
               // с помощью permitAll указыаем что не аутентифицированые пользователи могут заходить на перечисленные страницы
               .requestMatchers("/authentication","registration","/errors","resource/**","/static/**","/css/**","/js/**","/img/**","/product","/product/info/{id}","/product/search").permitAll()
               .anyRequest().hasAnyRole("USER","ADMIN")
               //указываем что для всех остальных страниц необходимо вызвать метод authenticated() который открывает форму анутентификации
//               .anyRequest().authenticated()
               .and()//указываем что дальще настраивается аутетнификация и соединяем её с настройкой доступа
               //указваем какой url запрос будет отправлятся при заходе на защищенные страницы
               .formLogin().loginPage("/product")
               .loginProcessingUrl("/process_login") //указываем на какой адрес будут отправляться данные с формы. Нам уже не нужено булет создавать метод в контроллере и обрабатывать данные с формы. Мы задали url , который используется по умолчанию для обработки формы аутетнификации по средствам Spring Security. Spring Security будет ждать объект с формы аутентификации и затем сверять логин и пароль с данными в БД
               .defaultSuccessUrl("/personal_account",true)//Указываем на какой url необходимо напрвить пользователя после успешной аутентификации. Вторым аргументом указывается true чтобы перенаправление шло в любом случае после успешной аутентификации
               .failureUrl("/authentication?error")//Указываем куда необходимо перенаправить пользователя при провальной аутентификации. В запрос будет передан объект error, который будет проверятся на форме и при наличии данного объекта в запросе выводится сообщение "Неправильный логин или пароль"
               .and()
               .logout().logoutUrl("/logout").logoutSuccessUrl("/product");

        return http.build();

    }

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
 authenticationManagerBuilder.userDetailsService(personDetailsService)
         .passwordEncoder(getPasswordEncoder());
    }
}
