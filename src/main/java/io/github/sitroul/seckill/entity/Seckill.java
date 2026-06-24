package io.github.sitroul.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

// 商品
@Data
public class Seckill {
    @TableId
    private Long seckillId;
    private String name;
    private Integer number;        // 库存
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
}