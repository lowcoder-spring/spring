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

package icu.lowcoder.spring.cloud.authentication.web;

import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.security.self.AuthenticatedUser;
import icu.lowcoder.spring.cloud.authentication.security.self.SelfGrantedToken;
import icu.lowcoder.spring.cloud.authentication.security.self.SelfGrantedTokenManager;
import icu.lowcoder.spring.cloud.authentication.web.model.PasswordLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/login")
@RestController
public class LoginController {

	@Autowired
	private SelfGrantedTokenManager selfGrantedTokenManager;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/password")
	public SelfGrantedToken passwordLogin(@RequestBody PasswordLoginRequest request) {
		if (!StringUtils.hasText(request.getUsername())) {
			throw new UsernameNotFoundException("用户名不能为空");
		}

		Account account = accountRepository.findByPhoneOrEmail(request.getUsername(), request.getUsername());
		if (account == null) {
			throw new UsernameNotFoundException("用户名或密码错误");
		}

		if (!StringUtils.hasText(request.getPassword())) {
			throw new AuthenticationCredentialsNotFoundException("密码不能为空");
		}

		if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
			throw new BadCredentialsException("用户名或密码错误");
		}

		return selfGrantedTokenManager.grant(AuthenticatedUser.create(account));
	}

}
