package io.github.sitroul.seckill.exception;

import io.github.sitroul.seckill.dto.SeckillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public SeckillResult<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return SeckillResult.fail("系统繁忙，请稍后再试");
    }
}
