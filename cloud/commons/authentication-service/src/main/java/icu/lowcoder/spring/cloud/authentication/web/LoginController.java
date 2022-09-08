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

import icu.lowcoder.spring.cloud.authentication.Constants;
import icu.lowcoder.spring.cloud.authentication.config.AuthProperties;
import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.dict.SmsCodeCategory;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.security.self.AuthenticatedUser;
import icu.lowcoder.spring.cloud.authentication.security.self.SelfGrantedToken;
import icu.lowcoder.spring.cloud.authentication.security.self.SelfGrantedTokenManager;
import icu.lowcoder.spring.cloud.authentication.service.SmsCodeManager;
import icu.lowcoder.spring.cloud.authentication.web.model.PasswordLoginRequest;
import icu.lowcoder.spring.cloud.authentication.web.model.SendSmsCodeRequest;
import icu.lowcoder.spring.cloud.authentication.web.model.SmsLoginRequest;
import icu.lowcoder.spring.cloud.authentication.web.model.SmsRegisterRequest;
import icu.lowcoder.spring.commons.robot.RobotVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

	@Autowired
	private SelfGrantedTokenManager selfGrantedTokenManager;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RobotVerifier robotVerifier;
	@Autowired
	private AuthProperties authProperties;
	@Autowired
	private SmsCodeManager smsCodeManager;

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

	@PostMapping("/sms-code")
	public void sendSmsCode(@RequestBody SendSmsCodeRequest request, @RequestParam Map<String, String> parameters, HttpServletRequest httpServletRequest) {
		if (!StringUtils.hasText(request.getPhone())) {
			throw new UsernameNotFoundException("手机号不能为空");
		}

		if (!robotVerifier.allow(httpServletRequest, parameters)) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "获取短信验证码失败，请重试。");
		}

		boolean existByPhone = accountRepository.existsByPhone(request.getPhone());
		// 登陆时未开启自动注册判断手机号
		if (request.getCategory().equals(SmsCodeCategory.LOGIN) && !authProperties.getSmsAutoRegister()) {
			if (!existByPhone) {
				throw new UsernameNotFoundException("手机号未注册");
			}
		}

		// 注册时判断手机状态
		/*if (request.getCategory().equals(SmsCodeCategory.REGISTER) && existByPhone) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "手机号已注册");
		}*/

		smsCodeManager.send(request.getCategory(), request.getPhone());
	}

	@PostMapping("/sms")
	@Transactional
	public SelfGrantedToken smsLogin(@RequestBody SmsLoginRequest request) {
		if (!StringUtils.hasText(request.getPhone())) {
			throw new UsernameNotFoundException("手机号不能为空");
		}
		if (!StringUtils.hasText(request.getSmsCode())) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "短信验证码不能为空");
		}

		try {
			smsCodeManager.verify(SmsCodeCategory.LOGIN, request.getPhone(), request.getSmsCode());
		} catch (Exception e) {
			throw new BadCredentialsException(e.getMessage());
		}

		// 验证通过
		Account account = accountRepository.findByPhone(request.getPhone())
				.orElse(null);

		if (account == null) {
			if (!authProperties.getSmsAutoRegister()) {
				throw new UsernameNotFoundException("手机号未注册");
			}

			account = new Account();
			account.setPhone(request.getPhone());
			account.setRegisterTime(new Date());
			account.setPassword(Constants.EMPTY_ENCODED_PASSWORD);
			account.setEnabled(true);
			account.setName("手机用户" + request.getPhone().substring(request.getPhone().length() - 4));
			accountRepository.save(account);
		}

		return selfGrantedTokenManager.grant(AuthenticatedUser.create(account));
	}

	@PostMapping("/sms-register")
	@Transactional
	public SelfGrantedToken smsRegister(@RequestBody SmsRegisterRequest request) {
		if (!StringUtils.hasText(request.getPhone())) {
			throw new UsernameNotFoundException("手机号不能为空");
		}
		if (!StringUtils.hasText(request.getSmsCode())) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "短信验证码不能为空");
		}
		// 注册时判断手机状态
		/*if (accountRepository.existsByPhone(request.getPhone())) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "手机号已注册");
		}*/

		try {
			smsCodeManager.verify(SmsCodeCategory.REGISTER, request.getPhone(), request.getSmsCode());
		} catch (Exception e) {
			throw new BadCredentialsException(e.getMessage());
		}

		// 已注册用户直接返回
		Account account = accountRepository.findByPhone(request.getPhone())
				.orElse(null);
		if (account == null) {
			account = new Account();
			account.setPhone(request.getPhone());
			account.setRegisterTime(new Date());
			account.setName(request.getName());
			account.setPassword(Constants.EMPTY_ENCODED_PASSWORD);
			account.setEnabled(true);
			if (!StringUtils.hasText(account.getName())) {
				account.setName("手机用户" + request.getPhone().substring(request.getPhone().length() - 4));
			}
			accountRepository.save(account);
		}

		return selfGrantedTokenManager.grant(AuthenticatedUser.create(account));
	}

}
