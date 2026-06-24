package io.github.sitroul.seckill.dto;

import lombok.Data;

@Data
public class SeckillResult<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> SeckillResult<T> success(String message) {
        SeckillResult<T> result = new SeckillResult<>();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static <T> SeckillResult<T> success(String message, T data) {
        SeckillResult<T> result = new SeckillResult<>();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> SeckillResult<T> fail(String message) {
        SeckillResult<T> result = new SeckillResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}