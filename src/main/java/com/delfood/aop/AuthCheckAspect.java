package com.delfood.aop;

import javax.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@SuppressWarnings("unchecked")
public class AuthCheckAspect {
  @Autowired
  private ShopService shopService;
  
  /**
   * session에서 owner 로그인을 체크한다.
   * 로그인되어있지 않을 시 해당 메서드 로직을 중지시킨 후 리턴한다.
   * @OwnerLoginCheck 해당 어노테이션이 적용된 메서드를 검사한다.
   * @author jun
   * @param pjp
   * @return 로그인시 SUCCESS, 비로그인시 NO_LOGIN
   * @throws Throwable
   */
  @Before("@annotation(com.delfood.aop.OwnerLoginCheck)")
  public void ownerLoginCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Owner Login Check Started");
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if(ownerId == null) {
      log.debug("AOP - Owner Login Check Result - NO_LOGIN");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
  }
  
  
  /**
   * 세션에서 사장님 로그인을 체크 한다.
   * 그 후 입력받은 파라미터 값 중 매장 id를 검색하여 해당 매장이 접속한 사장님의 것인지 검사한다.
   * @author jun
   * @param pjp
   * @return 비로그인시 NO_LOGIN, 해당 매장의 사장이 아닐 시 UNAUTHORIZED, 권한이 있을 시 SUCCESS
   * @throws Throwable
   */
  @Before("@annotation(com.delfood.aop.OwnerShopCheck)")
  public void ownerShopCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Owner Shop Check Started");
    
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if(ownerId == null) {
      log.debug("AOP - Owner Shop Check Result - NO_LOGIN");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
    
    Object[] args = jp.getArgs();
    Long shopId = (Long) args[0];
    
    if (!shopService.isShopOwner(shopId, ownerId)) {
      log.debug("AOP - Owner Shop Check Result - UNAUTHORIZED");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED") {};
    }
  }
  
  /**
   * 고객의 로그인을 체크한다.
   * @author jun
   * @param pjp
   * @return
   * @throws Throwable
   */
  @Before("@annotation(com.delfood.aop.MemberLoginCheck)")
  public void memberLoginCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Member Login Check Started");
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String memberId = SessionUtil.getLoginMemberId(session);
    
    if (memberId == null) {
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
  }
}
