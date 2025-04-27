package com.phungquocthai.symphony.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.Role;
import com.phungquocthai.symphony.dto.AuthenticationRequest;
import com.phungquocthai.symphony.dto.AuthenticationResponse;
import com.phungquocthai.symphony.dto.IntrospectRequest;
import com.phungquocthai.symphony.dto.IntrospectResponse;
import com.phungquocthai.symphony.dto.LogoutRequest;
import com.phungquocthai.symphony.dto.RefreshRequest;
import com.phungquocthai.symphony.entity.InvalidatedToken;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.repository.InvalidatedTokenRepository;
import com.phungquocthai.symphony.repository.SingerRepository;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SingerRepository singerRepository;
	
	@Autowired
	private InvalidatedTokenRepository invalidatedTokenRepository;
	
	@NonFinal
	@Value("${jwt.secretKey}")
	protected String SIGNER_KEY;
	
	@NonFinal
	@Value("${jwt.validDuration}")
	protected long VALID_DURATION;
	
	@NonFinal
	@Value("${jwt.refreshableDuration}")
	protected long REFRESHABLE_DURATION;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		String token = request.getToken();
		boolean valid = true;
		
		try {
			verifyToken(token, false);
		} catch (JOSEException | ParseException | AppException e) {
			valid = false;
		}
		
		return IntrospectResponse.builder()
				.valid(valid)
				.build();
	}
	
	public AuthenticationResponse refreshToken(RefreshRequest request) throws JOSEException, ParseException {
		SignedJWT signedJWT = verifyToken(request.getToken(), true);
		
		String jit = signedJWT.getJWTClaimsSet().getJWTID();
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		
		InvalidatedToken invalidatedToken = InvalidatedToken.builder()
				.id(jit)
				.expiryTime(expiryTime)
				.build();
		
		invalidatedTokenRepository.save(invalidatedToken);		
		
		String phone = signedJWT.getJWTClaimsSet().getSubject();
		User user = userRepository.findByPhone(phone).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
		
		String token = generateToken(user);
		
		return AuthenticationResponse.builder()
				.token(token)
				.authenticated(true)
				.build();
	}
	
	public void logout(LogoutRequest request) throws JOSEException, ParseException {
		try {
			SignedJWT signedJWT = verifyToken(request.getToken(), true);
			String jit = signedJWT.getJWTClaimsSet().getJWTID();
			Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
			
			InvalidatedToken invalidatedToken = InvalidatedToken.builder()
					.id(jit)
					.expiryTime(expiryTime)
					.build();
			
			invalidatedTokenRepository.save(invalidatedToken);
		} catch (AppException e) {
			log.info("Token already expired");
		}
	}
	
	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		
		SignedJWT signedJWT = SignedJWT.parse(token);
		
		Date expiryTime = (isRefresh) 
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
						.toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();
		
		boolean verified = signedJWT.verify(verifier);
		
		if(!(verified && expiryTime.after(new Date()))) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		 return signedJWT;
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest dto) {
		User user = userRepository.findByPhone(dto.getPhone())
		.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED));
				
		boolean authenticated = passwordEncoder.matches(dto.getPassword(), user.getPassword()); 		

		String token = "";
		if(authenticated) token = generateToken(user);

		return AuthenticationResponse.builder()
				.authenticated(authenticated)
				.token(token)
				.build();
	}
	
	public void grantSinger(Integer userId) {
		Singer singer = new Singer();
		User user = userRepository.findById(userId).orElseThrow();
		singer.setUser(user);
		singer.setStageName("Chưa có nghệ danh");

		singerRepository.save(singer);
		this.userRepository.updateUserRoleToSinger(userId);
	}
	
	private String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUserId() + "")
				.issuer("symphony.com")
				.issueTime(new Date())
				.expirationTime(new Date(
						Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
						))
				.jwtID(UUID.randomUUID().toString())
				.claim("phone", user.getPhone())
				.claim("scope", Role.fromValue(user.getRole()))
				.build();
		
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		
		JWSObject jwsObject = new JWSObject(header, payload);
		
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch(IllegalStateException | JOSEException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
