/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package icu.lowcoder.spring.cloud.authentication.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import icu.lowcoder.spring.cloud.authentication.EmptyPasswordEncoder;
import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.service.JpaUserDetailsService;
import icu.lowcoder.spring.commons.exception.security.AccessDeniedExceptionHandler;
import icu.lowcoder.spring.commons.exception.security.UnifiedExceptionAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private JwtProperties jwtProperties;

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.csrf().disable()
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((authorize) -> authorize
				.antMatchers("/login/**").permitAll()
				.antMatchers("/admin/accounts/**").hasAnyAuthority("commons_*_admin", "commons_accounts_admin")
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth2 -> oauth2
					.jwt(jwt -> jwt
							.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
					)
			)
			.exceptionHandling()
			.accessDeniedHandler(new AccessDeniedExceptionHandler())
			.authenticationEntryPoint(new UnifiedExceptionAuthenticationEntryPoint());
		// @formatter:on
		return http.build();
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = new RSAKey.Builder(jwtProperties.getPublicKey())
				.privateKey(jwtProperties.getPrivateKey())
				.keyID(jwtProperties.getKeyId())
				.build();

		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	UserDetailsService userDetailsService(AccountRepository accountRepository) {
		return new JpaUserDetailsService(accountRepository);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		String encodingId = "bcrypt";
		String defaultForMatches = "noop";

		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(encodingId, new BCryptPasswordEncoder());
		encoders.put("ldap", new LdapShaPasswordEncoder());
		encoders.put("MD4", new Md4PasswordEncoder());
		encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
		encoders.put("noop", NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder());
		encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
		encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
		encoders.put("sha256", new StandardPasswordEncoder());
		encoders.put("empty", new EmptyPasswordEncoder());

		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(defaultForMatches));

		return passwordEncoder;
	}

	Converter<Jwt, ? extends AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(source -> {
			String scope = (String) source.getClaims().getOrDefault("scope", "");
			if (StringUtils.hasText(scope)) {
				Collection<String> authorities = Arrays.asList(scope.split(" "));
				return authorities.stream()
						.filter(StringUtils::hasText)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
			} else {
				return Collections.emptyList();
			}
		});
		return jwtAuthenticationConverter;
	}

}
