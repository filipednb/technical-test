package filipednb.github.com.hostfullyapi.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(@filipednb.github.com.hostfullyapi.logger.Loggable *)")
  public void loggableClasses() { }

  @Before("loggableClasses()")
  public void logMethodCall(JoinPoint joinPoint) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      var request = attributes.getRequest();
      var url = request.getRequestURL().toString();
      var params = request.getQueryString();
      LOGGER.info("Request URL: {}", url);
      if (params != null) {
        LOGGER.info("Request parameters: {}", params);
      }
    }
  }
}